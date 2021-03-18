package day_17;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Map;
import java.util.TreeMap;

import java.util.Map.Entry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

  static Map<Integer, Map<Integer, Character>> map = new TreeMap<>();
  static int[] borders = new int[] {Integer.MAX_VALUE, 0, Integer.MAX_VALUE, 0};
  static Pattern pattern = Pattern.compile("([xy])=(\\d+), [xy]=(\\d+)\\.\\.(\\d+)");

  public static void main(String[] args) {
    Main.initMap();
    Main.setMap();

    Main.map.get(Main.borders[2]).put(500, '+');
    Main.map.get(Main.borders[2] + 1).put(500, '|');

    while (Main.springWater());

    int wetSand = Main.getCountOf('|');
    int water = Main.getCountOf('~');

    System.out.println("Total wet sand and water (1): " + (wetSand + water));
    System.out.println("Total water (2): " + water);
  }

  private static int getCountOf(char tile) {
    return Main.map.values().stream()
        .map(row -> (int) row.values().stream().filter(c -> c == tile).count()).reduce(Integer::sum)
        .orElse(-1);
  }

  private static boolean springWater() {
    boolean moved = false;

    for (Entry<Integer, Map<Integer, Character>> row : Main.map.entrySet()) {
      int y = row.getKey();

      for (Entry<Integer, Character> tile : row.getValue().entrySet()) {
        int x = tile.getKey();

        if (tile.getValue() == '~') {
          moved |= Main.expandWater(x, y);
        }

        if (tile.getValue() == '|') {
          if (Main.hasWalls(Main.map.get(y), x)) {
            Main.map.get(y).put(x, '~');
            moved = true;
          }

          moved |= Main.wetSand(x, y);
        }
      }
    }

    return moved;
  }

  private static boolean wetSand(int x, int y) {
    if (y >= Main.borders[3]) {
      return false;
    }

    char nextDown = Main.map.get(y + 1).get(x);

    if (nextDown == '.') {
      Main.map.get(y + 1).put(x, '|');
      return true;
    }

    if (x > Main.borders[0] && x < Main.borders[1]) {
      char nextRight = Main.map.get(y).get(x + 1);
      char nextLeft = Main.map.get(y).get(x - 1);

      if (nextDown == '#') {
        return Main.nextWall(nextRight, nextLeft, x, y);
      }

      if (nextDown == '|') {
        return Main.nextWet(nextRight, nextLeft, x, y);
      }

      if (nextDown == '~') {
        return Main.nextWater(nextRight, nextLeft, x, y);
      }

      return false;
    }

    return false;
  }

  private static boolean nextWall(char right, char left, int x, int y) {
    if (right == '.') {
      Main.map.get(y).put(x + 1, '|');
      return true;
    }

    if (left == '.') {
      Main.map.get(y).put(x - 1, '|');
      return true;
    }

    return false;
  }

  private static boolean nextWet(char right, char left, int x, int y) {
    if (right == '.' && left == '|' && Main.map.get(y + 1).get(x + 1) == '#') {
      Main.map.get(y).put(x + 1, '|');
      return true;
    }

    if (left == '.' && right == '|' && Main.map.get(y + 1).get(x - 1) == '#') {
      Main.map.get(y).put(x - 1, '|');
      return true;
    }

    return false;
  }

  private static boolean nextWater(char right, char left, int x, int y) {
    if (right == '.') {
      Main.map.get(y).put(x + 1, '|');
      return true;
    } else if (left == '.') {
      Main.map.get(y).put(x - 1, '|');
      return true;
    } else if (right == '~' || left == '.') {
      Main.map.get(y).put(x, '~');
      return true;
    }

    return false;
  }

  private static boolean expandWater(int x, int y) {
    if (x < Main.borders[1] && Main.map.get(y).get(x + 1) == '.') {
      Main.map.get(y).put(x + 1, '~');
      return true;
    }

    if (x > Main.borders[0] && Main.map.get(y).get(x - 1) == '.') {
      Main.map.get(y).put(x - 1, '~');
      return true;
    }

    return false;
  }

  private static boolean hasWalls(Map<Integer, Character> row, int x) {
    int d = 1;

    while (x + d <= Main.borders[1]) {
      if (row.get(x + d) == '#') {
        break;
      } else if (row.get(x + d) == '.') {
        return false;
      }

      d++;
    }

    d = 1;

    while (x - d >= Main.borders[0]) {
      if (row.get(x - d) == '#') {
        return true;
      } else if (row.get(x - d) == '.') {
        break;
      }

      d++;
    }

    return false;
  }

  private static void initMap() {
    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      String line = bufferedReader.readLine();

      while (line != null) {
        Main.updateBorders(Main.pattern.matcher(line));
        line = bufferedReader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    Main.borders[0]--;
    Main.borders[1]++;
    Main.borders[2]--;

    for (int j = borders[2]; j <= borders[3]; j++) {
      Map<Integer, Character> row = new TreeMap<>();

      for (int i = borders[0]; i <= borders[1]; i++) {
        row.put(i, '.');
      }

      Main.map.put(j, row);
    }
  }

  private static void updateBorders(Matcher matcher) {
    matcher.find();

    int x = Integer.parseInt(matcher.group(2));
    int y = Integer.parseInt(matcher.group(3));

    if (matcher.group(1).equals("y")) {
      int aux = x;
      x = y;
      y = aux;
    }

    if (Main.borders[0] > x) {
      Main.borders[0] = x;
    }

    if (Main.borders[1] < x) {
      Main.borders[1] = x;
    }

    if (Main.borders[2] > y) {
      Main.borders[2] = y;
    }

    if (Main.borders[3] < y) {
      Main.borders[3] = y;
    }
  }

  private static void setMap() {
    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      String line = bufferedReader.readLine();

      while (line != null) {
        Main.setClays(Main.pattern.matcher(line));
        line = bufferedReader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void setClays(Matcher matcher) {
    matcher.find();

    if (matcher.group(1).equals("x")) {
      int x = Integer.parseInt(matcher.group(2));
      int yLow = Integer.parseInt(matcher.group(3));
      int yHigh = Integer.parseInt(matcher.group(4));

      for (int i = yLow; i <= yHigh; i++) {
        Main.map.get(i).put(x, '#');
      }
    } else {
      int y = Integer.parseInt(matcher.group(2));
      int xLow = Integer.parseInt(matcher.group(3));
      int xHigh = Integer.parseInt(matcher.group(4));

      for (int i = xLow; i <= xHigh; i++) {
        Main.map.get(y).put(i, '#');
      }
    }
  }

}
