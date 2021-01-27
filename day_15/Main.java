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

    while (!Main.units.stream().allMatch(Unit::isGoblin)
        && !Main.units.stream().allMatch(Unit::isElf)) {

      boolean isLast = Main.lifecycle();
      Main.reSortUnits();

      if (!isLast && (Main.units.stream().allMatch(Unit::isGoblin)
          || Main.units.stream().allMatch(Unit::isElf))) {

        break;
      }

      rounds++;
    }

    if (level == 2
        && (!Main.units.stream().allMatch(Unit::isElf) || Main.units.size() != numElves)) {

      return -1;
    }

    return rounds * Main.units.stream().mapToInt(Unit::getHp).sum();

  }

  private static void setConfiguration(String line) {
    int y = Main.map.size();
    int goblinX = line.indexOf('G');
    int elfX = line.indexOf('E');

    while (goblinX != -1) {
      Main.units.add(new Goblin(goblinX, y));
      line = line.substring(0, goblinX) + "." + line.substring(goblinX + 1);
      goblinX = line.indexOf('G');
    }

    while (elfX != -1) {
      Main.units.add(new Elf(elfX, y));
      line = line.substring(0, elfX) + "." + line.substring(elfX + 1);
      elfX = line.indexOf('E');
    }

    Main.map.add(line);
  }

  private static boolean lifecycle() {
    boolean isLast = false;

    for (Unit unit : Main.units) {
      if (unit.isAlive()) {
        unit.setTarget();
        unit.move();
      }

      isLast = unit.equals(Main.units.last());

      if (Main.units.stream().filter(Unit::isAlive).allMatch(Unit::isGoblin)
          || Main.units.stream().filter(Unit::isAlive).allMatch(Unit::isElf)) {

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
    int[] best = new int[] {0, 0, Integer.MAX_VALUE};
    boolean found = false;

    for (Unit enemy : enemies) {
      int[] optimal = Main.breadthFirstSearch(unit, enemy);

      if (optimal[0] != -1 && optimal[1] != -1 && optimal[2] != -1) {
        found = true;

        if (best[2] > optimal[2]) {
          best = optimal;
        }

        if (unit.getX() == optimal[0] && unit.getY() == optimal[1] && optimal[2] == 0) {
          best = optimal;
          break;
        }
      }
    }

    unit.setTarget(found ? new int[] {best[0], best[1]} : null);
  }

  public static List<Integer[]> getAdjacentPoints(int x, int y) {
    return Arrays.asList(new Integer[] {x, y - 1, 0}, new Integer[] {x - 1, y, 0},
        new Integer[] {x + 1, y, 0}, new Integer[] {x, y + 1, 0});
  }

  private static int[] breadthFirstSearch(Unit orig, Unit dest) {
    Integer[] point = new Integer[] {orig.getX(), orig.getY(), 0};

    int[] optimal = new int[] {-1, -1, -1};
    int minSteps = Integer.MAX_VALUE;

    for (Integer[] e : Main.getAdjacentPoints(dest.getX(), dest.getY())) {
      if (Main.map.get(e[1]).charAt(e[0]) == '.' && Main.units.stream().filter(Unit::isAlive)
          .noneMatch(u -> !u.equals(orig) && u.getX() == e[0] && u.getY() == e[1])) {

        int steps = Main.breadthFirstSearch(point, e);

        if (minSteps > steps) {
          minSteps = steps;
          optimal = new int[] {e[0], e[1], minSteps};
        }
      }
    }

    return optimal;
  }

  public static int breadthFirstSearch(Integer[] orig, Integer[] dest) {
    Set<String> visited = new HashSet<>();
    LinkedList<Integer[]> queue = new LinkedList<>();

    visited.add(orig[0] + "," + orig[1]);
    queue.add(orig);

    Integer[] node = null;

    while (!queue.isEmpty()) {
      node = queue.poll();

      if (node[0].equals(dest[0]) && node[1].equals(dest[1])) {
        return node[2];
      }

      for (Integer[] next : Main.getPossiblePositions(node)) {
        String nextString = next[0] + "," + next[1];

        if (!visited.contains(nextString)) {
          next[2] = node[2] + 1;
          visited.add(nextString);
          queue.add(next);
        }
      }
    }

    return Integer.MAX_VALUE;
  }

  private static List<Integer[]> getPossiblePositions(Integer[] pos) {
    List<Integer[]> positions = new ArrayList<>();

    for (Integer[] e : Main.getAdjacentPoints(pos[0], pos[1])) {
      if (e[0] + e[1] > 1 && e[0] < Main.map.get(e[1]).length() && e[1] < Main.map.size()
          && Main.map.get(e[1]).charAt(e[0]) == '.' && Main.units.stream().filter(Unit::isAlive)
              .noneMatch(u -> u.getX() == e[0] && u.getY() == e[1])) {

        positions.add(e);
      }
    }

    return positions;
  }

  public static Unit getSurroundingEnemy(Unit unit, SortedSet<Unit> enemies) {
    TreeSet<Unit> closest = enemies.stream().filter(Unit::isAlive).filter(
        enemy -> Math.abs(unit.getX() - enemy.getX()) + Math.abs(unit.getY() - enemy.getY()) < 2)
        .collect(Collectors.toCollection(() -> new TreeSet<>(Unit::compareTo)));

    Unit enemy = null;
    int hp = Integer.MAX_VALUE;

    for (Unit c : closest) {
      if (hp > c.getHp()) {
        hp = c.getHp();
        enemy = c;
      }
    }

    return enemy;
  }

}


abstract class Unit implements Comparable<Unit> {

  protected int x;
  protected int y;
  protected int hp = 200;
  protected int[] target = null;
  protected boolean alive = true;

  protected Unit(int x, int y) {
    this.setX(x);
    this.setY(y);
  }

  protected abstract TreeSet<Unit> getEnemies();

  public void move() {
    if (this.target == null) {
      return;
    }

    if (this.x == this.target[0] && this.y == this.target[1]) {
      this.attack();
      return;
    }

    Integer[] t = new Integer[] {this.target[0], this.target[1]};

    int minSteps = Integer.MAX_VALUE;

    for (Integer[] e : Main.getAdjacentPoints(this.x, this.y)) {
      if (Main.map.get(e[1]).charAt(e[0]) == '.') {
        int steps = Main.breadthFirstSearch(e, t);

        if (minSteps > steps && Main.units.stream().filter(Unit::isAlive)
            .noneMatch(u -> u.getX() == e[0] && u.getY() == e[1])) {

          minSteps = steps;
          this.setX(e[0]);
          this.setY(e[1]);
        }
      }
    }

    if (this.x == this.target[0] && this.y == this.target[1]) {
      this.attack();
    }
  }

  public void attack() {
    Unit enemy = Main.getSurroundingEnemy(this, this.getEnemies());

    if (enemy == null) {
      this.setTarget();
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

  public void setTarget() {
    Main.getFirstUnit(this, this.getEnemies());
  }

  @Override
  public int compareTo(Unit unit) {
    if (this.y == unit.getY()) {
      return this.x - unit.getX();
    }

    return this.y - unit.getY();
  }

  @Override
  public abstract boolean equals(Object object);

  @Override
  public abstract int hashCode();

  public boolean isGoblin() {
    return this.getClass().equals(Goblin.class);
  }

  public boolean isElf() {
    return this.getClass().equals(Elf.class);
  }

  protected abstract int getAttack();

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getHp() {
    return hp;
  }

  public int[] getTarget() {
    return target;
  }

  public boolean isAlive() {
    return alive;
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public void setHp(int hp) {
    this.hp = hp;
  }

  public void setTarget(int[] target) {
    this.target = target;
  }

  public void setAlive(boolean alive) {
    this.alive = alive;
  }

}


class Goblin extends Unit {

  private static int attack = 3;

  public Goblin(int x, int y) {
    super(x, y);
  }

  public TreeSet<Unit> getEnemies() {
    return Main.units.stream().filter(Unit::isAlive).filter(Unit::isElf)
        .collect(Collectors.toCollection(() -> new TreeSet<>(Unit::compareTo)));
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Goblin) {
      Goblin goblin = (Goblin) object;

      return goblin.getX() == this.x && goblin.getY() == this.y && goblin.getHp() == this.hp;
    }

    return false;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  protected int getAttack() {
    return Goblin.attack;
  }

}


class Elf extends Unit {

  private static int attack = 3;

  public Elf(int x, int y) {
    super(x, y);
  }

  public TreeSet<Unit> getEnemies() {
    return Main.units.stream().filter(Unit::isAlive).filter(Unit::isGoblin)
        .collect(Collectors.toCollection(() -> new TreeSet<>(Unit::compareTo)));
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Elf) {
      Elf elf = (Elf) object;

      return elf.getX() == this.x && elf.getY() == this.y && elf.getHp() == this.hp;
    }

    return false;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  protected int getAttack() {
    return Elf.attack;
  }

  public static void setAttack(int attack) {
    Elf.attack = attack;
  }

}
