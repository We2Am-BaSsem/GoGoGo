package crawler.com;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;



public class Crawler {
    final static ArrayList <String>  pages_to_visit = new ArrayList<>();
    final static ArrayList <String>visited_pages = new ArrayList<>();
    //final static  Map<String, int> sample = new HashMap<String, String>();
    private final String start_url;

    // constructor and start crawl

    public Crawler(String start_url) {
        this.start_url = start_url;
        pages_to_visit.add(start_url);
        crawl();
    }

// check if this url is valid by try to open it
 private static boolean  valid_link (String url){
     try {
         (new java.net.URL(url)).openStream().close();
         return true;
     } catch (Exception ex) { }
     return false; }

    private static Document request(String url){
        try {
            // connect to url by jsoup
            Connection con = Jsoup.connect(url);
            // get document (content) of url
            Document doc = con.get();
            if (con.response().statusCode() == 200 ){ // check if connection we connect to url successfully
                // now we completely visited the link so we add it in visited pages and ready to get its content and store it
                System.out.println("Link: "+url);
                visited_pages.add(url);
                writeToFile(url, doc.text());
                return doc;
            }
            return null;
        } catch (IOException e) {
            System.out.println("error here1");
            return null;
        }
    }

    private static void crawl() {
        // crawling until pages_to_visit array is empty or we reach to the desired number of craweled pages
        while (!pages_to_visit.isEmpty() && visited_pages.size() < 20) {
            // get first element of pages_to_visit
            String next_url = pages_to_visit.remove(0);
            try {
                // get content from request function
                Document doc = request(next_url);
                if (doc != null  ) {
                    //loopover the ancher tags of pages
                    Elements elements = doc.select("a");
                    for (Element e : elements) {
                        String href = e.attr("href");
                        if(!visited_pages.contains(href))
                            // if link is not visited before we add it to pages_to_visit array (pending to be visited)
                            pages_to_visit.add(href);
                    }
                }
            }
            catch (Exception ex){
                System.out.println("error here4");
            }
        }
        System.out.println("Finished, number of pages visited = " + visited_pages.size());
    }

    private static void writeToFile(String url, String text) {
        String c_url;
        try {
            // generate the file containing the content of url
            c_url = set_up_url(url);
            PrintWriter out = new PrintWriter("E:\\SearchEngine/docs" + "/" + c_url + ".txt");
            out.write(text);
            out.close();
        } catch (Exception ex) {
            System.out.println("sda");
        }
    }
    //set up remove all special characters and remove protocol
    private static String set_up_url(String url){
        int pos1 =  url.indexOf('.');
        url = url.substring(pos1,url.length()-1);
        url = url.replaceAll("[^a-zA-Z0-9]", "");
        return  url;
    }

    private static String processLink(String link, String base) {

        try {
            URL u = new URL(base);
            if (link.startsWith("./")) {
                link = link.substring(2, link.length());
                link = u.getProtocol() + "://" + u.getAuthority() + stripFilename(u.getPath()) + link;
            } else if (link.startsWith("#")) {
                link = base + link;
            } else if (link.startsWith("javascript:")) {
                link = null;
            } else if (link.startsWith("../") || (!link.startsWith("http://") && !link.startsWith("https://"))) {
                link = u.getProtocol() + "://" + u.getAuthority() + stripFilename(u.getPath()) + link;
            }
            return link;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static String stripFilename(String path) {
        int pos = path.lastIndexOf("/");
        return pos <= -1 ? path : path.substring(0, pos + 1);
    }


}