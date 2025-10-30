package Chap18.Exercises;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadSafeCollection {
  // 13920 ms
  public static void main(String[] args) throws InterruptedException {
    ExecutorService threadPool = Executors.newFixedThreadPool(2);
    long start = System.currentTimeMillis();
    for (int i = 0; i < 10; i++) {
      IData data = new DataSafe();
      threadPool.execute(new AddUpperCaseJob(data));
      threadPool.execute(new AddLowerCaseJob(data));
    }
    threadPool.shutdown();
    threadPool.awaitTermination(5, TimeUnit.MINUTES);
    long end = System.currentTimeMillis();
    System.out.println("Total time: " + (end - start));
  }
}

class DataSafe implements IData {
  private final List<String> letters = new CopyOnWriteArrayList<>();

  public List<String> getLetters() {
    return letters;
  }

  public void addLetter(char letter) {
    letters.add(String.valueOf(letter));
  }
}

