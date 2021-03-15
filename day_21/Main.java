package day_21;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

  static List<String> testProgram = new ArrayList<>();

  public static void main(String[] args) {
    Main.readInput();

    int number = Integer.parseInt(Main.testProgram.get(7).split(" ")[1]);

    int minReg = Main.runActivationSystem(number, 1);
    int maxReg = Main.runActivationSystem(number, 2);

    System.out.println("Minimum register 0 to halt (1): " + minReg);
    System.out.println("Maximum register 0 to halt (2): " + maxReg);
  }

  private static int runActivationSystem(int number, int part) {
    Set<Integer> seen = new HashSet<>();
    int c = 0;
    int last = -1;

    while (true) {
      int a = c | 65536;
      c = number;

      while (true) {
        c = (((c + (a & 255)) & 16777215) * 65899) & 16777215;

        if (256 > a) {
          if (part == 2) {
            if (!seen.contains(c)) {
              seen.add(c);
              last = c;

              break;
            }

            return last;
          }

          return c;
        }

        a /= 256;
      }
    }
  }

  private static void readInput() {
    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      String line = bufferedReader.readLine();
      line = bufferedReader.readLine();

      while (line != null) {
        Main.testProgram.add(line);
        line = bufferedReader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
