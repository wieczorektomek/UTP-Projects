/**
 *
 *  @author Wieczorek Tomasz S27161
 *
 */

package zad1;


public class Main {

  public static void main(String[] args) throws InterruptedException {
    Letters letters = new Letters("ABCD");
    for (Thread t : letters.getThreads()) System.out.println(t.getName());
    letters.getThreads().forEach(Thread::start);

    Thread.sleep(5000);

    for (Thread t : letters.getThreads()){
      t.interrupt();
    }
    System.out.println("\nProgram skończył działanie");
  }

}
