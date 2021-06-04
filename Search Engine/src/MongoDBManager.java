import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import java.lang.Math;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

public class MongoDBManager {
    // first we store the connection string to connect with database
    // then we create a client
    // static String connectionString =
    // "mongodb+srv://SearchEngine:SearchEngine123456@crawler.sajqt.mongodb.net/admin";
    // static MongoClientURI clientURI = new MongoClientURI(connectionString);
    // static MongoClient mongoClient = new MongoClient(clientURI);

    static MongoClient mongoClient = new MongoClient("localhost", 27017);

    public static void main(String[] args) {
        try {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("GoGoGo_Search");
            MongoCollection collection = mongoDatabase.getCollection("Visited");
            collection.deleteMany(new Document());
            collection = mongoDatabase.getCollection("Crawler");
            collection.deleteMany(new Document());
            collection = mongoDatabase.getCollection("ToBeVisited");
            collection.deleteMany(new Document());

            // MongoDBManager manager = new MongoDBManager();
            // manager.insertIntobeVisited("https://Raz3.com");
            // manager.insertIntobeVisited("https://naksh.com");
            // manager.insertIntobeVisited("https://hh.com");
            // manager.insertIntobeVisited("https://lol.com");

            // Document doc = manager.retrieveFrombeVisited();
            // System.out.println("\n\n\n"+doc.get("URL")+"\n\n\n");
            // manager.deleteFrombeVisited(doc.get("URL").toString());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    int insertIntoCrawler(String URL, String title, String HTML_doc, String description) {
        try {
            // we access to the collection that should store the crawled pages data

            // MongoDatabase mongoDatabase = mongoClient.getDatabase("Crawler");
            // MongoCollection collection = mongoDatabase.getCollection("CrawledPages");

            MongoDatabase mongoDatabase = mongoClient.getDatabase("GoGoGo_Search");
            MongoCollection collection = mongoDatabase.getCollection("Crawler");

            // we prepare the document that should be inserted
            Document document = new Document("URL", URL);
            document.append("title", title);
            document.append("HTML_Document", HTML_doc);
            document.append("Description", description);

            collection.insertOne(document);

            return 0;
        } catch (Exception e) {
            // System.out.println(e.getMessage());
            return -1;
        }
    }

    int insertIntobeVisited(String URL) {
        try {
            // we access to the collection that should store the crawled pages data

            // MongoDatabase mongoDatabase = mongoClient.getDatabase("Crawler");
            // MongoCollection collection = mongoDatabase.getCollection("PagesToBeVisited");

            MongoDatabase mongoDatabase = mongoClient.getDatabase("GoGoGo_Search");
            MongoCollection collection = mongoDatabase.getCollection("ToBeVisited");

            // we prepare the document that should be inserted
            Document document = new Document("URL", URL);

            collection.insertOne(document);

            return 0;
        } catch (Exception e) {
            // System.out.println(e.getMessage());
            return -1;
        }
    }

    int insertIntoVisited_pages(String URL) {
        try {
            // we access to the collection that should store the crawled pages data

            // MongoDatabase mongoDatabase = mongoClient.getDatabase("Crawler");
            // MongoCollection collection = mongoDatabase.getCollection("PagesToBeVisited");

            MongoDatabase mongoDatabase = mongoClient.getDatabase("GoGoGo_Search");
            MongoCollection collection = mongoDatabase.getCollection("Visited");

            // we prepare the document that should be inserted
            Document document = new Document("URL", URL);

            collection.insertOne(document);

            return 0;
        } catch (Exception e) {
            // System.out.println(e.getMessage());
            return -1;
        }
    }

    int deleteFrombeVisited(String URL) {
        try {
            // we access to the collection that should store the crawled pages data

            // MongoDatabase mongoDatabase = mongoClient.getDatabase("Crawler");
            // MongoCollection collection = mongoDatabase.getCollection("PagesToBeVisited");

            MongoDatabase mongoDatabase = mongoClient.getDatabase("GoGoGo_Search");
            MongoCollection collection = mongoDatabase.getCollection("ToBeVisited");

            Document document = new Document("URL", URL);
            collection.deleteOne(document);

            return 0;
        } catch (Exception e) {
            // System.out.println(e.getMessage());
            return -1;
        }
    }

    Document retrieveFrombeVisited() {
        try {
            // we access to the collection that should store the crawled pages data

            // MongoDatabase mongoDatabase = mongoClient.getDatabase("Crawler");
            // MongoCollection collection = mongoDatabase.getCollection("PagesToBeVisited");

            MongoDatabase mongoDatabase = mongoClient.getDatabase("GoGoGo_Search");
            MongoCollection collection = mongoDatabase.getCollection("ToBeVisited");

            return (Document) (collection.find().first());

        } catch (Exception e) {
            // System.out.println(e.getMessage());
            return null;
        }
    }

    int insertIntoIndexer(HashMap<String, ArrayList<String>> indexer) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("GoGoGo_Search");
        MongoCollection collection = mongoDatabase.getCollection("Crawler");
        Long result = collection.count();
        System.out.println(result);
        collection = mongoDatabase.getCollection("Indexer");

        List<Document> docs = new ArrayList<Document>();
        try {
            Integer index = 0;
            for (String key : indexer.keySet()) {
                try {
                    Document document = new Document("key", key);
                    document.append("IDF", indexer.get(key).size());
                    Document URLTFdoc = new Document();
                    List<Document> documents = new ArrayList<Document>();
                    for (int i = 0; i < indexer.get(key).size(); i++) {
                        try {
                            String data = indexer.get(key).get(i);
                            if (data.contains("->title:") && data.contains("->desc:") && data.contains("->TF")) {
                                int indexTitle = data.indexOf("->title:");
                                int indexDescription = data.indexOf("->desc:");
                                int indexTF = data.indexOf("->TF");
                                // System.out.println(data);
                                // System.out.println(indexTitle);
                                // System.out.println(indexDescription);
                                // System.out.println(indexTF);

                                String url = data.substring(0, indexTitle);
                                String title = data.substring(indexTitle + 8, indexDescription);
                                String description = data.substring(indexDescription + 7, indexTF);
                                String TF = data.substring(indexTF + 4, data.length());
                                URLTFdoc.append("URL", url);
                                URLTFdoc.append("TF", Integer.parseInt(TF));
                                URLTFdoc.append("title", title);
                                URLTFdoc.append("Description", description);
                                documents.add(URLTFdoc);
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage() + "------------->here3\n");
                        }
                    }
                    try {
                        document.append("URLs", documents);
                        System.out.println(index++);
                        if (collection.find((new Document()).append("key", key)).iterator().hasNext()) {

                        } else {
                            docs.add(document);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage() + "------------->here4\n");
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage() + "------------->here2\n");
                    System.out.println("key: " + key);
                    System.out.println("DF: " + indexer.get(key).size());

                }
            }

            try {
                collection.insertMany(docs);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                try {
                    for (Document document : docs) {
                        try {
                            collection.insertOne(document);
                        } catch (Exception e4) {
                            System.out.println(e4.getMessage());
                        }
                    }
                } catch (Exception e2) {
                    System.out.println(e2.getMessage());
                }
            }
            return 0;
        } catch (Exception e) {
            System.out.println(e.getMessage() + "------------->here1\n");
            try {
                collection.insertMany(docs);
            } catch (Exception e2) {
                System.out.println(e2.getMessage());
                try {
                    for (Document document : docs) {
                        collection.insertOne(document);
                    }
                } catch (Exception e3) {
                    System.out.println(e3.getMessage());
                }
            }
            return -1;
        }

    }

