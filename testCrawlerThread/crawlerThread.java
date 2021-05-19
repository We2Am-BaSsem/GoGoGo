Mostafa Wael
#2310

Ahmed_217 — Today at 4:59 PM
hi:joy:
yala?
Mostafa Wael
 started a call.
 — Today at 4:59 PM
Mostafa Wael — Today at 5:12 PM
read file stream
private void recieveFileX() throws IOException
{
    DataInputStream dis = new DataInputStream(connection.getInputStream());
    //receive file number
    int len = dis.readInt();

Expand
message.txt
3 KB
Mostafa Wael — Today at 6:01 PM
https://www.cs.uic.edu/~jbell/CourseNotes/C_Programming/Decisions.html#:~:text=C%20does%20not%20have%20boolean,zero%20is%20interpreted%20as%20true.
Ahmed_217 — Today at 6:54 PM
https://youtu.be/SRafkuUMQh8
YouTube
kl2217
java example -- concurrent BSF web crawler

Mostafa Wael
 started a call that lasted a few seconds.
 — Today at 6:59 PM
Mostafa Wael
 started a call that lasted a minute.
 — Today at 7:04 PM
Ahmed_217
 started a call that lasted 44 minutes.
 — Today at 7:05 PM
Mostafa Wael — Today at 7:50 PM
crawlerThread
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

Expand
message.txt
7 KB
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.*;

public class crawlerScheduler {

     String start_url;
     Queue<String> pages_to_visit = new LinkedList<String>();
     HashSet<String> visited_pages = new HashSet<String>();

    public crawlerScheduler(String start_url) {
        ExecutorService executor = Executors.newFixedThreadPool(4);// creating a pool of 4 threads

        this.start_url = start_url;
        this.pages_to_visit.add(start_url);

        do {
                Runnable crawler = new crawlerThread(this.pages_to_visit, this.visited_pages);
                executor.execute(crawler);// calling execute method of ExecutorService
        } while (!pages_to_visit.isEmpty());

        // Finished
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        System.out.println("Finished all threads");
    }
}
﻿
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import java.util.*;

public class crawlerThread implements Runnable {

    private String start_url;
    private Queue<String> pages_to_visit;
    private HashSet<String> visited_pages;

    // constructor and start crawl
    public crawlerThread(Queue<String> pages_to_visit, HashSet<String> visited_pages) {
        System.out.println("started thread " + Thread.currentThread().getId());
        this.pages_to_visit = pages_to_visit;
        this.visited_pages = visited_pages;
    }

    public void addToVisitedPages(String url) {
        synchronized (this.visited_pages) {
            try {
                visited_pages.add(url);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void addTopagesToVisit(String url) {
        synchronized (this.pages_to_visit) {
            try {
                this.pages_to_visit.add(url);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public String pollFrompagesToVisit() {
        synchronized (this.pages_to_visit) {
            String next_url = "";
            try {
                next_url = this.pages_to_visit.poll();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return next_url;
        }
    }

    public boolean checkVisitedPages(String url) {
        synchronized (this.visited_pages) {
            return this.visited_pages.contains(url);
        }
    }

    // check if this url is valid by try to open it
    private boolean valid_link(String url) {
        try {
            (new java.net.URL(url)).openStream().close();
            return true;
        } catch (Exception ex) {
        }
        return false;
    }

    // connect to the page to be crwaled and svaes its content in the docs
    private Document request(String url) {
        try {
            // connect to url by jsoup
            Connection con = Jsoup.connect(url);
            // get document (content) of url
            Document doc = con.get();
            if (con.response().statusCode() == 200) { // check if connection we connect to url successfully
                // now we completely visited the link so we add it in visited pages and ready to
                // get its content and store it
                System.out.println("Link: " + url);
                addToVisitedPages(url);
                // visited_pages.add(url);
                writeToFile(url, doc.text());
                return doc;
            }
            return null;
        } catch (IOException e) {
            System.out.println("error here1");
            return null;
        }
    }

    // main fucntion
    public void run() {
        // crawling until pages_to_visit array is empty or we reach to the desired
        // number of craweled pages
        while (!this.pages_to_visit.isEmpty() && this.visited_pages.size() < 20) {
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
                        if (!checkVisitedPages(href))
                            // if link is not visited before we add it to pages_to_visit array (pending to
                            // be visited)
                            addTopagesToVisit(href);
                        // pages_to_visit.add(href);
                    }
                }
            } catch (Exception ex) {
                // System.out.println("can't access the URL");
            }
        }
        System.out.println("Finished, number of pages visited = " + visited_pages.size());
    }

    // write the file requested in the docs
    private void writeToFile(String url, String text) {
        String c_url;
        try {
            // generate the file containing the content of url
            c_url = set_up_url(url);
            PrintWriter out = new PrintWriter("docs" + "/" + c_url + ".txt");
            out.write(text);
            out.close();
        } catch (Exception ex) {
            System.out.println("sda");
        }
    }

    // set up remove all special characters and remove protocol
    private String set_up_url(String url) {
        int pos1 = url.indexOf('.');
        url = url.substring(pos1, url.length() - 1);
        url = url.replaceAll("[^a-zA-Z0-9]", "");
        return url;
    }

    // cleans up the URLs
    private String processLink(String link, String base) {

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

    // cleans up the URLs
    private String stripFilename(String path) {
        int pos = path.lastIndexOf("/");
        return pos <= -1 ? path : path.substring(0, pos + 1);
    }

}
message.txt
7 KB