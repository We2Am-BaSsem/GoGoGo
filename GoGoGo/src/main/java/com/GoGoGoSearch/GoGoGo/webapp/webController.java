package com.GoGoGoSearch.GoGoGo.webapp;


import ca.rmen.porterstemmer.PorterStemmer;
import com.GoGoGoSearch.GoGoGo.Repository.LinkRepo;
import com.GoGoGoSearch.GoGoGo.document.MyJSONdoc;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import com.GoGoGoSearch.GoGoGo.DBService.DBService.*;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.HashMap;
@Controller
public class webController {


    @Autowired
    private final LinkRepo linkRepo; // defining the linkRepo which contains the database operations

    // constructor
    public webController(LinkRepo linkRepo) {
        this.linkRepo = linkRepo;
    }

    // view home page
    public String viewHomeScreen(Model model) {
        return "Index";
    } // Index-> is the name of the html file


    // show the results in the result page, takes the search sentence and the search mode
    @GetMapping("/search")
    public String viewResultScreen(@ModelAttribute("searchSentence") String searchSentence, @ModelAttribute("mode") String mode, Model model) {
        MyJSONdoc results = getLinks(searchSentence, mode);

        if (results == null) {
            return "empty";
        } else {
            String s = "";
            for (int i = 0; i < results.getKey().size(); i++) {
                s += results.getKey().get(i);

                s += " ";
            }
            model.addAttribute("keys", s);
            model.addAttribute("size", results.getSize());
            model.addAttribute("URLS", results.getURLS());

        }
        return "result";

    }

    public MyJSONdoc getLinks(String searchSentence, String mode) {

        //  here we split the sentence into words that are used for search process
        String[] searchWords = searchSentence.split(" ");


        ArrayList<String> keys = new ArrayList<>();     //this list should store the list of words we are looking for
        ArrayList<Document> URLS = new ArrayList<>();   //this list should store the list of returned documents from the database,
        // each document contains the URL, Title, Description

        if (searchWords.length == 0) {
            return null;
        }
        //   according to the mode we perform the proper search query
        //key1-> url1 url5 url3
        //key2-> url5 url3 url7
        //result-> url1 url5 url3 url7
        if (mode.equals("Or")) {
            ArrayList<Document> URLSOrMode = new ArrayList<>();
            for (String word : searchWords) {
                try {
                    PorterStemmer ps = new PorterStemmer();
                    String searchKey = ps.stemWord(word);
                    URLSOrMode = linkRepo.findByKey(searchKey).get(0).getURLS();
                    URLS.addAll(URLSOrMode);
                    keys.add(word);
                } catch (Exception e) {
                    keys.add(word);
                }
            }
        }
        //key1-> url1 url5 url3
        //key2-> url5 url3 url7
        //result-> url5 url3 url7
        else if (mode.equals("And")) {
            HashMap<Document, Long> URLSAndMode = new HashMap<>();
            for (String word : searchWords) {
                keys.add(word);
                ArrayList<Document> queryResult;
                try {
                    PorterStemmer ps = new PorterStemmer();
                    String searchKey = ps.stemWord(word);
                    queryResult = linkRepo.findByKey(searchKey).get(0).getURLS();
                } catch (Exception e) {
                    return null;
                }
                for (Document document : queryResult) {
                    if (URLSAndMode.containsKey(document)) {
                        URLSAndMode.put(document, URLSAndMode.get(document) + 1);
                        URLS.add(document);
                    } else {
                        URLSAndMode.put(document, 1L);
                    }
                }
            }
        }

        MyJSONdoc results = new MyJSONdoc(keys, URLS, URLS.size());

        if (results.getSize() == 0) {
            return null;
        }
        return results;
    }

}
