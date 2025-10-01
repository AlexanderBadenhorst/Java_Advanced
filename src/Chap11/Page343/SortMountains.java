package Chap11.Page343;

import java.util.*;   // Import everything from java.util (List, ArrayList, Comparator, etc.)

public class SortMountains {
    public static void main(String[] args) {
        new SortMountains().go();
    }

    public void go() {
        // Create a list of Mountain objects
        List<Mountain> mountains = new ArrayList<>();
        mountains.add(new Mountain("Longs", 14255));
        mountains.add(new Mountain("Elbert", 14433));
        mountains.add(new Mountain("Maroon", 14156));
        mountains.add(new Mountain("Castle", 14265));

        // Print the list as it was entered (no sorting yet)
        System.out.println("as entered:\n" + mountains);

        // --- Sort by NAME (alphabetical order) ---
        // We use a lambda expression that compares the "name" Strings of two mountains.
        // String.compareTo() handles alphabetical order.
        mountains.sort((one, two) -> one.getName().compareTo(two.getName()));
        System.out.println("by name:\n" + mountains);

        // --- Sort by HEIGHT (numeric, descending) ---
        // Use Integer.compare() to compare two int heights.
        // Note the arguments are reversed (two, one) → this ensures DESCENDING order.
        mountains.sort((one, two) -> Integer.compare(two.getHeight(), one.getHeight()));
        System.out.println("by height:\n" + mountains);
    }
}

// A simple Mountain class to hold name + height of a mountain
class Mountain {
    private String name;
    private int height;

    // Constructor to initialize the mountain’s name and height
    Mountain(String name, int height) {
        this.name = name;
        this.height = height;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for height
    public int getHeight() {
        return height;
    }

    // toString() defines how a Mountain is printed
    // Prints "Name Height", so the list looks nice in output.
    @Override
    public String toString() {
        return name + " " + height;
    }
}
