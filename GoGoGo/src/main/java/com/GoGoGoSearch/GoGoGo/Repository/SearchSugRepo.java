package com.GoGoGoSearch.GoGoGo.Repository;

import com.GoGoGoSearch.GoGoGo.document.Link;
import com.GoGoGoSearch.GoGoGo.document.searchSuggWords;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface SearchSugRepo extends MongoRepository<searchSuggWords,Integer> {



}
