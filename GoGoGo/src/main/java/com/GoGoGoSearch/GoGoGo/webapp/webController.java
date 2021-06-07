package com.GoGoGoSearch.GoGoGo.webapp;


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
    private final LinkRepo linkRepo;

    public webController(LinkRepo linkRepo) {
        this.linkRepo = linkRepo;
    }

    public String viewHomeScreen(Model model) {
        return "Index";
    }

    @GetMapping(path="/getData/{word}")
    public String getSuggest(@PathVariable("word") String word){
        return "hello from server";//return the data as json
    }
    @GetMapping("/search")
    public String viewResultScreen(@ModelAttribute("searchSentence") String searchSentence, @ModelAttribute("mode") String mode, Model model) {
        MyJSONdoc results = getLinks(searchSentence, mode);

        if (results == null) {
//            model.addAttribute("keys","0");
//            model.addAttribute("size","0");
//            model.addAttribute("URLS","0");

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
        //   according to the mode we perform the proper search query query
        if (mode.equals("Or")) {
            //key1-> url1 url5 url3
            //key2-> url5 url3 url7
            //result-> url1 url5 url3 url7

            ArrayList<Document> URLSOrMode = new ArrayList<>();


            for (String word : searchWords) {
                try {
                    URLSOrMode = linkRepo.findByKey(word).get(0).getURLS();
                    URLS.addAll(URLSOrMode);
                    keys.add(word);
                } catch (Exception e) {
                    keys.add(word);
                }

            }


        } else if (mode.equals("And")) {
            //key1-> url1 url5 url3
            //key2-> url5 url3 url7
            //result-> url5 url3 url7

            HashMap<Document, Long> URLSAndMode = new HashMap<>();

            for (String word : searchWords) {
                keys.add(word);

                ArrayList<Document> queryResult;
                try {
                    queryResult = linkRepo.findByKey(word).get(0).getURLS();
                } catch (Exception e) {
                    return null;
                }

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

        MyJSONdoc results = new MyJSONdoc(keys, URLS, URLS.size());

        if (results.getSize() == 0) {
            return null;
        }
        return results;
    }

}
