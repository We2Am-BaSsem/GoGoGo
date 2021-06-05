import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class crawlerScheduler {

    //String start_url;
    static  HashSet<String> visited_pages = new HashSet<String>();
    String start_url;

    public crawlerScheduler(String [] seeds) {



        System.out.println("Enter the Number of Threads : ");

        int n = -1;
        while (n <= 0)
            try {
                n = new Scanner(System.in).nextInt();
            } catch (Exception e) {
                System.out.println("Please Enter a Number");
                n = -1;
            }

        Thread[] threads = new Thread[n];
        for (int i = 0; i < n; i++) {
            threads[i] = new Thread(new crawler( this.visited_pages, seeds[i]));
            threads[i].setName(Integer.toString(i));
        }

        long before = System.currentTimeMillis();
        for (int i = 0; i < n; i++)
            threads[i].start();

        for (int i = 0; i < n; i++)
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        long after = System.currentTimeMillis();
    }
}

