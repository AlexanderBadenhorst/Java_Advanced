package Chap18.Exercises;

class AddUpperCaseJob implements Runnable {
  private final IData data;
  AddUpperCaseJob(IData data) {
    this.data = data;
  }
  public void run() {
    char letter = 'A';
    for (int i = 0; i < 26; i++) {
      data.addLetter(letter++);
      try {
        Thread.sleep(50);
      } catch (InterruptedException ignored) {
      }
    }
    System.out.println(Thread.currentThread().getName() + data.getLetters());
    System.out.println(Thread.currentThread().getName() + " getLetters().size() = " + data.getLetters().size());
  }

}
