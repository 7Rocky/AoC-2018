package day_22;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class Main {

  static int depth;
  static int[] target;
  static Map<Point, Integer> geologicalIndeces = new HashMap<>();
  static Map<Point, Integer> erosionLevels = new HashMap<>();
  static Map<Point, Integer> types = new HashMap<>();
  static Map<Point, Integer> minutes = new HashMap<>();

  public static void main(String[] args) {
    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      Main.depth = Integer.parseInt(bufferedReader.readLine().split(" ")[1]);
      Main.target = Arrays.asList(bufferedReader.readLine().split(" ")[1].split(",")).stream()
          .mapToInt(Integer::parseInt).toArray();
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (int i = 0; i <= Main.target[0]; i++) {
      for (int j = 0; j <= Main.target[1]; j++) {
        Point point = new Point(i, j);

        Main.setGeologicalIndex(point);
        Main.setErosionLevel(point);
        Main.setType(point);
      }
    }

    System.out.print("Total risk level (1): ");
    System.out.println(Main.types.values().stream().mapToInt(Integer::valueOf).sum());

    for (int i = 0; i <= 20 * Main.target[0]; i++) {
      for (int j = 0; j <= 20 * Main.target[1]; j++) {
        Point point = new Point(i, j);

        Main.setGeologicalIndex(point);
        Main.setErosionLevel(point);
        Main.setType(point);
      }
    }

    Point orig = new Point(0, 0, 0, Item.TORCH);
    Point dest = new Point(Main.target[0], Main.target[1], -1, Item.TORCH);

    System.out.println("Minimum time to reach target (2): " + breadthFirstSearch(orig, dest));
  }

  private static void setGeologicalIndex(Point point) {
    if ((point.getX() + point.getY() == 0)
        || (point.getX() == Main.target[0] && point.getY() == Main.target[1])) {
      Main.geologicalIndeces.put(point, 0);
    } else if (point.getX() == 0) {
      Main.geologicalIndeces.put(point, point.getY() * 48271);
    } else if (point.getY() == 0) {
      Main.geologicalIndeces.put(point, point.getX() * 16807);
    } else {
      int e1 = Main.erosionLevels.get(new Point(point.getX() - 1, point.getY()));
      int e2 = Main.erosionLevels.get(new Point(point.getX(), point.getY() - 1));

      Main.geologicalIndeces.put(point, e1 * e2);
    }
  }

  private static void setErosionLevel(Point point) {
    Main.erosionLevels.put(point, (Main.geologicalIndeces.get(point) + Main.depth) % 20183);
  }

  private static void setType(Point point) {
    Main.types.put(point, Main.erosionLevels.get(point) % 3);
  }

  public static int breadthFirstSearch(Point orig, Point dest) {
    Map<Point, Integer> visited = new HashMap<>();
    Queue<Point> queue = new PriorityQueue<>();

    queue.add(orig);
    visited.put(orig, 0);

    while (!queue.isEmpty()) {
      Point node = queue.poll();

      if (dest.equals(node)) {
        return node.getMinutes();
      }

      for (Point next : Main.getPossiblePositions(node)) {
        next.setMinutes(node.getMinutes() + next.getMinutes() + 1);

        if (!visited.containsKey(next) || visited.get(next) > next.getMinutes()) {
          queue.add(next);
          visited.put(next, next.getMinutes());
        }
      }
    }

    return Integer.MAX_VALUE;
  }

  private static List<Point> getPossiblePositions(Point point) {
    List<Point> positions = new ArrayList<>();

    positions.add(Main.changeItem(new Point(point)));

    for (Point p : Main.getAdjacentPoints(point)) {
      if ((p.getX() >= 0 && p.getY() >= 0 && Main.types.containsKey(p))
          && ((point.getItem().equals(Item.NEITHER) && Main.types.get(p) != 0)
              || (point.getItem().equals(Item.TORCH) && Main.types.get(p) != 1)
              || (point.getItem().equals(Item.CLIMBING) && Main.types.get(p) != 2))) {
        p.setItem(point.getItem());
        positions.add(p);
      }
    }

    return positions;
  }

  public static Point changeItem(Point point) {
    Item it = point.getItem();
    int type = Main.types.get(new Point(point.getX(), point.getY()));

    if (type == 0 && it.equals(Item.TORCH)) {
      point.setItem(Item.CLIMBING);
    }

    if (type == 0 && it.equals(Item.CLIMBING)) {
      point.setItem(Item.TORCH);
    }

    if (type == 1 && it.equals(Item.NEITHER)) {
      point.setItem(Item.CLIMBING);
    }

    if (type == 1 && it.equals(Item.CLIMBING)) {
      point.setItem(Item.NEITHER);
    }

    if (type == 2 && it.equals(Item.NEITHER)) {
      point.setItem(Item.TORCH);
    }

    if (type == 2 && it.equals(Item.TORCH)) {
      point.setItem(Item.NEITHER);
    }

    point.setMinutes(6);

    return point;
  }

  public static List<Point> getAdjacentPoints(Point point) {
    int x = point.getX();
    int y = point.getY();

    return Arrays.asList(new Point(x, y - 1), new Point(x - 1, y), new Point(x + 1, y),
        new Point(x, y + 1));
  }

}


enum Item {
  TORCH, CLIMBING, NEITHER
}


class Point implements Comparable<Point> {

  private int x;
  private int y;
  private int minutes = 0;
  private Item item = Item.TORCH;

  public Point(int x, int y) {
    this.setX(x);
    this.setY(y);
  }

  public Point(int x, int y, int minutes, Item item) {
    this(x, y);
    this.setMinutes(minutes);
    this.setItem(item);
  }

  public Point(Point point) {
    this(point.getX(), point.getY(), point.getMinutes(), point.getItem());
  }

  @Override
  public String toString() {
    return this.x + "," + this.y + "." + this.item;
  }

  @Override
  public int compareTo(Point point) {
    return this.minutes - point.getMinutes();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Point) {
      Point point = (Point) object;
      return this.x == point.getX() && this.y == point.getY() && this.item == point.getItem();
    }

    return false;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getMinutes() {
    return minutes;
  }

  public Item getItem() {
    return item;
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public void setMinutes(int minutes) {
    this.minutes = minutes;
  }

  public void setItem(Item item) {
    this.item = item;
  }

}
