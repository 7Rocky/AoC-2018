package day_02;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Main {

  public static void main(String[] args) {
    List<String> boxIDs = new ArrayList<>();

    try (BufferedReader bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      bufferedReader.lines().forEach(boxIDs::add);
    } catch (IOException e) {
      e.printStackTrace();
    }

    List<Map<String, Integer>> boxIDLetterCounts = Main.getBoxIDLetterCounts(boxIDs);

    int count2 = countIDWithOcurrences(boxIDLetterCounts, 2);
    int count3 = countIDWithOcurrences(boxIDLetterCounts, 3);

    System.out.print("Checksum (1): ");
    System.out.println(count2 * count3);

    String commonLetters = "";

    for (String id1 : boxIDs) {
      for (String id2 : boxIDs) {
        if (!id1.equals(id2) && charDifference(id1, id2) == 1) {
          commonLetters = getCommonLetters(id1, id2);
          break;
        }
      }

      if (commonLetters.length() > 0) {
        break;
      }
    }

    System.out.print("Common letters (2): ");
    System.out.println(commonLetters);
  }

  private static List<Map<String, Integer>> getBoxIDLetterCounts(List<String> boxIDs) {
    List<Map<String, Integer>> boxIDLetterCounts = new ArrayList<>();

    for (int i = 0; i < boxIDs.size(); i++) {
      boxIDLetterCounts.add(new HashMap<>());
      Set<Character> charSet = new TreeSet<>();
      boxIDs.get(i).chars().forEach(c -> charSet.add(Character.valueOf((char) c)));

      for (char letter : charSet) {
        int count = (int) boxIDs.get(i).chars().filter(c -> c == letter).count();

        if (boxIDLetterCounts.get(i).get(String.valueOf(letter)) == null) {
          boxIDLetterCounts.get(i).put(String.valueOf(letter), count);
        } else {
          int current = boxIDLetterCounts.get(i).get(String.valueOf(letter));
          boxIDLetterCounts.get(i).put(String.valueOf(letter), current + 1);
        }
      }
    }

    return boxIDLetterCounts;
  }

  private static int countIDWithOcurrences(List<Map<String, Integer>> letterCounts, int number) {
    int count = 0;

    for (Map<String, Integer> m : letterCounts) {
      for (int n : m.values()) {
        if (n == number) {
          count++;
          break;
        }
      }
    }

    return count;
  }

  private static int charDifference(String s1, String s2) {
    int differences = 0;
    int length = s1.length() < s2.length() ? s1.length() : s2.length();

    for (int i = 0; i < length; i++) {
      if (s1.charAt(i) != s2.charAt(i)) {
        differences++;
      }
    }

    return differences + Math.abs(s1.length() - s2.length());
  }

  private static String getCommonLetters(String s1, String s2) {
    StringBuilder commonLetters = new StringBuilder();
    int length = s1.length() < s2.length() ? s1.length() : s2.length();

    for (int i = 0; i < length; i++) {
      if (s1.charAt(i) == s2.charAt(i)) {
        commonLetters.append(s1.charAt(i));
      }
    }

    return commonLetters.toString();
  }

}
