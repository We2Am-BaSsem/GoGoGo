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

    int insertIntoIndexer(Hashtable<String, ArrayList<String>> indexer) {
        try {
            MongoDatabase mongoDatabase = mongoClient.getDatabase("Crawler");
            MongoCollection collection = mongoDatabase.getCollection("Inexer");

            List<Document> docs = new ArrayList<Document>();
            for (String key : indexer.keySet()) {
                Document document = new Document("key", key);
                document.append("DF", indexer.get(key).size());
                Document URLTFdoc = new Document();
                List<Document> documents = new ArrayList<Document>();
                for (int i = 0; i < indexer.get(key).size(); i++) {
                    String data = indexer.get(key).get(i);
                    int indexTitle = data.indexOf("->title:");
                    int indexTF = data.indexOf("->TF");
                    String url = data.substring(0, indexTitle);
                    String title = data.substring(indexTitle + 8, indexTF);
                    String TF = data.substring(indexTF + 4, data.length());
                    URLTFdoc.append("URL", url);
                    URLTFdoc.append("TF", Integer.parseInt(TF));
                    URLTFdoc.append("title", title);
                    documents.add(URLTFdoc);
                }
                document.append("URLs", documents);
                docs.add(document);
            }

            try {
                collection.insertMany(docs);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
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
