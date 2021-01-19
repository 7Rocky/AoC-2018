package day_12;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

  static String initialState = "";
  static Map<String, String> changes = new TreeMap<>();

  public static void main(String[] args) {
    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      bufferedReader.lines().forEach(Main::setPoints);
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.print("Sum of position of pots after 20 generations (1): ");
    System.out.println(Main.countPotsInGeneration(20, '#'));

    System.out.print("Sum of position of pots after 50000000000 generations (2): ");
    System.out.println(Main.countPotsInGeneration(50000000000L, '#'));
  }

  private static void setPoints(String line) {
    if (line.contains("initial state: ")) {
      Main.initialState = line.substring("initial state: ".length());
      return;
    }

    Pattern pattern = Pattern.compile("(.+)\\s+=>\\s+(.+)");
    Matcher matcher = pattern.matcher(line);

    while (matcher.find()) {
      String llcrr = matcher.group(1);
      String n = matcher.group(2);

      Main.changes.put(llcrr, n);
    }
  }

  private static long countPotsInGeneration(long generation, char pot) {
    String next = Main.initialState;

    long increase = 0;
    int i = 0;
    boolean arrivedConstantIncrease = false;
    long newCount = 0L;

    while (i < generation) {
      long previousCount = Main.countOcurrences(next, pot, i);
      next = Main.nextGeneration(next);
      newCount = Main.countOcurrences(next, pot, i + 1);

      if (increase == newCount - previousCount) {
        arrivedConstantIncrease = true;
        break;
      }

      increase = newCount - previousCount;
      i++;
    }

    if (arrivedConstantIncrease) {
      return newCount + (generation - i - 1) * increase;
    }

    return Main.countOcurrences(next, pot, (int) generation);
  }

  private static String nextGeneration(String state) {
    state = "..." + state + "...";
    var next = new StringBuilder();

    for (int i = 0; i < state.length() - 4; i++) {
      next.append(Main.changes.get(state.substring(i, i + 5)));
    }

    return next.toString();
  }

  private static long countOcurrences(String state, char pot, int offset) {
    List<Integer> numbers = new ArrayList<>();

    for (int i = 0; i < state.length(); i++) {
      numbers.add(i);
    }

    return numbers.stream().filter(n -> state.charAt(n) == pot).mapToInt(n -> n - offset).sum();
  }

}
