package Chap18.Exercises;

class AddLowerCaseJob implements Runnable {
  private final IData data;
  AddLowerCaseJob(IData data) {
    this.data = data;
  }

  public void run() {
    char letter = 'a';

    for (int i = 0; i < 26; i++) {
      data.addLetter(letter++);
      try {
        Thread.sleep(50);
      } catch (InterruptedException ignored) {
      }
    }
    // this can throw a ConcurrentModificationException
    System.out.println(Thread.currentThread().getName() + data.getLetters());
    System.out.println(Thread.currentThread().getName() + " getLetters().size() = " + data.getLetters().size());
  }

}
