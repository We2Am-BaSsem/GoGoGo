import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Main {
    public static void main(String[] args) {
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
        MongoDBManager dbManager = new MongoDBManager();

        String[] seeds = {"https://www.w3schools.com/tags/tag_textarea.asp",
                "https://www.sanfoundry.com/java-program-compute-determinant-matrix/",
                "https://www.youtube.com/watch?v=Dj2R-f9zgGs",
                "https://en.wikipedia.orgapi/",
                "https://www.w3schools.com/tags/tag_textarea.asp"};
        crawlerScheduler crawler = new crawlerScheduler (seeds);

    }
}
