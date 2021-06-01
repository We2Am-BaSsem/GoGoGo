import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class IndexerSchedular {

    public Hashtable<String, ArrayList<String>> indexer = new Hashtable<String, ArrayList<String>>();
    public MongoDBManager dbManager = new MongoDBManager();
    public Hashtable<String, List<Document>> docsKeyURLS = new Hashtable<String, List<Document>>();

    public IndexerSchedular() {
        Indexer indexerObject = new Indexer(this.docsKeyURLS);

        Thread t1, t2, t3, t4, t5, t6;
        t1 = new Thread(indexerObject);
        t2 = new Thread(indexerObject);
        t3 = new Thread(indexerObject);
        t4 = new Thread(indexerObject);
        t5 = new Thread(indexerObject);
        t6 = new Thread(indexerObject);

        t1.setName("1");
        t2.setName("2");
        t3.setName("3");
        t4.setName("4");
        t5.setName("5");
        t6.setName("6");

        long start = System.currentTimeMillis();
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();

        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            t4.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            t5.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            t6.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // create the inverted file and store it in the indexer
        // writeToFile(indexer);
         System.out.println("\n******************************\nindexing: " + docsKeyURLS.size()
                 + " word\n******************************\n");
        InsertIntoDB(dbManager);

        long end = System.currentTimeMillis();


        System.out.println("\n******************************\nindexing time: " + (end - start) / 1000
                + "s\n******************************\n");

        System.out.println("\n******************************\nindexing time: " + (end - start) / 1000 / 60
                + "min\n******************************\n");

    }

    private void InsertIntoDB(MongoDBManager dbManager) {
        try {
            dbManager = new MongoDBManager();
            dbManager.insertIntoIndexer2(this.docsKeyURLS);
            dbManager.CloseConnection();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
