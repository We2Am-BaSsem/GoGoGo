import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

public class MongoDBManager {
    // first we store the connection string to connect with database
    // then we create a client
    static String connectionString = "mongodb+srv://SearchEngine:SearchEngine123456@crawler.sajqt.mongodb.net/admin";
    static MongoClientURI clientURI = new MongoClientURI(connectionString);
    static MongoClient mongoClient = new MongoClient(clientURI);

    // public static void main(String[] args) {
    // try {
    // MongoDatabase mongoDatabase = mongoClient.getDatabase("Crawler");
    // MongoCollection collection = mongoDatabase.getCollection("Inexer");
    // Document document = new Document();
    // collection.deleteMany(document);
    // } catch (Exception e) {
    // System.out.println(e.getMessage());
    // }
    // }

    int insertIntoCrawler(String URL, String title, String HTML_doc, String description) {
        try {
            // we access to the collection that should store the crawled pages data
            MongoDatabase mongoDatabase = mongoClient.getDatabase("Crawler");
            MongoCollection collection = mongoDatabase.getCollection("CrawledPages");

            // we prepare the document that should be inserted
            Document document = new Document("URL", URL);
            document.append("title", title);
            document.append("HTML_Document", HTML_doc);
            document.append("Description", description);

            collection.insertOne(document);

            return 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    int insertIntoInexer(Hashtable<String, ArrayList<String>> indexer) {
        try {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("Crawler");
            MongoCollection collection = mongoDatabase.getCollection("Inexer");

            List<Document> documents = new ArrayList<Document>();

            for (String key : indexer.keySet()) {
                Document document = new Document("key", key);
                // for (int i = 0; i < indexer.get(key).size(); i++) {
                document.append("URLs", indexer.get(key));
                // }
                // documents.add(document);
                try {
                    collection.insertOne(document);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            // collection.insertMany(documents);
            return 0;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return -1;
        }

    }

    FindIterable<Document> retrieveFromCrawler() {
        try {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("Crawler");
            MongoCollection collection = mongoDatabase.getCollection("CrawledPages");
             
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
