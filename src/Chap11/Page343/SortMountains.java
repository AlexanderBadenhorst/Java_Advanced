package Chap11.Page343;

import java.util.*;   // List, ArrayList, Comparator, etc.

public class SortMountains {
    public static void main(String[] args) {
        new SortMountains().go();
    }

    public void go() {
        List<Mountain> mountains = new ArrayList<>();
        mountains.add(new Mountain("Longs", 14255));
        mountains.add(new Mountain("Elbert", 14433));
        mountains.add(new Mountain("Maroon", 14156));
        mountains.add(new Mountain("Castle", 14265));
        System.out.println("as entered:\n" + mountains);

        // by NAME (alphabetical, ascending)
        mountains.sort((one, two) -> one.getName().compareTo(two.getName()));
        System.out.println("by name:\n" + mountains);

        // by HEIGHT (numeric, descending)
        mountains.sort((one, two) -> Integer.compare(two.getHeight(), one.getHeight()));
        System.out.println("by height:\n" + mountains);
    }
}

class Mountain {
    private String name;
    private int height;

    Mountain(String name, int height) {
        this.name = name;
        this.height = height;
    }

    public String getName() {
        return name;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return name + " " + height;
    }
}
