package com.GoGoGo_Search.SearchEngine.document;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Document(collection = "Indexer")
public class Link {

    @Id
    private org.bson.types.ObjectId ID;
    private String key;
    private Integer DF;
    private ArrayList<org.bson.Document> URLS;

    public Link() {
        this.ID = ID;
        this.key = key;
        this.DF = DF;
        this.URLS = URLS;
    }

    public ObjectId getID() {
        return ID;
    }

    public String getKey() {
        return key;
    }

    public Integer getDF() {
        return DF;
    }

    public ArrayList<org.bson.Document> getURLS() {
        return URLS;
    }
}
