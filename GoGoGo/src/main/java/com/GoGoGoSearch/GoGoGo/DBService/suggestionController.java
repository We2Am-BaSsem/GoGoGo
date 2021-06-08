package com.GoGoGoSearch.GoGoGo.DBService;

import com.GoGoGoSearch.GoGoGo.Repository.SearchSugRepo;
import com.GoGoGoSearch.GoGoGo.document.searchSuggWords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class suggestionController {


    private SearchSugRepo searchSugRepo;

    public suggestionController(SearchSugRepo searchSugRepo) {
        this.searchSugRepo = searchSugRepo;
    }


    public void addSentence(searchSuggWords searchSuggWords){
        searchSugRepo.insert(searchSuggWords);
    }
}
