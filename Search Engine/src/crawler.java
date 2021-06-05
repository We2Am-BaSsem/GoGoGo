
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
import java.util.ArrayList;
import java.util.*;
import com.mongodb.internal.connection.Time;

public class crawler implements Runnable {

    private String start_url;
    public HashSet<String> visited_pages;
    public static HashSet<String> toVisit_pages = new HashSet<String>();
    public  static ArrayList<String> blocked_robots = new ArrayList<String>();
    static MongoDBManager dbManager = new MongoDBManager();

    /**
     * @brief Constructor  of crawler
     * param Visitied_pages
     * param Start_url of each Thread
     */
    public crawler(HashSet<String> visited_pages, String start_url) {
        this.start_url = start_url;
        this.visited_pages = visited_pages;
        addTopagesToVisit(start_url);
    }

    /**
     * Synchronized Function to add to visited pages in MongoDB
     *
     * @param url needed to be put in visited_pages
     */
    public void addToVisitedPages(String url) {
        synchronized (this.dbManager) {
            try {
                dbManager.insertIntoVisited_pages(url);
                this.visited_pages.add(url);
            } catch (Exception e) {

            }
        }
    }

    /**
     * @brief Synchronized Function to add to to_be_vistied pages in MongoDB
     *
     * @param url needed to be put in to_be_vistied pages in MongoDB
     */
    public void addTopagesToVisit(String url) {

            synchronized (this.dbManager) {
                if (!(this.toVisit_pages.contains(url) || this.visited_pages.contains(url))) {
                try {
                    int result = dbManager.insertIntobeVisited(url);
                    if (result == -1) {
                        this.toVisit_pages.add(url);
                    } else {
                        this.toVisit_pages.add(url);
                    };
                    this.dbManager.notifyAll();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @brief Synchronized Function to get next_link(url) from MongoDB then remove it from MongoDB
     *
     *  no needed
     */
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

    /**
     * @brief Synchronized Function to get getsize_visited_pages
     *
     *  no param needed
     */
    public int getsize_visited_pages() {
        synchronized (this.visited_pages) {
            return this.visited_pages.size();
        }
    }

    /**
     * @brief Synchronized Function to get check if this url in visited pages hashset
     *
     *  @param  url to be checked
     */
    public boolean checkVisitedPages(String url) {
        synchronized (this.visited_pages) {
            return this.visited_pages.contains(url);
        }
    }

    /**
     * @brief Synchronized Function to get check if this if this url in ToVisit Pages hashset
     *
     *  @param  url to be checked
     */
    public boolean checkToVisitPages(String url) {
        synchronized (this.toVisit_pages) {
            return this.toVisit_pages.contains(url);
        }
    }

    /**
     * @brief Run Function Doing Crawler work
     *
     */
    public void run() {

        while (getsize_visited_pages() < 1000) {

            String next_url = pollFrompagesToVisit();

            try {
                Document doc = request(next_url);
                if (doc != null) {
                    Elements elements = doc.select("a[href]");
                    for (Element e : elements) {
                        String href = e.attr("href");
                        href = normalize(href, next_url);
                        try {
                            if (href != null && !checkToVisitPages(href) &&  !checkVisitedPages(href)) {  //&& robotSafe(href)
                                addTopagesToVisit(href);
                            }
                        } catch (Exception e2) {
                        }
                    }
                }

            } catch (Exception ex) {

            }
        }

    }


    /**
     * @brief Run check get Document in webpage and add crawler pages into MongoDataBase
     *
     * @param url
     */
    private Document request(String url) {
        try {
            if(robotSafe(url) == false) {return null;}
            Connection con = Jsoup.connect(url);
            Document doc = con
                    .userAgent(
                            "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get();
            if (con.response().statusCode() == 200) {
                String HTML_Document = doc.toString();

                // Get time of Insertion into DB
                long start = System.currentTimeMillis();
                String title = doc.title();
                String description = doc.select("meta[name=description]").get(0).attr("content");
                Integer result = dbManager.insertIntoCrawler(url, title, HTML_Document, description);
                long end = System.currentTimeMillis();
                System.out.println("inserting time :" + (end - start) / 1000 + "s");
                if (result == 0) {
                    System.out.println("Thread# " + Thread.currentThread().getName() + " Inserted <" + url + "> successfully");
                    addToVisitedPages(url);
                    return doc;
                } else if (result == -1) {
                    System.out.println("Inserted <" + url + "> failed");
                    //addToVisitedPages(url);
                    return null;
                }

            }
            return null;
        } catch (IOException e) {
            //System.out.println(e.getMessage());
            return null;
        }

    }

    /**
     * @brief Function Takes link and base and return Normalized links
     *
     * @param link
     * @param base
     */
    private String normalize(String link, String base) {
        try {
            URL u = new URL(base);
            if (link.startsWith("./")) {
                link = link.substring(2);
                link = u.getProtocol() + "://" + u.getAuthority() + stripPath(u.getPath()) + link;
            } else if (link.indexOf('?') != -1)
                link = link.substring(0, link.indexOf('?'));
            else if (link.indexOf('#') != -1)
                link = link.substring(0, link.indexOf('#'));
            else if (link.startsWith("javascript:")) {
                link = null;
            }
            return link;
        } catch (Exception e) {
           // e.printStackTrace();
            return null;
        }

    }

    // cleans up the URLs
    private String stripPath(String path) {
        int pos = path.lastIndexOf("/");
        return pos <= -1 ? path : path.substring(0, pos + 1);
    }

    /**
     * @brief Function Takes link  and Check if link is robotsafe or not
     *
     * @param link
     */
    boolean  robotSafe(String link) {

        if (blocked_robots.contains(link)) return false;

        URL url = null;
        String strHost;
        try {
            url = new URL(link);
        } catch (Exception ex) {
        }
        strHost = url.getHost();

        String strRobot = url.getProtocol() + "://" + strHost + "/robots.txt"; // https://www.google.com/robots.txt

        URL urlRobot;

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
                    if(robot_String == "") {return true;}
                    else if (robot_String == "/") {
                        System.out.println("blocked by robot "+link + robot_String);
                        return false;
                    }
                    else if (robot_String.startsWith("/") && link.endsWith("/")) {
                        robot_String = robot_String.substring(1);
                    } else {
                        robot_String = line.substring(start, end).trim();
                    }

                    if (link.contains(robot_String)) {
                        blocked_robots.add(link + robot_String);
                        System.out.println("blocked by robot "+link + robot_String);
                        return false;
                    }

                }
                if (line.startsWith("User-agent")) {
                    break;
                }
            }
        }
        return true;
    }
}