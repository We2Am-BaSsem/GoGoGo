package com.GoGoGoSearch.GoGoGo.DBService;

import com.GoGoGoSearch.GoGoGo.Repository.LinkRepo;
import com.GoGoGoSearch.GoGoGo.document.MyJSONdoc;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;

public class DBService {


    @Autowired
    private final LinkRepo linkRepo;

    public DBService(LinkRepo linkRepo) {
        this.linkRepo = linkRepo;
    }
}
