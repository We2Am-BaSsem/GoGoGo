package com.GoGoGo_Search.SearchEngine.resource;

import com.GoGoGo_Search.SearchEngine.LinkRepository.LinkRepo;
import com.GoGoGo_Search.SearchEngine.document.*;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest/links")
public class LinkResource {

    @Autowired
    private LinkRepo linkRepo;

    public LinkResource(LinkRepo linkRepo) {
        this.linkRepo = linkRepo;
    }

//    searchfield=play+men+&mode=Or
//    searchfield=play+men+&mode=And
    @RequestMapping(method = RequestMethod.GET ,path = "/findKey")
    public myJSONdoc getAll(@RequestParam(defaultValue = "") String searchkey) {

        //
        searchkey = searchkey.replaceAll("\\+", ",");
        List<String> searchWords = Arrays.asList(searchkey);

        System.out.println("\nI was searching for: " +searchkey);

        String mode = "Or";//searchkey.substring(searchkey.indexOf('='));
        System.out.println("I was in mode: " +mode);
        //
        ArrayList<String> keys = new ArrayList<>();
        ArrayList<Document> URLS = new ArrayList<>();
        ArrayList<Document> temp = new ArrayList<>();
        //
        if(mode.equals("Or"))
        {
            for (String word: searchWords) {
                System.out.println("Cuurent Word: " + word);
                temp = linkRepo.findByKey(word).get(0).getURLS();
                System.out.println("URLs of: " + word + temp);
                URLS.addAll(temp);
                keys.add(word);
            }
        }
        else if(mode.equals("And"))
        {

        }


        System.out.println("comming Nowwwwwwwww");
        myJSONdoc doc = new myJSONdoc(keys,URLS, URLS.size());

        System.out.println("I was searching for: " + doc.getKey());
        System.out.println("list size is: "+doc.getSize());
        System.out.println("URLs: "+doc.getURLS());

        return doc;
    }
}
