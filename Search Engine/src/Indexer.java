
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Indexer {
    static HashMap<String, Long> keyWords = new HashMap<String, Long>();
    static HashMap<String, ArrayList<String>> indexer = new HashMap<String, ArrayList<String>>();

    public static void main(String[] args) throws Exception {
        try {
            Index("googlecomassistan");
            System.out.println(indexer);
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("error in indexing");
        }
    }

    private static void Index(String url) {
        try {
            Scanner In = new Scanner(new File("docs\\" + url + ".txt"));
            while (In.hasNext()) {

                String word = In.next();
                word.toLowerCase();
                //System.out.println(word);
                if (keyWords.containsKey(word)) {
                    keyWords.put(word, keyWords.get(word) + 1);
                } else {
                    keyWords.put(word, (long) 1);
                }

                if (indexer.containsKey(word)) {
                    // keyWords.put(word, keyWords.get(word)+1);
                    indexer.get(word).add(url);
                } else {
                    keyWords.put(word, (long) 1);
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(url);
                    indexer.put(word, temp);
                }


            }
            In.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
