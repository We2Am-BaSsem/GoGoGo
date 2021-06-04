
//package web.crawler;
import com.mongodb.client.FindIterable;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import org.jsoup.select.Elements;
import com.mongodb.internal.connection.Time;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import java.util.*;
import com.mongodb.internal.connection.Time;

public class crawler implements Runnable {
    private String start_url;
    public HashSet<String> visited_pages;
    public HashSet<String> toVisit_pages = new HashSet<String>();

    static MongoDBManager dbManager = new MongoDBManager();

    public crawler(HashSet<String> visited_pages, String start_url) {
        // System.out.println("started thread " + Thread.currentThread().getName());
        this.start_url = start_url;
        // this.pages_to_visit = pages_to_visit;
        this.visited_pages = visited_pages;
        // this.pages_to_visit.add(start_url);
        addTopagesToVisit(start_url);
    }

    public void addToVisitedPages(String url) {
        synchronized (this.dbManager) {
            try {
                dbManager.insertIntoVisited_pages(url);
                this.visited_pages.add(url);
            } catch (Exception e) {
                System.out.println("error");
            }
        }
    }

    public void addTopagesToVisit(String url) {
        if (!(this.toVisit_pages.contains(url) || this.visited_pages.contains(url))) {

            synchronized (this.dbManager) {
                try {
                    int result = dbManager.insertIntobeVisited(url);
                    if (result == -1) {
                        System.out.println(
                                "thread #" + Thread.currentThread().getName() + "fail to insert be visited" + url);
                        this.toVisit_pages.add(url);
                    } else {
                        System.out.println("thread #" + Thread.currentThread().getName()
                                + "suucessfully to insert be visited" + url);
                        this.toVisit_pages.add(url);
                    }
                    // if(!this.pages_to_visit.contains(url)) {
                    // this.pages_to_visit.add(url);
                    // this.pages_to_visit.notifyAll();
                    this.dbManager.notifyAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public String pollFrompagesToVisit() {
        synchronized (this.dbManager) {
            try {
                String next_url;
                next_url = (String) dbManager.retrieveFrombeVisited().get("URL");
                if (next_url == null) {
                    System.out.println(Thread.currentThread().getName() + " sleep\n");
                    // this.pages_to_visit.wait();
                }
                dbManager.deleteFrombeVisited(next_url);
                return next_url;
            } catch (Exception e) {
                // System.out.println("error1");
            }
            return null;
        }
    }

    public int getsize_visited_pages() {
        synchronized (this.visited_pages) {
            return this.visited_pages.size();
        }
    }

    public boolean checkVisitedPages(String url) {
        synchronized (this.visited_pages) {
            return this.visited_pages.contains(url);
        }
    }

    public boolean checkToVisitPages(String url) {
        synchronized (this.toVisit_pages) {
            return this.toVisit_pages.contains(url);
        }
    }

    public void run() {

        while (getsize_visited_pages() < 15) {
            // System.out.println("num = "+Thread.activeCount());
            // System.out.println(getsize_visited_pages());
            // get first element of pages_to_visit
            // String next_url = pages_to_visit.poll();
            String next_url = pollFrompagesToVisit();
            // System.out.println("e1: " + next_url);
            try {
                // get content from request function
                Document doc = request(next_url);
                if (doc != null) {
                    // loopover the ancher tags of pages
                    Elements elements = doc.select("a");
                    for (Element e : elements) {
                        String href = e.attr("href");
                        href = normalize(href, next_url);
                        try {
                            if (href != null &&  !checkToVisitPages(href) && !checkVisitedPages(href) && robotSafe(href)) {
                                // System.out.println("e2 " + href);
                                addTopagesToVisit(href);

                            }
                        } catch (Exception e2) {
                            System.out.println(e2.getMessage());
                        }
                    }
                }

            } catch (Exception ex) {
                // System.out.println("can't access the URL");
            }
        }
        // System.out.println("Finished, "+Thread.currentThread().getId()+" number of
        // pages visited = " + visited_pages.size());
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

    private Document request(String url) {

        try {
            // Connection con =
            // Jsoup.connect(url).userAgent(userAgent).referrer(referrer).maxBodySize(0);
            // Document doc = con.get();

            Connection con = Jsoup.connect(url);
            Document doc = con
                    .userAgent(
                            "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .get();
            if (con.response().statusCode() == 200) {

                String HTML_Document = doc.toString();
                // System.out.println("#thread"+Thread.currentThread().getName()+" link :"+url);
                long start = System.currentTimeMillis();
                String title = doc.title();
                String description = doc.select("meta[name=description]").get(0).attr("content");
                Integer result = dbManager.insertIntoCrawler(url, title, HTML_Document, description);
                long end = System.currentTimeMillis();
                System.out.println("inserting time :" + (end - start) / 1000 + "s");
                if (result == 0) {
                    System.out.println(
                            "Thread# " + Thread.currentThread().getName() + " Inserted <" + url + "> successfully");
                    // addToVisitedPages(url);
                    addToVisitedPages(url);
                } else if (result == -1) {
                    System.out.println("Inserted <" + url + "> failed");
                    addToVisitedPages(url);
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

    private String normalize(String link, String base) {

        try {
            URL u = new URL(base);
            if (link.startsWith("./")) {
                link = link.substring(2, link.length());
                link = u.getProtocol() + "://" + u.getAuthority() + stripFilename(u.getPath()) + link;
            } else if (link.indexOf('?') != -1)
                link = link.substring(0, link.indexOf('?'));
            else if (link.indexOf('#') != -1)
                link = link.substring(0, link.indexOf('#'));
            else if (link.startsWith("javascript:")) {
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

    boolean robotSafe(String link) {
        // final ArrayList<String> blocked_by_robot = new ArrayList<>();
        // final ArrayList<String> visited_hosts = new ArrayList<>();
        URL url = null;
        String strHost;
        try {
            url = new URL(link);
        } catch (Exception ex) {
            System.out.println("here");
        }
        strHost = url.getHost();

        String strRobot = url.getProtocol() + "://" + strHost + "/robots.txt"; // https://www.google.com/robots.txt
        // System.out.println(strRobot);
        URL urlRobot;
        // visited_hosts.add(strHost);
        boolean found = false, blocked_found = false;
        String content = null;
        URLConnection connection = null;
        try {
            connection = new URL(strRobot).openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();
            scanner.close();
        } catch (Exception ex) { // there is no robot.txt file
            // System.out.println("here 2");
            return true;

        }
        String[] arr = content.split("\n");
        String robot_String;
        for (String line : arr) {
            if (line.startsWith("User-agent: *")) {
                found = true;
                continue;
            }
            if (found) {
                if (line.contains("Disallow")) {
                    int start = line.indexOf(":") + 1;
                    int end = line.length();
                    robot_String = line.substring(start, end).trim();
                    if (robot_String == "/") {
                        System.out.println("blocked by robot\n");
                        return false;
                    }
                    if (robot_String.startsWith("/") && link.endsWith("/")) {
                        robot_String = robot_String.substring(1);
                    } else {
                        robot_String = line.substring(start, end).trim();
                    }

                    if (link.contains(robot_String)) {
                        System.out.println("blocked by robot\n");
                        return false;
                    }
                    // System.out.println(link + line.substring(start, end).trim());
                    // blocked_by_robot.add(link + robot_String);
                }
                if (line.startsWith("User-agent")) {
                    break;
                }
            }
        }
        return true;
    }
}
