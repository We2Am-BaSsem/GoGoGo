import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Main {
    public static void main(String[] args) {
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
        MongoDBManager dbManager = new MongoDBManager();

        String start_url = "https://mkyong.com/java/how-to-delete-directory-in-java/";
        crawlerScheduler crawler = new crawlerScheduler (start_url);
        dbManager.CloseConnection();
    }
}
