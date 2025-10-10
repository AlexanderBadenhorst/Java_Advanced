package Chap12.Exercises.Page386;

import java.util.*;
import java.util.stream.*;

public class CoffeeOrder {
    public static void main(String[] args) {

        List<String> coffees = List.of("Cappuccino", "Americano", "Espresso",
                "Cortado", "Mocha", "Cappuccino",
                "Flat White", "Latte");

        List<String> coffeesEndingInO = coffees.stream()
                .filter(s -> s.endsWith("o"))

                .sorted()

                .distinct()

                .collect(Collectors.toList());

        System.out.println(coffeesEndingInO);
        // Output: [Americano, Cappuccino, Cortado]
    }
}

/* DEBUGGER WALKTHROUGH:
 *
 * BREAKPOINT 1 - After list creation:
 * coffees = [Cappuccino, Americano, Espresso, Cortado, Mocha,
 *            Cappuccino, Flat White, Latte]
 *
 * BREAKPOINT 2 - Inside filter (for each element):
 * - "Cappuccino".endsWith("o") → true ✓
 * - "Americano".endsWith("o") → true ✓
 * - "Espresso".endsWith("o") → true ✓
 * - "Cortado".endsWith("o") → true ✓
 * - "Mocha".endsWith("o") → false ✗
 * - "Cappuccino".endsWith("o") → true ✓
 * - "Flat White".endsWith("o") → false ✗
 * - "Latte".endsWith("o") → false ✗
 * After filter: [Cappuccino, Americano, Espresso, Cortado, Cappuccino]
 *
 * BREAKPOINT 3 - After sorted():
 * Stream contents: [Americano, Cappuccino, Cappuccino, Cortado, Espresso]
 *
 * BREAKPOINT 4 - After distinct():
 * Stream contents: [Americano, Cappuccino, Cortado, Espresso]
 *
 * BREAKPOINT 5 - After collect():
 * coffeesEndingInO = [Americano, Cappuccino, Cortado, Espresso]
 *
 * FINAL OUTPUT:
 * [Americano, Cappuccino, Cortado, Espresso]
 */