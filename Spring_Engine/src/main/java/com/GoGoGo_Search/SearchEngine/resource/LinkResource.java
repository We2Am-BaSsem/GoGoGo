package com.GoGoGo_Search.SearchEngine.resource;

import com.GoGoGo_Search.SearchEngine.LinkRepository.LinkRepo;
import com.GoGoGo_Search.SearchEngine.document.Link;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/allurls")
    public List<Link> getAll() {
        return linkRepo.findAll();
    }

    @RequestMapping(method = RequestMethod.GET ,path = "/findKey")
    public List<Link> getAll(@RequestParam(defaultValue = "") String searchkey) {
        List<Link> temp = linkRepo.findByKey(searchkey);
        System.out.println(temp.get(0).getID());
        System.out.println(temp.get(0).getKey());
        System.out.println(temp.get(0).getDF());
        System.out.println(temp.get(0).getURLS());
        System.out.println(temp.get(0).getURLS().size());
        System.out.println(temp.get(0).getURLS().get(0).get("URL"));
        System.out.println(temp.get(0).getURLS().get(0).get("Title"));
        System.out.println(temp.get(0).getURLS().get(0).get("Description"));
        System.out.println(temp.get(0).getURLS().get(0).get("TF"));



        //System.out.println(temp.get(0).getURLS().get(1));
        //System.out.println(temp.get(0).getURLS().get(2));


        return temp;
    }
}
