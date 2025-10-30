package Chap16.Exercises;

import java.io.*;

public class Fragments {
  void fragment1() {
//    FileReader fileReader = new FileReader();
//    BufferedReader reader = new BufferedReader(fileReader);
  }

  void fragment2() throws IOException {
    FileOutputStream f = new FileOutputStream("Foo.ser");
    ObjectOutputStream os = new ObjectOutputStream(f);
  }

  void fragment3(File file) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    while ((line = reader.readLine()) != null) {
      makeCard(line);
    }
  }

  void fragment4() throws IOException, ClassNotFoundException {
    FileOutputStream f = new FileOutputStream("Game.ser");
//    ObjectInputStream is = new ObjectInputStream(f);
//    GameCharacter oneAgain = (GameCharacter) is.readObject();
  }

  private void makeCard(String line) {

  }
}
