package day_23;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

  public static void main(String[] args) {
    TreeSet<Nanobot> nanobots = new TreeSet<>();

    Pattern pattern = Pattern.compile("pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(-?\\d+)");

    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      bufferedReader.lines().forEach(line -> {
        Matcher matcher = pattern.matcher(line);
        matcher.find();

        int x = Integer.parseInt(matcher.group(1));
        int y = Integer.parseInt(matcher.group(2));
        int z = Integer.parseInt(matcher.group(3));
        int range = Integer.parseInt(matcher.group(4));

        nanobots.add(new Nanobot(new int[] {x, y, z}, range));
      });

    } catch (IOException e) {
      e.printStackTrace();
    }

    Nanobot strongestNanobot = nanobots.first();

    int inRange = (int) nanobots.stream().filter(n -> n.isInRangeOf(strongestNanobot)).count();

    System.out.println("Number of nanobots in range of the strongest nanobot (1): " + inRange);

    Queue<Integer[]> queue = new PriorityQueue<>(Comparator.comparingInt(l -> l[0]));

    for (Nanobot nanobot : nanobots) {
      int distance = Arrays.stream(nanobot.getPos()).map(Math::abs).sum();
      int range = nanobot.getRange();

      queue.add(new Integer[] {Math.max(0, distance - range + 1), 1});
      queue.add(new Integer[] {distance + range + 1, -1});
    }

    int count = 0;
    int maxCount = 0;
    int best = 0;

    while (!queue.isEmpty()) {
      Integer[] l = queue.poll();
      count += l[1];

      if (maxCount < count) {
        maxCount = count;
        best = l[0];
      }
    }

    System.out.println("Best position to teleport (2): " + best);
  }

}


class Nanobot implements Comparable<Nanobot> {

  private int[] pos;
  private int range;

  public Nanobot(int[] pos, int range) {
    this.setPos(pos);
    this.setRange(range);
  }

  public boolean isInRangeOf(Nanobot nanobot) {
    int[] nanobotPos = nanobot.getPos();
    int distance = 0;

    for (int i = 0; i < nanobotPos.length; i++) {
      distance += Math.abs(this.pos[i] - nanobotPos[i]);
    }

    return distance <= nanobot.getRange();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Nanobot) {
      Nanobot nanobot = (Nanobot) object;

      return this.toString().equals(nanobot.toString());
    }

    return false;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  @Override
  public int compareTo(Nanobot nanobot) {
    return nanobot.getRange() - this.range;
  }

  @Override
  public String toString() {
    return "pos=<" + pos[0] + "," + pos[1] + "," + pos[2] + ">, r=" + range;
  }

  public int[] getPos() {
    return pos;
  }

  public int getRange() {
    return range;
  }

  public void setPos(int[] pos) {
    this.pos = pos;
  }

  public void setRange(int range) {
    this.range = range;
  }

}
