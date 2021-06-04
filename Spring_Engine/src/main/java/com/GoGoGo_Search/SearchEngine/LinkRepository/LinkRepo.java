package com.GoGoGo_Search.SearchEngine.LinkRepository;

import com.GoGoGo_Search.SearchEngine.document.Link;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LinkRepo extends MongoRepository<Link, ObjectId> {

    @Query("{'key':?0}")
    List<Link> findByKey(String key);

}
