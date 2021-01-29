package day_15;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import java.util.stream.Collectors;

public class Main {

  static List<String> map = new ArrayList<>();
  static SortedSet<Unit> units = new TreeSet<>();

  public static void main(String[] args) {
    int outcome = Main.getOutcome(1);

    System.out.println("Outcome of the combat (1): " + outcome);

    int currentAttack = 3;

    do {
      currentAttack += 3;
      Elf.setAttack(currentAttack);
      outcome = Main.getOutcome(2);
    } while (outcome == -1);

    for (int i = 1; i < 3; i++) {
      Elf.setAttack(currentAttack - i);
      int checkedOutcome = Main.getOutcome(2);

      if (checkedOutcome != -1) {
        outcome = checkedOutcome;
        break;
      }
    }

    System.out.println("Outcome of the all-Elves-alive combat (2): " + outcome);
  }

  private static int getOutcome(int level) {
    Main.map.clear();
    Main.units.clear();

    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      bufferedReader.lines().forEach(Main::setConfiguration);
    } catch (IOException e) {
      e.printStackTrace();
    }

    int rounds = 0;
    int numElves = (int) Main.units.stream().filter(Unit::isElf).count();

    while (!Main.allGoblins() && !Main.allElves()) {
      boolean isLast = Main.lifecycle();
      Main.reSortUnits();

      if (!isLast && (Main.allGoblins() || Main.allElves())) {
        break;
      }

      rounds++;
    }

    if (level == 2 && (!Main.allElves() || Main.units.size() != numElves)) {
      return -1;
    }

    return rounds * Main.units.stream().mapToInt(Unit::getHp).sum();
  }

  private static boolean allGoblins() {
    return Main.units.stream().filter(Unit::isAlive).allMatch(Unit::isGoblin);
  }

  private static boolean allElves() {
    return Main.units.stream().filter(Unit::isAlive).allMatch(Unit::isElf);
  }

  private static void setConfiguration(String line) {
    int y = Main.map.size();
    int goblinX = line.indexOf('G');
    int elfX = line.indexOf('E');

    while (goblinX != -1) {
      Main.units.add(new Goblin(new Point(goblinX, y)));
      line = line.substring(0, goblinX) + "." + line.substring(goblinX + 1);
      goblinX = line.indexOf('G');
    }

    while (elfX != -1) {
      Main.units.add(new Elf(new Point(elfX, y)));
      line = line.substring(0, elfX) + "." + line.substring(elfX + 1);
      elfX = line.indexOf('E');
    }

    Main.map.add(line);
  }

  private static boolean lifecycle() {
    boolean isLast = false;

    for (Unit unit : Main.units) {
      if (unit.isAlive()) {
        unit.calculateTarget();
        unit.move();
      }

      isLast = unit.equals(Main.units.last());

      if (Main.allGoblins() || Main.allElves()) {
        break;
      }
    }

    return isLast;
  }

  private static void reSortUnits() {
    SortedSet<Unit> newUnits = new TreeSet<>();
    Main.units.stream().filter(Unit::isAlive).forEach(newUnits::add);
    Main.units = newUnits;
  }

  public static void getFirstUnit(Unit unit, SortedSet<Unit> enemies) {
    Point best = new Point(0, 0, Integer.MAX_VALUE);
    boolean found = false;

    for (Unit enemy : enemies) {
      Point optimal = Main.breadthFirstSearch(unit, enemy);

      if (!optimal.equals(new Point(-1, -1))) {
        found = true;

        if (best.getSteps() > optimal.getSteps()) {
          best = optimal;
        }

        if (optimal.equals(unit.getPosition()) && optimal.getSteps() == 0) {
          best = optimal;
          break;
        }
      }
    }

    unit.setTarget(found ? best : null);
  }

  public static List<Point> getAdjacentPoints(Point point) {
    int x = point.getX();
    int y = point.getY();

    return Arrays.asList(new Point(x, y - 1), new Point(x - 1, y), new Point(x + 1, y),
        new Point(x, y + 1));
  }

  public static boolean isEmptySpace(Point point) {
    return Main.map.get(point.getY()).charAt(point.getX()) == '.';
  }

  private static Point breadthFirstSearch(Unit orig, Unit dest) {
    Point point = new Point(orig.getPosition().getX(), orig.getPosition().getY());

    Point optimal = new Point(-1, -1);
    int minSteps = Integer.MAX_VALUE;

    for (Point p : Main.getAdjacentPoints(dest.getPosition())) {
      if (Main.isEmptySpace(p) && Main.units.stream().filter(Unit::isAlive)
          .noneMatch(u -> !u.equals(orig) && p.equals(u.getPosition()))) {

        int steps = Main.breadthFirstSearch(point, p);

        if (minSteps > steps) {
          minSteps = steps;
          optimal = new Point(p.getX(), p.getY(), minSteps);
        }
      }
    }

    return optimal;
  }

  public static int breadthFirstSearch(Point orig, Point dest) {
    Set<Point> visited = new HashSet<>();
    LinkedList<Point> queue = new LinkedList<>();

    visited.add(orig);
    queue.add(orig);

    while (!queue.isEmpty()) {
      Point node = queue.poll();

      if (node.equals(dest)) {
        return node.getSteps();
      }

      for (Point next : Main.getPossiblePositions(node)) {
        if (!visited.contains(next)) {
          next.setSteps(node.getSteps() + 1);
          visited.add(next);
          queue.add(next);
        }
      }
    }

    return Integer.MAX_VALUE;
  }

  public static boolean isAvailable(Point point) {
    return Main.units.stream().filter(Unit::isAlive).map(Unit::getPosition)
        .noneMatch(point::equals);
  }

  private static List<Point> getPossiblePositions(Point point) {
    List<Point> positions = new ArrayList<>();

    for (Point p : Main.getAdjacentPoints(point)) {
      if (p.getX() + p.getY() > 1 && p.getX() < Main.map.get(p.getY()).length()
          && p.getY() < Main.map.size() && Main.isEmptySpace(p) && Main.isAvailable(p)) {

        positions.add(p);
      }
    }

    return positions;
  }

  public static Unit getSurroundingEnemy(Unit unit, SortedSet<Unit> enemies) {
    TreeSet<Unit> closest = enemies.stream().filter(Unit::isAlive).filter(unit::isSurrounding)
        .collect(Collectors.toCollection(() -> new TreeSet<>(Unit::compareTo)));

    Unit enemy = null;
    int hp = Integer.MAX_VALUE;

    for (Unit u : closest) {
      if (hp > u.getHp()) {
        hp = u.getHp();
        enemy = u;
      }
    }

    return enemy;
  }

}


