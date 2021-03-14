package day_20;

import java.awt.Point;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.HashMap;

public class Main {

  static String regex = "";
  static HashMap<Point, Integer> map = new HashMap<>();

  public static void main(String[] args) {
    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      Main.regex = bufferedReader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }

    Main.map.put(new Point(0, 0), 0);

    Main.routeRegex(new Point(0, 0));

    System.out.print("Shortest path to furthest room (1): ");
    System.out.println(Main.map.values().stream().max(Integer::compareTo).orElse(-1));

    System.out.print("Number of paths of length greater than or equal to 1000 (2): ");
    System.out.println(Main.map.values().stream().filter(s -> s >= 1000).count());
  }

  public static void routeRegex(Point pivot) {
    Point startingPoint = new Point(pivot);
    int steps = Main.map.get(startingPoint);

    while (!Main.regex.isEmpty()) {
      char dir = Main.regex.charAt(0);
      Main.regex = Main.regex.substring(1);

      switch (dir) {
        case '^':
          break;
        case 'E':
          pivot.setLocation(pivot.getX() + 1, pivot.getY());
          Main.map.putIfAbsent(new Point(pivot), ++steps);
          break;
        case 'W':
          pivot.setLocation(pivot.getX() - 1, pivot.getY());
          Main.map.putIfAbsent(new Point(pivot), ++steps);
          break;
        case 'N':
          pivot.setLocation(pivot.getX(), pivot.getY() + 1);
          Main.map.putIfAbsent(new Point(pivot), ++steps);
          break;
        case 'S':
          pivot.setLocation(pivot.getX(), pivot.getY() - 1);
          Main.map.putIfAbsent(new Point(pivot), ++steps);
          break;
        case '(':
          Main.routeRegex(pivot);
          steps = Main.map.get(pivot);
          break;
        case '|':
          pivot = new Point(startingPoint);
          steps = Main.map.get(startingPoint);
          break;
        default:
          return;
      }
    }
  }

}
