package zad1;

import java.util.ArrayList;
import java.util.List;

public class Letters {

    String letters;
    private final List<Thread> threads;


    public Letters(String letters){
        this.letters = letters;
        threads = new ArrayList<>();
        createThreads();
    }

    private void createThreads() {
        for (char c : letters.toCharArray()){
            Thread thread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()){
                    System.out.print(c);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            thread.setName("Thread " + c);
            threads.add(thread);
        }
    }


    public List<Thread> getThreads() {return threads;}



}
