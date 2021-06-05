import java.util.logging.Level;
import java.util.logging.Logger;
public class Main {
    public static void main(String[] args) {
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
        MongoDBManager dbManager = new MongoDBManager();

        String[] seeds = {
                "https://www.w3schools.com",
                "https://spring.io/",
                "https://www.javatpoint.com",
                "https://docs.mongodb.com",
                "https://www.gamespot.com/",
                "https://www.skysports.com/",
                "https://cooking.nytimes.com/",
                "https://en.unesco.org/",
                "https://www.who.int/",
                "https://www.imdb.com/"
        };
        long start = System.currentTimeMillis();
        crawlerScheduler crawler = new crawlerScheduler (seeds);
        long time = (System.currentTimeMillis() - start) / 1000;
        System.out.println("\nTime taken = " + time + " ms");
    }
}
