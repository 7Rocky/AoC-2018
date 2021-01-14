package day_13;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import java.util.function.Predicate;

import java.util.stream.Collectors;

public class Main {

  static List<String> map = new ArrayList<>();
  static TreeSet<Cart> carts = new TreeSet<>();

  public static void main(String[] args) {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      bufferedReader.lines().forEach(map::add);
    } catch (IOException e) {
      e.printStackTrace();
    }

    Main.setCarts();

    String firstCrashPosition = "";

    while (carts.stream().filter(Predicate.not(Cart::isCrashed)).count() > 1) {
      for (Cart cart : carts) {
        if (!cart.isCrashed()) {
          cart.move();

          if (Main.cartsCrash() && firstCrashPosition.isEmpty()) {
            firstCrashPosition = Main.getCrashPosition();
          }
        }
      }

      Main.reSortCarts();
    }

    System.out.print("Position of first cart crash (1): ");
    System.out.println(firstCrashPosition);

    System.out.print("Position of last cart (2): ");
    System.out.println(carts.first());
  }

  private static String getCrashPosition() {
    Set<Cart> crashedCarts = carts.stream().filter(Cart::isCrashed).collect(Collectors.toSet());

    if (!crashedCarts.isEmpty()) {
      Cart crashedCart = crashedCarts.stream().findAny().orElse(null);

      if (crashedCart != null) {
        return crashedCart.toString();
      }
    }

    return "";
  }

  private static boolean cartsCrash() {
    for (Cart c1 : carts) {
      for (Cart c2 : carts) {
        if (c1.equals(c2) && !c1.isCrashed()) {
          c1.setCrashed(true);
          c2.setCrashed(true);
          return true;
        }
      }
    }

    return false;
  }

  private static void reSortCarts() {
    TreeSet<Cart> newCarts = new TreeSet<>();

    carts.stream().filter(Predicate.not(Cart::isCrashed)).forEach(newCarts::add);
    carts = newCarts;
  }

  private static void setCarts() {
    for (int j = 0; j < map.size(); j++) {
      String s = map.get(j);

      for (int i = 0; i < s.length(); i++) {
        if (s.charAt(i) == '<') {
          Main.carts.add(new Cart(i, j, 'L'));
          s = new StringBuilder(s.substring(0, i)).append("-").append(s.substring(i + 1))
              .toString();
        }

        if (s.charAt(i) == '>') {
          Main.carts.add(new Cart(i, j, 'R'));
          s = new StringBuilder(s.substring(0, i)).append("-").append(s.substring(i + 1))
              .toString();
        }

        if (s.charAt(i) == 'v') {
          Main.carts.add(new Cart(i, j, 'D'));
          s = new StringBuilder(s.substring(0, i)).append("|").append(s.substring(i + 1))
              .toString();
        }

        if (s.charAt(i) == '^') {
          Main.carts.add(new Cart(i, j, 'U'));
          s = new StringBuilder(s.substring(0, i)).append("|").append(s.substring(i + 1))
              .toString();
        }

        map.set(j, s);
      }
    }
  }

}


class Cart implements Comparable<Cart> {

  private int x;
  private int y;
  private char direction;
  private String id;
  private boolean crashed = false;
  private char nextIntersection = 'L';

  public Cart(int x, int y, char direction) {
    this.setX(x);
    this.setY(y);
    this.setDirection(direction);
    this.setId();
  }

  @Override
  public String toString() {
    return new StringBuilder().append(x).append(",").append(y).toString();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Cart) {
      Cart c = (Cart) object;

      return c.getX() == x && c.getY() == y && c.isCrashed() == crashed && !c.getId().equals(id);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return new StringBuilder().append(x).append(y).toString().hashCode();
  }

  @Override
  public int compareTo(Cart cart) {
    return y == cart.getY() ? x - cart.getX() : y - cart.getY();
  }

  private void moveRight() {
    char c = Main.map.get(y).charAt(x + 1);

    switch (c) {
      case '-':
        x++;
        break;
      case '/':
        x++;
        direction = 'U';
        break;
      case '\\':
        x++;
        direction = 'D';
        break;
      case '+':
        x++;
        direction = this.getNextDirection();
        this.setNextIntersection();
        break;
      default:
        System.exit(-1);
    }
  }

  private void moveLeft() {
    char c = Main.map.get(y).charAt(x - 1);

    switch (c) {
      case '-':
        x--;
        break;
      case '/':
        x--;
        direction = 'D';
        break;
      case '\\':
        x--;
        direction = 'U';
        break;
      case '+':
        x--;
        direction = this.getNextDirection();
        this.setNextIntersection();
        break;
      default:
        System.exit(-1);
    }
  }

  private void moveUp() {
    char c = Main.map.get(y - 1).charAt(x);

    switch (c) {
      case '|':
        y--;
        break;
      case '/':
        y--;
        direction = 'R';
        break;
      case '\\':
        y--;
        direction = 'L';
        break;
      case '+':
        y--;
        direction = this.getNextDirection();
        this.setNextIntersection();
        break;
      default:
        System.exit(-1);
    }
  }

  private void moveDown() {
    char c = Main.map.get(y + 1).charAt(x);

    switch (c) {
      case '|':
        y++;
        break;
      case '/':
        y++;
        direction = 'L';
        break;
      case '\\':
        y++;
        direction = 'R';
        break;
      case '+':
        y++;
        direction = this.getNextDirection();
        this.setNextIntersection();
        break;
      default:
        System.exit(-1);
    }
  }

  public void move() {
    switch (direction) {
      case 'R':
        this.moveRight();
        break;
      case 'L':
        this.moveLeft();
        break;
      case 'U':
        this.moveUp();
        break;
      case 'D':
        this.moveDown();
        break;
      default:
        System.exit(-1);
    }
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public char getDirection() {
    return direction;
  }

  public String getId() {
    return id;
  }

  public boolean isCrashed() {
    return crashed;
  }

  public void setX(int x) {
    this.x = x;
  }

  public void setY(int y) {
    this.y = y;
  }

  public void setDirection(char direction) {
    this.direction = direction;
  }

  public void setId() {
    id = new StringBuilder().append(x).append(y).toString();
  }

  public void setCrashed(boolean crashed) {
    this.crashed = crashed;
  }

  public char getNextDirection() {
    if (nextIntersection == 'U') {
      return direction;
    }

    if (direction == 'D') {
      return nextIntersection == 'L' ? 'R' : 'L';
    }

    if (direction == 'R') {
      return nextIntersection == 'L' ? 'U' : 'D';
    }

    if (direction == 'L') {
      return nextIntersection == 'L' ? 'D' : 'U';
    }

    return nextIntersection;
  }

  public void setNextIntersection() {
    nextIntersection = nextIntersection == 'L' ? 'U' : nextIntersection == 'R' ? 'L' : 'R';
  }

}
