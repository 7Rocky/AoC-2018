package day_11;

import java.util.ArrayList;
import java.util.List;

public class Main {

  public static final int DIM = 300;

  static int gridSerialNumber = 2866;
  static List<List<Integer>> powers = new ArrayList<>();

  public static void main(String[] args) {
    Main.setPowers();

    System.out.print("Top-right corner of 3x3 grid with max power (1): ");
    System.out.println(getMaxPowerPosition(3));

    System.out.print("Top-right corner identifier of grid with max power (2): ");
    System.out.println(getMaxPowerPosition());
  }

  private static int getPower(int x, int y) {
    return ((((x + 10) * y + Main.gridSerialNumber) * (x + 10)) / 100) % 10 - 5;
  }

  private static void setPowers() {
    for (int y = 1; y <= Main.DIM; y++) {
      List<Integer> row = new ArrayList<>();

      for (int x = 1; x <= Main.DIM; x++) {
        row.add(getPower(x, y));
      }

      powers.add(row);
    }
  }

  private static int getGridPower(int i, int j, int d) {
    int power = 0;

    for (int x = 0; x < d; x++) {
      for (int y = 0; y < d; y++) {
        power += Main.powers.get(j + y - 1).get(i + x - 1);
      }
    }

    return power;
  }

  private static String getMaxPowerPosition(int gridSize) {
    int maxPower = 0;
    int[] grid = new int[2];

    for (int i = 1; i <= Main.DIM - 2; i++) {
      for (int j = 1; j <= Main.DIM - 2; j++) {
        int power = getGridPower(i, j, gridSize);

        if (power > maxPower) {
          maxPower = power;
          grid = new int[] {i, j};
        }
      }
    }

    return getPositionString(grid[0], grid[1]);
  }

  private static int getGridPower(int i, int j, int d, int lastPowerGrid) {
    int power = lastPowerGrid;

    for (int x = 0; x < d; x++) {
      power += Main.powers.get(j + d - 2).get(i + x - 1);
    }

    for (int y = 0; y < d - 1; y++) {
      power += Main.powers.get(j + y - 1).get(i + d - 2);
    }

    return power;
  }

  private static String getMaxPowerPosition() {
    int[] grid = new int[3];
    int maxPower = 0;

    for (int i = 1; i <= Main.DIM; i++) {
      for (int j = 1; j <= Main.DIM; j++) {
        int power = 0;

        for (int d = 1; d <= getMaxGridDimension(i, j); d++) {
          power = getGridPower(i, j, d, power);

          if (power > maxPower) {
            maxPower = power;
            grid = new int[] {i, j, d};
          }
        }
      }
    }

    return getPositionString(grid[0], grid[1], grid[2]);
  }

  private static int getMaxGridDimension(int i, int j) {
    return i > j ? Main.DIM - i : Main.DIM - j;
  }

  private static String getPositionString(int x, int y) {
    return x + "," + y;
  }

  private static String getPositionString(int x, int y, int d) {
    return Main.getPositionString(x, y) + "," + d;
  }

}
