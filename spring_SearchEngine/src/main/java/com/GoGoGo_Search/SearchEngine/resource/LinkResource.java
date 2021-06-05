package com.GoGoGo_Search.SearchEngine.resource;

import com.GoGoGo_Search.SearchEngine.LinkRepository.LinkRepo;
import com.GoGoGo_Search.SearchEngine.document.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/rest/links")
public class LinkResource {

    @Autowired
    private final LinkRepo linkRepo;

    public LinkResource(LinkRepo linkRepo) {
        this.linkRepo = linkRepo;
    }


    @RequestMapping(method = RequestMethod.GET, path = "/findKey")
    public myJSONdoc getAll(@RequestParam(defaultValue = "") String searchSentence, String mode) {

        //  here we split the sentence into words that are used for search process
        String[] searchWords = searchSentence.split(" ");


        ArrayList<String> keys = new ArrayList<>();     //this list should store the list of words we are looking for
        ArrayList<Document> URLS = new ArrayList<>();   //this list should store the list of returned documents from the database,
                                                        // each document contains the URL, Title, Description


        //   according to the mode we perform the proper search query query
        if (mode.equals("Or")) {
            //key1-> url1 url5 url3
            //key2-> url5 url3 url7
            //result-> url1 url5 url3 url7

            ArrayList<Document> URLSOrMode = new ArrayList<>();

            for (String word : searchWords) {
                URLSOrMode = linkRepo.findByKey(word).get(0).getURLS();
                URLS.addAll(URLSOrMode);
                keys.add(word);
            }
        } else if (mode.equals("And")) {
            //key1-> url1 url5 url3
            //key2-> url5 url3 url7
            //result-> url5 url3

            HashMap<Document, Long> URLSAndMode = new HashMap<>();

            for (String word : searchWords) {
                ArrayList<Document> queryResult = linkRepo.findByKey(word).get(0).getURLS();

                for (int i = 0; i < queryResult.size(); i++) {
                    if (URLSAndMode.containsKey(queryResult.get(i))) {
                        URLSAndMode.put(queryResult.get(i), URLSAndMode.get(queryResult.get(i)) + 1);
                        URLS.add(queryResult.get(i));
                    } else {
                        URLSAndMode.put(queryResult.get(i), 1L);
                    }
                }
            }
        }

        myJSONdoc doc = new myJSONdoc(keys, URLS, URLS.size());

        return doc;
    }
}
