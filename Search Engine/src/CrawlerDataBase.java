import java.net.URL;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

public class CrawlerDataBase {
    static String connectionString = "mongodb+srv://SearchEngine:SearchEngine123456@crawler.sajqt.mongodb.net/admin";
    static MongoClientURI clientURI = new MongoClientURI(connectionString);
    static MongoClient mongoClient = new MongoClient(clientURI);
    public static void main(String[] args) {
        try {

        } catch (Exception e) {
            System.out.println("failed to insert");

        }
    }

    int insertIntoCrawler(String URL, String fileName) {
        try {
        MongoDatabase mongoDatabase = mongoClient.getDatabase("Crawler");
        MongoCollection collection = mongoDatabase.getCollection("CrawledPages");

        Document document = new Document("URL", URL);
        document.append("filename", fileName);
        
        collection.insertOne(document);

        //mongoClient.close();

        return 0;
        } catch (Exception e) {
            //TODO: handle exception
            return -1;
        }
    }
}
