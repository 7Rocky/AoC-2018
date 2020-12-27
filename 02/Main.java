package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class Main {

  public static int countIDWithOcurrences(List<HashMap<String, Integer>> letterCounts, int number) {
    int count = 0;

    for (HashMap<String, Integer> hM : letterCounts) {
      for (int n : hM.values()) {
        if (n == number) {
          count++;
          break;
        }
      }
    }

    return count;
  }

  public static int charDifference(String s1, String s2) {
    int differences = 0;
    int length = s1.length() < s2.length() ? s1.length() : s2.length();
  
    for (int i = 0; i < length; i++) {
      if (s1.charAt(i) != s2.charAt(i)) {
        differences++;
      }
    }

    differences += Math.abs(s1.length() - s2.length());

    return differences;
  }

  public static String getCommonLetters(String s1, String s2) {
    StringBuilder commonLetters = new StringBuilder();
    int length = s1.length() < s2.length() ? s1.length() : s2.length();
  
    for (int i = 0; i < length; i++) {
      if (s1.charAt(i) == s2.charAt(i)) {
        commonLetters.append(s1.charAt(i));
      }
    }

    return commonLetters.toString();
  }

  public static void main(String[] args) {
    BufferedReader bufferedReader = null;

    try {
      bufferedReader = new BufferedReader(new FileReader("./input.txt"));
    } catch (FileNotFoundException e) { }

    List<String> boxIDs = new ArrayList<>();

    bufferedReader.lines().forEach(line -> boxIDs.add(line));

    List<HashMap<String, Integer>> boxIDLetterCounts = new ArrayList<>();

    for (int i = 0; i < boxIDs.size(); i++) {
      boxIDLetterCounts.add(new HashMap<String, Integer>());
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

    int count2 = countIDWithOcurrences(boxIDLetterCounts, 2);
    int count3 = countIDWithOcurrences(boxIDLetterCounts, 3);

    System.out.println("Checksum (1): " + (count2 * count3));

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

    System.out.println("Common letters (2): " + commonLetters);
  }

}