class Point implements Comparable<Point> {

  private int x;
  private int y;
  private int steps = 0;

  public Point(int x, int y) {
    this.setX(x);
    this.setY(y);
  }

  public Point(int x, int y, int steps) {
    this(x, y);
    this.setSteps(steps);
  }

  public boolean isSurrounding(Point point) {
    return Math.abs(this.x - point.getX()) + Math.abs(this.y - point.getY()) < 2;
  }

  @Override
  public String toString() {
    return this.x + "," + this.y;
  }

  @Override
  public int compareTo(Point point) {
    if (this.y == point.getY()) {
      return this.x - point.getX();
    }

    return this.y - point.getY();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Point) {
      Point point = (Point) object;
      return this.x == point.getX() && this.y == point.getY();
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

  public int getSteps() {
    return steps;
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public void setSteps(int steps) {
    this.steps = steps;
  }

}


abstract class Unit implements Comparable<Unit> {

  protected Point position;
  protected Point target;
  protected int hp = 200;
  protected boolean alive = true;

  protected Unit(Point position) {
    this.setPosition(position);
  }

  protected abstract TreeSet<Unit> getEnemies();

  public void move() {
    if (this.target == null) {
      return;
    }

    if (this.position.equals(this.target)) {
      this.attack();
      return;
    }

    for (Point p : Main.getAdjacentPoints(this.position)) {
      if (Main.isEmptySpace(p)) {
        int steps = Main.breadthFirstSearch(p, this.target);

        if (steps == this.target.getSteps() - 1 && Main.isAvailable(p)) {
          this.setPosition(p);
          break;
        }
      }
    }

    if (this.position.equals(this.target)) {
      this.attack();
    }
  }

  public void attack() {
    Unit enemy = Main.getSurroundingEnemy(this, this.getEnemies());

    if (enemy == null) {
      this.calculateTarget();
      this.move();
    } else {
      enemy.isAttacked(this.getAttack());
    }
  }

  public void isAttacked(int attack) {
    this.hp -= attack;
    this.checkAlive();
  }

  public void checkAlive() {
    this.setAlive(this.hp > 0);
  }

  public void calculateTarget() {
    Main.getFirstUnit(this, this.getEnemies());
  }

  public boolean isSurrounding(Unit enemy) {
    return this.position.isSurrounding(enemy.getPosition());
  }

  @Override
  public String toString() {
    return "(" + this.position.toString() + ")";
  }

  @Override
  public int compareTo(Unit unit) {
    return this.position.compareTo(unit.getPosition());
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Goblin) {
      Goblin goblin = (Goblin) object;
      return this.position.equals(goblin.getPosition()) && goblin.getHp() == this.hp;
    } else if (object instanceof Elf) {
      Elf elf = (Elf) object;
      return this.position.equals(elf.getPosition()) && elf.getHp() == this.hp;
    }

    return false;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  public boolean isGoblin() {
    return this.getClass().equals(Goblin.class);
  }

  public boolean isElf() {
    return this.getClass().equals(Elf.class);
  }

  protected abstract int getAttack();

  public Point getPosition() {
    return position;
  }

  public Point getTarget() {
    return target;
  }

  public int getHp() {
    return hp;
  }

  public boolean isAlive() {
    return alive;
  }

  public void setPosition(Point position) {
    this.position = position;
  }

  public void setTarget(Point target) {
    this.target = target;
  }

  public void setHp(int hp) {
    this.hp = hp;
  }

  public void setAlive(boolean alive) {
    this.alive = alive;
  }

}


class Goblin extends Unit {

  private static int attack = 3;

  public Goblin(Point position) {
    super(position);
  }

  public TreeSet<Unit> getEnemies() {
    return Main.units.stream().filter(Unit::isAlive).filter(Unit::isElf)
        .collect(Collectors.toCollection(() -> new TreeSet<>(Unit::compareTo)));
  }

  @Override
  public String toString() {
    return "G" + super.toString();
  }

  protected int getAttack() {
    return Goblin.attack;
  }

}


class Elf extends Unit {

  private static int attack = 3;

  public Elf(Point position) {
    super(position);
  }

  public TreeSet<Unit> getEnemies() {
    return Main.units.stream().filter(Unit::isAlive).filter(Unit::isGoblin)
        .collect(Collectors.toCollection(() -> new TreeSet<>(Unit::compareTo)));
  }

  @Override
  public String toString() {
    return "E" + super.toString();
  }

  protected int getAttack() {
    return Elf.attack;
  }

  public static void setAttack(int attack) {
    Elf.attack = attack;
  }

}
