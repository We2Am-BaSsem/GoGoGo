import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import ca.rmen.porterstemmer.PorterStemmer;

public class Indexer {
    // static HashMap<String, Long> keyWords = new HashMap<String, Long>();
    static Hashtable<String, ArrayList<String>> indexer = new Hashtable<String, ArrayList<String>>();
    static ArrayList<String> stopWords = new ArrayList<String>();

    public static void main(String[] args) throws Exception {
        try {
            //first we need to indetify the stop words we shouldn't index
            fillStopWords();

            //then we retrieve the data,crawled pages data, from the database
            MongoDBManager dbManager = new MongoDBManager();
            MongoCursor<Document> cursor = dbManager.retrieveFromCrawler();

            Integer i = 0;
            while (cursor.hasNext()) {
                Document Item = cursor.next();
                System.out.println(i +"- "+Item.get("URL").toString());
                System.out.println("\"" + Item.get("filename").toString() + "\"");
                
                //here we index each page one by one
                Index(Item.get("filename").toString(), Item.get("URL").toString());
                
                i++;
            }
            // System.out.println(indexer);

            //create the inverted file and store it in the indexer
            writeToFile(indexer);
            InsertIntoDB(dbManager);
            

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void InsertIntoDB(MongoDBManager dbManager) {
        dbManager = new MongoDBManager();
        dbManager.insertIntoInexer(indexer);
        dbManager.CloseConnection();
    }

    private static void fillStopWords() {
        try {
            //we just read the txt file and store it in an array list
            Scanner In = new Scanner(new File("StopWords dataSet\\English.txt"));
            while (In.hasNext()) {
                stopWords.add(In.next());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void Index(String filename, String url) {
        try {
            Scanner In = new Scanner(new File("docs\\" + filename + ".txt"));
            while (In.hasNext()) {

                String preWord = In.next();
                preWord.toLowerCase();

                //  1- we remove special chars from the word before processing 
                // and then check if it was a special we continue looping 
                // if not we process
                String word = specialCharStemmer(preWord);
                if (word.length() == 0 || stopWords.contains(word))
                    continue;

                //  2- we stem the word
                PorterStemmer ps = new PorterStemmer();
                word = ps.stemWord(word);

                /*
                // if (keyWords.containsKey(word)) {
                // keyWords.put(word, keyWords.get(word) + 1);
                // } else {
                // keyWords.put(word, (long) 1);
                // }
                */

                //  3- we check if this word was indexed before
                if (indexer.containsKey(word)) {
                    // if well we just append the url to the list
                    if (!indexer.get(word).contains(url))
                        indexer.get(word).add(url);
                } else {
                    // if not we add the word and url to the table
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(url);
                    indexer.put(word, temp);
                }

            }
            In.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void writeToFile(Hashtable<String, ArrayList<String>> indexer) {
        try {
            PrintWriter out = new PrintWriter("index\\index.txt");
            for (String keyWord : indexer.keySet()) {
                out.write(keyWord + ':');
                for (int i = 0; i < indexer.get(keyWord).size(); i++) {
                    out.write(indexer.get(keyWord).get(i) + " --- ");
                }
                out.write('\n');
            }
            out.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static String specialCharStemmer(String preWord) {
        int i = 0;
        while (i < preWord.length() && (preWord.charAt(i) < 'A' || (preWord.charAt(i) > 'Z' && preWord.charAt(i) < 'a')
                || preWord.charAt(i) > 'z'))
            i++;

        if (i == preWord.length()) {
            char[] empty = new char[0];
            return new String(empty);
        }

        int j = preWord.length() - 1;
        while (preWord.charAt(j) < 'A' || (preWord.charAt(j) > 'Z' && preWord.charAt(j) < 'a')
                || preWord.charAt(j) > 'z')
            j--;

        char[] charArr = new char[j - i + 1];
        int l = i;
        for (int k = 0; k < j - i + 1; k++) {
            charArr[k] = preWord.charAt(l);
            l++;
        }

        return new String(charArr);

    }
}
