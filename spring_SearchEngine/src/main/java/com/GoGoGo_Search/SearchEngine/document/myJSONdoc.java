package com.GoGoGo_Search.SearchEngine.document;

import org.bson.Document;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;

@org.springframework.data.mongodb.core.mapping.Document(collection = "Indexer")
public class myJSONdoc {
    public myJSONdoc(ArrayList<String> keys, ArrayList<Document> URLS, int size) {
        this.keys = keys;
        this.URLS = URLS;
        this.size = size;
    }

    @Id
    private ArrayList<String> keys;
    private ArrayList<Document> URLS;
    private int size;

    public ArrayList<String> getKey() {
        return keys;
    }

    public void setKey(ArrayList<String> key) {
        this.keys = keys;
    }

    public ArrayList<Document> getURLS() {
        return URLS;
    }

    public void setURLS(ArrayList<Document> URLS) {
        this.URLS = URLS;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
