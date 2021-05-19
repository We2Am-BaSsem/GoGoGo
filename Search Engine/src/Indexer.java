
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Scanner;

import ca.rmen.porterstemmer.PorterStemmer;

public class Indexer {
    // static HashMap<String, Long> keyWords = new HashMap<String, Long>();
    static Hashtable<String, ArrayList<String>> indexer = new Hashtable<String, ArrayList<String>>();
    static ArrayList<String> stopWords = new ArrayList<String>();

    public static void main(String[] args) throws Exception {
        try {
            fillStopWords();
            Index("googlecomassistan");
            // System.out.println(indexer);
        } catch (Exception e) {
            System.out.println("error in indexing1");
        }
    }

    private static void fillStopWords() {
        try {
            Scanner In = new Scanner(new File("StopWords dataSet\\English.txt"));
            while (In.hasNext()) {
                stopWords.add(In.next());
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private static void Index(String url) {
        try {
            Scanner In = new Scanner(new File("docs\\" + url + ".txt"));
            while (In.hasNext()) {

                String preWord = In.next();

                String word = specialCharStemmer(preWord);

                if (word.length() == 0 || stopWords.contains(word))
                    continue;
                PorterStemmer ps = new PorterStemmer();
                word = ps.stemWord(word);

                // if (keyWords.containsKey(word)) {
                // keyWords.put(word, keyWords.get(word) + 1);
                // } else {
                // keyWords.put(word, (long) 1);
                // }

                if (indexer.containsKey(word)) {
                    // keyWords.put(word, keyWords.get(word)+1);
                    indexer.get(word).add(url);
                } else {
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

    private static void writeToFile(Hashtable<String, ArrayList<String>> indexer) {
        try {
            PrintWriter out = new PrintWriter("index\\index.txt");
            for (String keyWord : indexer.keySet()) {
                // System.out.println(i);
                out.write(keyWord + ':');
                for (int i = 0; i < indexer.get(keyWord).size(); i++) {
                    out.write(indexer.get(keyWord).get(i) + " ");
                }
                out.write('\n');
            }
            out.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    private static String specialCharStemmer(String preWord) {
        int i = 0;
        while (i < preWord.length() && (preWord.charAt(i) < 'A' ||
              (preWord.charAt(i) > 'Z' && preWord.charAt(i) < 'a')
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
