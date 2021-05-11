
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import ca.rmen.porterstemmer.PorterStemmer;

public class Indexer {
    static HashMap<String, Long> keyWords = new HashMap<String, Long>();
    static HashMap<String, ArrayList<String>> indexer = new HashMap<String, ArrayList<String>>();

    public static void main(String[] args) throws Exception {
        try {
            Index("googlecomassistan");
            System.out.println(indexer);
        } catch (Exception e) {
            System.out.println("error in indexing1");
        }
    }

    private static void Index(String url) {
        try {
            Scanner In = new Scanner(new File("docs\\" + url + ".txt"));
            while (In.hasNext()) {

                String word = In.next();
                PorterStemmer ps = new PorterStemmer();
                word = ps.stemWord(word);
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

                writeToFile(indexer);
            }
            In.close();
        } catch (Exception e) {
            System.out.println("error in indexing2");
        }
    }

    private static void writeToFile(HashMap<String, ArrayList<String>> indexer) {
        try {
            PrintWriter out = new PrintWriter("index\\index.txt");
            for (String keyWord : indexer.keySet()) {
                //System.out.println(i);
                out.write(keyWord+':');
                for (int i = 0; i < indexer.get(keyWord).size(); i++) {
                    out.write(indexer.get(keyWord).get(i)+ " ");
                }
                out.write('\n');
              }
            out.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }    

}