    void insertIntoIndexer2(Hashtable<String, List<Document>> indexer) {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("GoGoGo_Search");
        MongoCollection collection = mongoDatabase.getCollection("Crawler");

        Long documentsNumber = collection.count();
        collection = mongoDatabase.getCollection("Indexer");

        try {
            Long start = System.currentTimeMillis();

            List<Document> docs = new ArrayList<Document>();
            try {
                Integer i = 0;
                for (String key : indexer.keySet()) {
                    try {
                        if (!collection.find((new Document()).append("key", key)).iterator().hasNext()) {
                            System.out.println(i++);
                            Document tempDoc = new Document("key", key);
                            double IDF = Math.log((1.0 * documentsNumber) / indexer.get(key).size());
                            tempDoc.append("IDF", IDF);
                            tempDoc.append("URLS", indexer.get(key));
                            try {
                                collection.insertOne(tempDoc);
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            // docs.add(tempDoc);
                        } else {
                            // Document fDocument = new Document("key", key);
                            // Document tempDoc = new Document();
                            // // tempDoc.append("DF", indexer.get(key).size());
                            // tempDoc.append("URLS", indexer.get(key));
                            // int size = indexer.get(key).size();
                            // for (int j = 0; j < size; j++) {
                            // BasicDBObject updateQuery = new BasicDBObject("$addToSet",
                            // new BasicDBObject("URLS", indexer.get(key).get(j)));
                            // collection.findOneAndUpdate(fDocument, updateQuery);
                            // }

                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            // try {
            // collection.insertMany(docs);
            // } catch (Exception e) {
            // System.out.println(e.getMessage());
            // }
            Long end = System.currentTimeMillis();
            System.out.println("\n******************************\nIndexer Insertion Time: " + (end - start) / 1000
                    + "s\n******************************\n");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    FindIterable<Document> retrieveFromCrawler() {
        try {
            // MongoDatabase mongoDatabase = mongoClient.getDatabase("Crawler");
            // MongoCollection collection = mongoDatabase.getCollection("CrawledPages");

            MongoDatabase mongoDatabase = mongoClient.getDatabase("GoGoGo_Search");
            MongoCollection collection = mongoDatabase.getCollection("Crawler");

            return collection.find();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    void CloseConnection() {
        mongoClient.close();
    }
}