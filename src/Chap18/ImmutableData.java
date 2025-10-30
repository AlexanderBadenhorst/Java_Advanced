package Chap18;

public class ImmutableData {
  final String name;
  final int value;

  public ImmutableData(String name, int value) {
    this.name = name;
    this.value = value;
  }

  public String getName() { return name; }

  public int getValue() { return value; }
}
