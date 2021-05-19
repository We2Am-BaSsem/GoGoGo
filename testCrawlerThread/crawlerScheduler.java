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
