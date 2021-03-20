package day_25;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

public class Main {

  public static void main(String[] args) {
    List<int[]> points = new ArrayList<>();

    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      points.addAll(bufferedReader.lines().map(line -> line.split(","))
          .map(strings -> Arrays.stream(strings).mapToInt(Integer::parseInt).toArray())
          .collect(Collectors.toList()));
    } catch (IOException e) {
      e.printStackTrace();
    }

    List<List<int[]>> constelations = new ArrayList<>();

    while (!points.isEmpty()) {
      List<int[]> constelation = new ArrayList<>();
      constelation.add(points.get(0));
      int lastSize = 0;

      while (lastSize != constelation.size()) {
        lastSize = constelation.size();

        for (int[] point : points) {
          if (constelation.stream().anyMatch(p -> Main.manhattanDistance(p, point) <= 3)) {
            constelation.add(point);
          }
        }

        points.removeAll(constelation);
      }

      constelations.add(constelation);
    }

    System.out.println("Number of constelations formed (1): " + constelations.size());
  }

  public static int manhattanDistance(int[] p, int[] q) {
    int distance = 0;

    for (int i = 0; i < p.length; i++) {
      distance += Math.abs(p[i] - q[i]);
    }

    return distance;
  }

}
