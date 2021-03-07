package day_19;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

  static List<String> testProgram = new ArrayList<>();
  static int ipReg;

  public static void main(String[] args) {
    Main.readInput();

    List<Integer> regs = Arrays.asList(0, 0, 0, 0, 0, 0);

    int ip = regs.get(Main.ipReg);

    while (ip < testProgram.size()) {
      String[] split = testProgram.get(ip).split(" ");
      List<Integer> opcode = Arrays.asList(0, Integer.parseInt(split[1]),
          Integer.parseInt(split[2]), Integer.parseInt(split[3]));

      regs = Opcode.exec(split[0], regs, opcode);

      regs.set(Main.ipReg, regs.get(Main.ipReg) + 1);
      ip = regs.get(Main.ipReg);
    }

    System.out.println("Value of register 0 after executing the program (1): " + regs.get(0));

    regs = Arrays.asList(1, 0, 0, 0, 0, 0);

    ip = regs.get(Main.ipReg);

    int target = 0;

    while (ip < testProgram.size()) {
      String[] split = testProgram.get(ip).split(" ");
      List<Integer> opcode = Arrays.asList(0, Integer.parseInt(split[1]),
          Integer.parseInt(split[2]), Integer.parseInt(split[3]));

      regs = Opcode.exec(split[0], regs, opcode);

      regs.set(Main.ipReg, regs.get(Main.ipReg) + 1);
      ip = regs.get(Main.ipReg);

      if ("gtrr".equals(split[0])) {
        target = regs.get(opcode.get(2));
        break;
      }
    }

    int sum = 0;

    for (int i = 1; i <= target; i++) {
      if (target % i == 0) {
        sum += i;
      }
    }

    System.out.println("Value of register 0 after executing the program (2): " + sum);
  }

  private static void readInput() {
    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      String line = bufferedReader.readLine();

      Main.ipReg = Integer.parseInt(line.split(" ")[1]);

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


class Opcode {

  private Opcode() {
  }

  public static List<Integer> exec(String id, List<Integer> before, List<Integer> opcode) {
    switch (id) {
      case "addr":
        return Opcode.addr(before, opcode);
      case "addi":
        return Opcode.addi(before, opcode);
      case "mulr":
        return Opcode.mulr(before, opcode);
      case "muli":
        return Opcode.muli(before, opcode);
      case "banr":
        return Opcode.banr(before, opcode);
      case "bani":
        return Opcode.bani(before, opcode);
      case "borr":
        return Opcode.borr(before, opcode);
      case "bori":
        return Opcode.bori(before, opcode);
      case "setr":
        return Opcode.setr(before, opcode);
      case "seti":
        return Opcode.seti(before, opcode);
      case "gtir":
        return Opcode.gtir(before, opcode);
      case "gtri":
        return Opcode.gtri(before, opcode);
      case "gtrr":
        return Opcode.gtrr(before, opcode);
      case "eqir":
        return Opcode.eqir(before, opcode);
      case "eqri":
        return Opcode.eqri(before, opcode);
      case "eqrr":
        return Opcode.eqrr(before, opcode);
      default:
    }

    return new ArrayList<>();
  }

  private static List<Integer> addr(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), before.get(opcode.get(1)) + before.get(opcode.get(2)));
    return after;
  }

  private static List<Integer> addi(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), before.get(opcode.get(1)) + opcode.get(2));
    return after;
  }

  private static List<Integer> mulr(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), before.get(opcode.get(1)) * before.get(opcode.get(2)));
    return after;
  }

  private static List<Integer> muli(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), before.get(opcode.get(1)) * opcode.get(2));
    return after;
  }

  private static List<Integer> banr(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), before.get(opcode.get(1)) & before.get(opcode.get(2)));
    return after;
  }

  private static List<Integer> bani(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), before.get(opcode.get(1)) & opcode.get(2));
    return after;
  }

  private static List<Integer> borr(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), before.get(opcode.get(1)) | before.get(opcode.get(2)));
    return after;
  }

  private static List<Integer> bori(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), before.get(opcode.get(1)) | opcode.get(2));
    return after;
  }

  private static List<Integer> setr(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), before.get(opcode.get(1)));
    return after;
  }

  private static List<Integer> seti(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), opcode.get(1));
    return after;
  }

  private static List<Integer> gtir(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), opcode.get(1) > before.get(opcode.get(2)) ? 1 : 0);
    return after;
  }

  private static List<Integer> gtri(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), before.get(opcode.get(1)) > opcode.get(2) ? 1 : 0);
    return after;
  }

  private static List<Integer> gtrr(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), before.get(opcode.get(1)) > before.get(opcode.get(2)) ? 1 : 0);
    return after;
  }

  private static List<Integer> eqir(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), opcode.get(1).equals(before.get(opcode.get(2))) ? 1 : 0);
    return after;
  }

  private static List<Integer> eqri(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), before.get(opcode.get(1)).equals(opcode.get(2)) ? 1 : 0);
    return after;
  }

  private static List<Integer> eqrr(List<Integer> before, List<Integer> opcode) {
    List<Integer> after = new ArrayList<>(before);
    after.set(opcode.get(3), before.get(opcode.get(1)).equals(before.get(opcode.get(2))) ? 1 : 0);
    return after;
  }

}
