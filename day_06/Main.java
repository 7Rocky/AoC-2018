package day_06;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

  public static void main(String[] args) {
    Pattern pattern = Pattern.compile("(\\d+), (\\d+)");

    List<Coordinate> coordinates = new ArrayList<>();

    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      bufferedReader.lines().forEach(line -> {
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
          int x = Integer.parseInt(matcher.group(1));
          int y = Integer.parseInt(matcher.group(2));

          coordinates.add(new Coordinate(x, y));
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }

    int max = coordinates.stream().map(Coordinate::getX).max(Integer::compareTo).orElse(-1);
    int min = coordinates.stream().map(Coordinate::getX).min(Integer::compareTo).orElse(-1);

    Map<String, Integer> closestAreas = new HashMap<>();

    for (Coordinate c : coordinates) {
      int size = c.countClosestAreas(coordinates, min - 100, max + 100);

      closestAreas.put(c.toString(), size);
    }

    for (Coordinate c : coordinates) {
      int size = c.countClosestAreas(coordinates, min, max);

      if (closestAreas.get(c.toString()) != size)
        closestAreas.remove(c.toString());
    }

    int largestSize = closestAreas.values().stream().max(Integer::compareTo).orElse(-1);

    System.out.println("Size of the largest area (1): " + largestSize);

    int minimumDistance = 10000;
    int safeArea = 0;

    for (int x = min; x <= max; x++) {
      for (int y = min; y <= max; y++) {
        Coordinate coordinate = new Coordinate(x, y);
        int totalDistance = coordinates.stream().map(coordinate::distance).reduce(0, Integer::sum);

        if (minimumDistance > totalDistance)
          safeArea++;
      }
    }

    System.out.println("Size of the safe area (2): " + safeArea);
  }

}


class Coordinate {

  private int x;
  private int y;

  public Coordinate(int x, int y) {
    this.setX(x);
    this.setY(y);
  }

  public int countClosestAreas(List<Coordinate> coordinates, int min, int max) {
    int closestAreas = 0;

    for (int i = min; i <= max; i++) {
      for (int j = min; j <= max; j++) {
        boolean isClosest = true;
        Coordinate coordinate = new Coordinate(i, j);

        int d = this.distance(coordinate);

        for (Coordinate c : coordinates) {
          if (d > c.distance(coordinate) || (!this.equals(c) && d == c.distance(coordinate))) {
            isClosest = false;
            break;
          }
        }

        if (isClosest)
          closestAreas++;
      }
    }

    return closestAreas;
  }

  public int distance(Coordinate c) {
    return Math.abs(x - c.getX()) + Math.abs(y - c.getY());
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof Coordinate) {
      Coordinate c = (Coordinate) o;

      return x == c.getX() && y == c.getY();
    }

    return false;
  }

  @Override
  public int hashCode() {
    return (x + "," + y).hashCode();
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

}
