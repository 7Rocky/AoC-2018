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
    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      bufferedReader.lines().forEach(Main.map::add);
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

    System.out.println("Position of first cart crash (1): " + firstCrashPosition);
    System.out.println("Position of last cart (2): " + carts.first());
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
    for (int j = 0; j < Main.map.size(); j++) {
      String s = Main.map.get(j);

      for (int i = 0; i < s.length(); i++) {
        char c = '-';
        Direction direction = Direction.LEFT;
        boolean isCart = true;

        switch (s.charAt(i)) {
          case '<':
            break;
          case '>':
            direction = Direction.RIGHT;
            break;
          case 'v':
            c = '|';
            direction = Direction.DOWN;
            break;
          case '^':
            c = '|';
            direction = Direction.UP;
            break;
          default:
            isCart = false;
        }

        if (isCart) {
          Main.carts.add(new Cart(i, j, direction));
          Main.map.set(j, s.substring(0, i) + c + s.substring(i + 1));
        }
      }
    }
  }

}


enum Direction {
  RIGHT, LEFT, UP, DOWN
}


class Cart implements Comparable<Cart> {

  private int x;
  private int y;
  private Direction direction;
  private String id;
  private boolean crashed = false;
  private Direction nextIntersection = Direction.LEFT;

  public Cart(int x, int y, Direction direction) {
    this.setX(x);
    this.setY(y);
    this.setDirection(direction);
    this.setId();
  }

  @Override
  public String toString() {
    return x + "," + y;
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
    return this.toString().hashCode();
  }

  @Override
  public int compareTo(Cart cart) {
    return y == cart.getY() ? x - cart.getX() : y - cart.getY();
  }

  private void abstractMove(int dx, int dy, Direction dirA, Direction dirB) {
    char c = Main.map.get(y + dy).charAt(x + dx);

    x += dx;
    y += dy;

    switch (c) {
      case '/':
        direction = dirA;
        break;
      case '\\':
        direction = dirB;
        break;
      case '+':
        direction = this.getNextDirection();
        this.setNextIntersection();
        break;
      default:
    }
  }

  public void move() {
    switch (direction) {
      case RIGHT -> this.abstractMove(+1, 0, Direction.UP, Direction.DOWN);
      case LEFT -> this.abstractMove(-1, 0, Direction.DOWN, Direction.UP);
      case UP -> this.abstractMove(0, -1, Direction.RIGHT, Direction.LEFT);
      case DOWN -> this.abstractMove(0, +1, Direction.LEFT, Direction.RIGHT);
    }
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public Direction getDirection() {
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

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  public void setId() {
    id = this.toString();
  }

  public void setCrashed(boolean crashed) {
    this.crashed = crashed;
  }

  public Direction getNextDirection() {
    if (nextIntersection == Direction.UP) {
      return direction;
    }

    if (direction == Direction.DOWN) {
      return nextIntersection == Direction.LEFT ? Direction.RIGHT : Direction.LEFT;
    }

    if (direction == Direction.RIGHT) {
      return nextIntersection == Direction.LEFT ? Direction.UP : Direction.DOWN;
    }

    if (direction == Direction.LEFT) {
      return nextIntersection == Direction.LEFT ? Direction.DOWN : Direction.UP;
    }

    return nextIntersection;
  }

  public void setNextIntersection() {
    nextIntersection = nextIntersection == Direction.LEFT ? Direction.UP
        : nextIntersection == Direction.RIGHT ? Direction.LEFT : Direction.RIGHT;
  }

}
