import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.*;

public class crawlerScheduler {

    //String start_url;
    HashSet<String> pages_to_visit = new HashSet<String>();
    HashSet<String> visited_pages = new HashSet<String>();

    public crawlerScheduler(String start_url) {
//        ExecutorService executor = Executors.newFixedThreadPool(4);// creating a pool of 4 threads
//
//        this.start_url = start_url;
//        this.pages_to_visit.add(start_url);
//
//        //do {
//            Runnable crawler = new crawlerThread(this.pages_to_visit, this.visited_pages);
//            executor.execute(crawler);// calling execute method of ExecutorService
//        //} while (!pages_to_visit.isEmpty());
//
//        // Finished
//        executor.shutdown();
//        while (!executor.isTerminated()) {
//        }
//
//        System.out.println("Finished all threads");
        //String start_url = "https://stackoverflow.com";
        String [] seeds = {"https://www.programiz.com/"};
        this.pages_to_visit.add(start_url);
        crawler crawling_seed = new crawler(this.pages_to_visit, this.visited_pages);
        Thread t1, t2, t3;
        t1 = new Thread(crawling_seed);
        t2 = new Thread(crawling_seed);
        t3 = new Thread(crawling_seed);

        t1.setName("1");
        t2.setName("2");
        t3.setName("3");
        long before = System.currentTimeMillis();
        t1.start();
        t2.start();
        t3.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long after = System.currentTimeMillis();
        System.out.println("Time  = " + (after - before) + " ms = " + (after - before) / 60000 + " min");

    }
}