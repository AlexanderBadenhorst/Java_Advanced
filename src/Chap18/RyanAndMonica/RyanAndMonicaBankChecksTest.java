package Chap18.RyanAndMonica;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RyanAndMonicaBankChecksTest {
  public static void main(String[] args) throws InterruptedException {
    for (int i = 0; i < 10; i++) {
      BankAccountChecks account = new BankAccountChecks();
      RyanAndMonicaBankChecksJob ryan = new RyanAndMonicaBankChecksJob("Ryan", account, 50);
      RyanAndMonicaBankChecksJob monica = new RyanAndMonicaBankChecksJob("Monica", account, 100);
      ExecutorService executor = Executors.newFixedThreadPool(2);
      executor.execute(ryan);
      executor.execute(monica);
      executor.shutdown();
      executor.awaitTermination(1, TimeUnit.MINUTES);
      System.out.println("---");
    }
  }
}

class RyanAndMonicaBankChecksJob implements Runnable {
  private final String name;
  private final BankAccountChecks account;
  private final int amountToSpend;

  RyanAndMonicaBankChecksJob(String name, BankAccountChecks account, int amountToSpend) {
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

class BankAccountChecks {
  private int balance = 100;

  public int getBalance() {
    return balance;
  }

  public synchronized void spend(String name, int amount) {
    if (getBalance() >= amount) {
      balance = balance - amount;
      if (balance < 0) {
        System.out.println("Overdrawn!");
      }
    } else {
      System.out.println("Sorry, not enough for " + name);
    }
  }
}
