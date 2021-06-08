package com.GoGoGoSearch.GoGoGo.document;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "searchSug")
public class searchSuggWords {

    private String searchSentence;

    public searchSuggWords(String searchSentence) {
        this.searchSentence = searchSentence;
    }

    public String getSearchSentence() {
        return searchSentence;
    }
}

