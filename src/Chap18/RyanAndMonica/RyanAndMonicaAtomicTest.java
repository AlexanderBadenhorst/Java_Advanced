package Chap18.RyanAndMonica;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RyanAndMonicaAtomicTest {
  public static void main(String[] args) throws InterruptedException {
    for (int i = 0; i < 100; i++) {
      BankAccountWithAtomic account = new BankAccountWithAtomic();
      RyanAndMonicaAtomicJob ryan = new RyanAndMonicaAtomicJob("Ryan", account, 50);
      RyanAndMonicaAtomicJob monica = new RyanAndMonicaAtomicJob("Monica", account, 100);
      ExecutorService executor = Executors.newFixedThreadPool(2);
      executor.execute(ryan);
      executor.execute(monica);
      executor.shutdown();
      executor.awaitTermination(1, TimeUnit.MINUTES);
      System.out.println("---");
    }
  }
}

class RyanAndMonicaAtomicJob implements Runnable {
  private final String name;
  private final BankAccountWithAtomic account;
  private final int amountToSpend;

  RyanAndMonicaAtomicJob(String name, BankAccountWithAtomic account, int amountToSpend) {
    this.name = name;
    this.account = account;
    this.amountToSpend = amountToSpend;
  }

  public void run() {
    goShopping(amountToSpend);
  }

  private void goShopping(int amount) {
    System.out.println(name + " is about to spend");
    account.spend(name, amount);
    System.out.println(name + " finishes spending");
  }
}

class BankAccountWithAtomic {
  private final AtomicInteger balance = new AtomicInteger(100);

  public void spend(String name, int amount) {
    int initialBalance = balance.get();
    if (initialBalance >= amount) {
      boolean success = balance.compareAndSet(initialBalance, initialBalance - amount);
      if (!success) {
        System.out.println("Sorry " + name + ", you haven't spent the money.");
      }
    } else {
      System.out.println("Sorry, not enough for " + name);
    }
  }
}