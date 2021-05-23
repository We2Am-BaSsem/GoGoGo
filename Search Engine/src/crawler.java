//package web.crawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;



import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;

import com.mongodb.internal.connection.Time;

public class crawler {
    ArrayList<String> URLS = new ArrayList<>();
    ArrayList<String> fileNames = new ArrayList<>();
    static MongoDBManager dbManager = new MongoDBManager();


    public static void main(String[] args) throws Exception {
        String url = "https://developers.google.com/community/dsc-solution-challenge";
        ArrayList<String> visited = new ArrayList<>();
        ArrayList<String> pending = new ArrayList<>();
        crawl(1, url, visited);
        MongoDBManager dbManager = new MongoDBManager();
        dbManager.CloseConnection();
    }

    private static void crawl(int level, String url, ArrayList<String> visited) {
        try {
            if (visited.size() < 100) {
                Document doc = request(url, visited);
                String text = doc.text();
                if (doc != null) {
                    // System.out.println(text);
                    for (Element link : doc.select("a")) {

                        String next_link = link.attr("href");
                        // next_link = processLink(next_link, url);
                        if (visited.contains(next_link) == false && next_link.startsWith("https://")) {
                            crawl(level++, next_link, visited);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static String writeToFile(String url, String text) {
        try {
            url = set_up_url(url);
            PrintWriter out = new PrintWriter("docs\\" + url + ".txt");
            out.write(text);
            out.close();
            return url;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    private static Document request(String url, ArrayList<String> v) {
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();
            if (con.response().statusCode() == 200) {
                // System.out.println("Link: "+url);
                // System.out.println("title: "+doc.title());
                v.add(url);
                //String fileName = writeToFile(url, doc.text());

                String HTML_Document = doc.toString();

                long start = System.currentTimeMillis();
                String description = doc.select("meta[name=description]").get(0).attr("content");
                Integer result = dbManager.insertIntoCrawler(url, doc.title(), HTML_Document,description);
                long end = System.currentTimeMillis();
                System.out.println("inserting time :"+ (end-start)/1000 + "s");
                if (result == 0) {
                    System.out.println("Inserted <" + url + "> successfully");
                }
                return doc;
            }
            return null;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private static String set_up_url(String url) {
        int pos1 = url.indexOf('.');

        url = url.substring(pos1, url.length() - 1);
        url = url.replaceAll("[^a-zA-Z0-9]", "");
        return url;
    }

    /*
     * private static String processLink(String link, String base) {
     * 
     * try { URL u = new URL(base); if (link.startsWith("./")) { link =
     * link.substring(2, link.length()); link = u.getProtocol() + "://" +
     * u.getAuthority() + stripFilename(u.getPath()) + link; } else if
     * (link.startsWith("#")) { link = base + link; } else if
     * (link.startsWith("javascript:")) { link = null; } else if
     * (link.startsWith("../") || (!link.startsWith("http://") &&
     * !link.startsWith("https://"))) { link = u.getProtocol() + "://" +
     * u.getAuthority() + stripFilename(u.getPath()) + link; } return link; } catch
     * (Exception e) { e.printStackTrace(); return null; }
     * 
     * } private static String stripFilename(String path) { int pos =
     * path.lastIndexOf("/"); return pos <= -1 ? path : path.substring(0, pos + 1);
     * }
     */
}