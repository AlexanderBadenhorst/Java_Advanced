package Chap18.Exercises;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SynchronizedCollection {
  //13 959 ms
  public static void main(String[] args) throws InterruptedException {
    ExecutorService threadPool = Executors.newFixedThreadPool(2);
    long start = System.currentTimeMillis();
    for (int i = 0; i < 10; i++) {
      IData data = new DataSynchronized();
      threadPool.execute(new AddLowerCaseJob(data));
      threadPool.execute(new AddUpperCaseJob(data));
    }
    threadPool.shutdown();
    threadPool.awaitTermination(5, TimeUnit.MINUTES);
    long end = System.currentTimeMillis();
    System.out.println("Total time: " + (end - start));
  }
}

class DataSynchronized implements IData {
  private final List<String> letters = new ArrayList<>();

  public List<String> getLetters() {
    return letters;
  }

  public synchronized void addLetter(char letter) {
    letters.add(String.valueOf(letter));
  }
}
