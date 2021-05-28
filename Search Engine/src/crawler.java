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
import org.jsoup.select.Elements;
//import com.mongodb.internal.connection.Time;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import java.util.*;
import com.mongodb.internal.connection.Time;

public class crawler implements Runnable {
    private String start_url;
    public HashSet<String> pages_to_visit;
    public HashSet<String> visited_pages;

    static MongoDBManager dbManager = new MongoDBManager();
    public crawler(HashSet<String> pages_to_visit, HashSet<String> visited_pages) {
        //System.out.println("started thread " + Thread.currentThread().getName());
        this.pages_to_visit = pages_to_visit;
        this.visited_pages = visited_pages;
    }
    public void addToVisitedPages(String url) {
        synchronized (this.visited_pages) {
            try {
                visited_pages.add(url);
            } catch (Exception e) {
                System.out.println("error");
            }
        }
    }

    public void addTopagesToVisit(String url) {
        synchronized (this.pages_to_visit) {
            try {
                if(!this.pages_to_visit.contains(url)) {
                    this.pages_to_visit.add(url);
                    this.pages_to_visit.notifyAll();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public String pollFrompagesToVisit() {
        synchronized (this.pages_to_visit) {
            String next_url = null;
            try {
                while(pages_to_visit.isEmpty()){
                    this.pages_to_visit.wait();
                }
                next_url = this.pages_to_visit.iterator().next();
                this.pages_to_visit.remove(next_url);
            } catch (Exception e) {
                System.out.println("error1");
            }
            return next_url;
        }
    }
    public int getsize_visited_pages() {
        synchronized (this.visited_pages) {
            return this.visited_pages.size();
        }
    }
    public boolean check_if_empty() {
        synchronized (this.pages_to_visit) {
            return this.pages_to_visit.isEmpty();
        }
    }

    public boolean checkVisitedPages(String url) {
        synchronized (this.visited_pages) {
            return this.visited_pages.contains(url);
        }
    }
    public boolean checkPages_to_visit(String url) {
        synchronized (this.pages_to_visit) {
            return this.pages_to_visit.contains(url);
        }
    }

    public void run() {
        //System.out.println("started thread " + Thread.currentThread().getId());
        // crawling until pages_to_visit array is empty or we reach to the desired
        // number of craweled pages

        while ( getsize_visited_pages() < 10 ) {
            //System.out.println(getsize_visited_pages());
            // get first element of pages_to_visit
            // String next_url = pages_to_visit.poll();
            String next_url = pollFrompagesToVisit();
            try {
                // get content from request function
                Document doc = request(next_url);
                if (doc != null) {
                    // loopover the ancher tags of pages
                    Elements elements = doc.select("a");
                    for (Element e : elements) {
                        String href = e.attr("href");
                        href = processLink(href,next_url);
                        if(!checkVisitedPages(href)&&!checkPages_to_visit(href) && href!= null) {
                            addTopagesToVisit(href);
                        }
                        // pages_to_visit.add(href);
                    }
                }

            } catch (Exception ex) {
                // System.out.println("can't access the URL");
            }
        }
        // System.out.println("Finished, "+Thread.currentThread().getId()+" number of pages visited = " + visited_pages.size());
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

    private  Document request(String url) {
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();
            if (con.response().statusCode() == 200) {
                // System.out.println("Link: "+url);
                // System.out.println("title: "+doc.title());
                addToVisitedPages(url);
                //String fileName = writeToFile(url, doc.text());

                String HTML_Document = doc.toString();
                //System.out.println("#thread"+Thread.currentThread().getName()+" link :"+url);
                long start = System.currentTimeMillis();
                String title = doc.title();
                String description = doc.select("meta[name=description]").get(0).attr("content");
                Integer result = dbManager.insertIntoCrawler(url, title, HTML_Document,description);
                long end = System.currentTimeMillis();
                System.out.println("inserting time :"+ (end-start)/1000 + "s");
                if (result == 0) {
                    System.out.println("Inserted <" + url + "> successfully");
                }
                else if (result == -1){
                    System.out.println("Inserted <" + url + "> failed");
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


    private String processLink(String link, String base) {

        try {
            URL u = new URL(base);
            if (link.startsWith("./")) {
                link = link.substring(2, link.length());
                link = u.getProtocol() + "://" + u.getAuthority() + stripFilename(u.getPath()) + link;
            } else if (link.contains("#")) {
                link = null;
            } else if (link.startsWith("javascript:")) {
                link = null;
            }
            return link;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    // cleans up the URLs
    private String stripFilename(String path) {
        int pos = path.lastIndexOf("/");
        return pos <= -1 ? path : path.substring(0, pos + 1);
    }
}