package day_05;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {

  public static void main(String[] args) {
    String polymer = "";

    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      polymer = bufferedReader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Units remaining (1): " + reactPolymer(new StringBuilder(polymer)).length());

    int minLength = polymer.length();

    for (char c = 'A'; c <= 'Z'; c++) {
      String newPolymer = polymer.replaceAll("(?i)" + c, "");

      int newLength = reactPolymer(new StringBuilder(newPolymer)).length();

      if (newLength < minLength) {
        minLength = newLength;
      }
    }

    System.out.println("Shortest polymer length (2): " + minLength);
  }

  private static String reactPolymer(StringBuilder polymer) {
    boolean removed = true;
    int lowerUpperDifference = 'a' - 'A';

    while (removed) {
      removed = false;

      for (int i = 1; i < polymer.length(); i++) {
        if ((polymer.charAt(i - 1) ^ polymer.charAt(i)) == lowerUpperDifference) {
          polymer.delete(i - 1, i + 1);
          removed = true;
          break;
        }
      }
    }

    return polymer.toString();
  }

}
