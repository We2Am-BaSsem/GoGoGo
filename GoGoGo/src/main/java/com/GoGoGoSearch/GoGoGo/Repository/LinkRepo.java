package com.GoGoGoSearch.GoGoGo.Repository;

import com.GoGoGoSearch.GoGoGo.document.Link;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkRepo extends MongoRepository <Link,Integer> {

    @Query("{'key':?0}")
    List<Link> findByKey(String key);



}
