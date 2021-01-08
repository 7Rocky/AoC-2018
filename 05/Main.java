package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

  static Pattern pattern = null;

  public static Pattern generatePattern() {
    if (pattern != null) return pattern;

    StringBuilder patternString = new StringBuilder("(");

    for (char c = 'A'; c <= 'Z'; c++) {
      if (c != 'A') patternString.append('|');

      patternString.append(c).append((char) (c + 32))
        .append('|').append((char) (c + 32)).append(c);
    }

    patternString.append(')');

    pattern = Pattern.compile(patternString.toString());

    return pattern;
  }

  private static String reactPolymer(String polymer) {
    Matcher matcher = Main.generatePattern().matcher(polymer);

    while (matcher.find()) {
      polymer = polymer.replace(matcher.group(1), "");
      matcher = pattern.matcher(polymer);
    }

    return polymer;
  }

  public static void main(String[] args) {
    BufferedReader bufferedReader = null;
    String polymer = "";

    try {
      bufferedReader = new BufferedReader(new FileReader("input.txt"));
      polymer = bufferedReader.readLine();
    } catch (FileNotFoundException e) {
    } catch (IOException e) { }

    System.out.print("Units remaining (1): ");
    System.out.println(reactPolymer(polymer).length());

    int minLength = polymer.length();

    for (char c = 'A'; c <= 'Z'; c++) {
      String newPolymer = polymer.replace(String.valueOf(c), "")
                                 .replace(String.valueOf((char) (c + 32)), "");

      int newLength = reactPolymer(newPolymer).length();

      if (newLength < minLength) minLength = newLength;
    }

    System.out.print("Shortest polymer length (2): ");
    System.out.println(minLength);
  }

}
