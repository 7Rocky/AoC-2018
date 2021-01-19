package day_10;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import java.util.function.Predicate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.stream.Collectors;

public class Main {

  static final int GUESED_TIME = 10577;
  static List<Point> points = new ArrayList<>();

  public static void main(String[] args) {
    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      bufferedReader.lines().forEach(Main::setPoints);
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (int t = 0; t < Main.GUESED_TIME; t++) {
      Main.points.stream().forEach(Point::updatePosition);
    }

    Main.printPoints(Main.GUESED_TIME);
  }

  private static void setPoints(String line) {
    Pattern pattern = Pattern
        .compile("position=<\\s*(-?\\d+),\\s*(-?\\d+)> velocity=<\\s*(-?\\d+),\\s*(-?\\d+)>");
    Matcher matcher = pattern.matcher(line);

    while (matcher.find()) {
      int px = Integer.parseInt(matcher.group(1));
      int py = Integer.parseInt(matcher.group(2));
      int vx = Integer.parseInt(matcher.group(3));
      int vy = Integer.parseInt(matcher.group(4));

      List<Integer> position = new ArrayList<>(2);
      position.add(px);
      position.add(py);

      List<Integer> velocity = new ArrayList<>(2);
      velocity.add(vx);
      velocity.add(vy);

      Main.points.add(new Point(position, velocity));
    }
  }

  private static void printPoints(int time) {
    List<Point> printablePoints = Main.points.stream()
        .filter(p -> p.countSurroundingPoints(Main.points, 3) > 3).collect(Collectors.toList());

    if (!printablePoints.isEmpty()) {
      int minX = printablePoints.stream().map(p -> p.getPosition().get(0)).min(Integer::compareTo)
          .orElse(-1);
      int maxX = printablePoints.stream().map(p -> p.getPosition().get(0)).max(Integer::compareTo)
          .orElse(-1);
      int minY = printablePoints.stream().map(p -> p.getPosition().get(1)).min(Integer::compareTo)
          .orElse(-1);
      int maxY = printablePoints.stream().map(p -> p.getPosition().get(1)).max(Integer::compareTo)
          .orElse(-1);

      List<String> grid = Main.generateGrid(minY, maxY);

      Main.putPoints(printablePoints, grid, minX, maxX, minY);

      System.out.println("Time (2): " + time + "\n\nWord (1):\n");

      grid.stream().filter(Predicate.not(String::isEmpty)).forEach(System.out::println);
    }
  }

  private static List<String> generateGrid(int minY, int maxY) {
    List<String> grid = new ArrayList<>();

    for (int y = minY; y <= maxY; y++) {
      grid.add("");
    }

    return grid;
  }

  private static void putPoints(List<Point> points, List<String> grid, int minX, int maxX,
      int minY) {
    points.stream().forEach(point -> {
      int normX = point.getPosition().get(0) - minX;
      int normY = point.getPosition().get(1) - minY;

      String row = grid.get(normY);

      if (row.isEmpty()) {
        row = ".".repeat(normX) + "#" + ".".repeat(maxX - (normX + minX));
      } else {
        row = row.substring(0, normX) + "#" + row.substring(normX + 1);
      }

      grid.set(normY, row);
    });
  }

}


class Point {

  private List<Integer> position;
  private List<Integer> velocity;

  public Point(List<Integer> position, List<Integer> velocity) {
    this.setPosition(position);
    this.setVelocity(velocity);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Point) {
      Point point = (Point) object;

      return point.getPosition().equals(position) && point.getVelocity().equals(velocity);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return (position.toString() + velocity.toString()).hashCode();
  }

  public int countSurroundingPoints(List<Point> points, int distance) {
    return (int) points.stream().filter(p -> this.isSurroundingPoint(p, distance)).count();
  }

  public boolean isSurroundingPoint(Point point, int distance) {
    return !point.equals(this)
        && (Math.abs(position.get(0) - point.getPosition().get(0)) <= distance
            && Math.abs(position.get(1) - point.getPosition().get(1)) <= distance);
  }

  public void updatePosition() {
    position.set(0, position.get(0) + velocity.get(0));
    position.set(1, position.get(1) + velocity.get(1));
  }

  public List<Integer> getPosition() {
    return position;
  }

  public List<Integer> getVelocity() {
    return velocity;
  }

  public void setPosition(List<Integer> position) {
    this.position = position;
  }

  public void setVelocity(List<Integer> velocity) {
    this.velocity = velocity;
  }

}
