package day_11;

public class Main {

  static int gridSerialNumber = 2866;

  public static void main(String[] args) {
    int[] grid = getMaxPowerPosition();

    System.out.print("Top-right corner of 3x3 grid with max power (1): ");
    System.out.println(getPositionString(grid[0], grid[1]));

    int maxPower = 0;
    int dimension = 0;

    for (int i = 1; i <= 300; i++) {
      for (int j = 1; j <= 300; j++) {
        for (int d = 0; d < getMaxGridDimension(i, j, 300); d++) {
          int power = getGridPower(i, j, d);

          if (power > maxPower) {
            maxPower = power;
            grid[0] = i;
            grid[1] = j;
            dimension = d;
          }
        }
      }
    }

    System.out.print("Top-right corner identifier of grid with max power (2): ");
    System.out.println(getPositionString(grid[0], grid[1], dimension));
  }

  private static int[] getMaxPowerPosition() {
    int maxPower = 0;
    int[] grid = new int[2];

    for (int i = 1; i <= 300 - 2; i++) {
      for (int j = 1; j <= 300 - 2; j++) {
        int power = getGridPower(i, j, 3);

        if (power > maxPower) {
          maxPower = power;
          grid[0] = i;
          grid[1] = j;
        }
      }
    }

    return grid;
  }

  private static int getMaxGridDimension(int i, int j, int total) {
    return i > j ? total - i : total - j;
  }

  private static int getGridPower(int i, int j, int d) {
    int power = 0;

    for (int x = 0; x < d; x++) {
      for (int y = 0; y < d; y++) {
        power += getPower(i + x, j + y);
      }
    }

    return power;
  }

  private static String getPositionString(int x, int y) {
    return new StringBuilder().append(x).append(",").append(y).toString();
  }

  private static String getPositionString(int x, int y, int d) {
    return new StringBuilder().append(x).append(",").append(y).append(",").append(d).toString();
  }

  private static int getPower(int x, int y) {
    return ((((x + 10) * y + Main.gridSerialNumber) * (x + 10)) / 100) % 10 - 5;
  }

}
