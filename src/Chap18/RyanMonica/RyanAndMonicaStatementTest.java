package Chap18.RyanMonica;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RyanAndMonicaStatementTest {
  public static void main(String[] args) throws InterruptedException {
    BankAccountStatement account = new BankAccountStatement();
    ExecutorService executor = Executors.newFixedThreadPool(4);
    for (int i = 0; i < 5; i++) {
      executor.execute(new RyanAndMonicaStatementJob("Ryan", account, 20));
      executor.execute(new StatementReader("Monica", account));
      executor.execute(new StatementReader("Accountant", account));
      executor.execute(new StatementReader("Bank Manager", account));
    }
    executor.shutdown();
  }
}

class RyanAndMonicaStatementJob implements Runnable {
  private final String name;
  private final BankAccountStatement account;
  private final int amountToSpend;

  RyanAndMonicaStatementJob(String name, BankAccountStatement account, int amountToSpend) {
    this.name = name;
    this.account = account;
    this.amountToSpend = amountToSpend;
  }

  public void run() {
    if (account.getBalance() >= amountToSpend) {
      account.spend(amountToSpend, name);
      System.out.println(name + " finishes spending");
    } else {
      System.out.println("Sorry, not enough for " + name);
    }
  }
}

class StatementReader implements Runnable {
  private final String name;
  private final BankAccountStatement account;

  StatementReader(String name, BankAccountStatement account) {
    this.name = name;
    this.account = account;
  }

  @Override
  public void run() {
    for (Transaction transaction : account.getStatement()) {
      System.out.println(name + " read " + transaction);
    }
  }
}

class BankAccountStatement {
  private final List<Transaction> statement = new CopyOnWriteArrayList<>();

  public BankAccountStatement() {
    statement.add(new Transaction("Initial Balance", 0, 100));
  }

  public int getBalance() {
    int lastEntry = statement.size() - 1;
    return statement.get(lastEntry).getCurrentBalance();
  }

  public void spend(int amount, String name) {
    int newBalance = getBalance() - amount;
    Transaction transaction = new Transaction(name, amount, newBalance);
    statement.add(transaction);
  }

  public List<Transaction> getStatement() {
    return statement;
  }
}

class Transaction {
  private final String name;
  private final int amount;
  private final int currentBalance;

  Transaction(String name, int amount, int currentBalance) {
    this.name = name;
    this.amount = amount;
    this.currentBalance = currentBalance;
  }

  public String getName() {
    return name;
  }

  public int getAmount() {
    return amount;
  }

  public int getCurrentBalance() {
    return currentBalance;
  }

  @Override
  public String toString() {
    return "Transaction{" +
            "name='" + name + '\'' +
            ", amount=" + amount +
            ", currentBalance=" + currentBalance +
            '}';
  }
}


