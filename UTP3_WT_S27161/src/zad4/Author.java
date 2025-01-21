/**
 *
 *  @author Wieczorek Tomasz S27161
 *
 */

package zad4;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.util.stream.IntStream.range;

public class Author implements Runnable {
    public final BlockingQueue queue;
    private String[] s;
    public Author(String[] string) {
        this.s = string;
        queue = new LinkedBlockingQueue();
    }


    @Override
    public void run() {
        int bound = s.length;
        range(0, bound).forEach(i -> {
            try {
                Thread.sleep(1000);
                queue.put(s[i]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }


}
