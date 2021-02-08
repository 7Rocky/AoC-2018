package day_16;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.stream.Collectors;

public class Main {

  static List<List<Integer>> beforeStates = new ArrayList<>();
  static List<List<Integer>> opcodes = new ArrayList<>();
  static List<List<Integer>> afterStates = new ArrayList<>();
  static List<List<Integer>> testProgram = new ArrayList<>();
  static List<Integer> behaviours = new ArrayList<>();

  static Map<Integer, Set<String>> mapOpcodes = new HashMap<>();
  static Set<String> knownOpcodes =
      new HashSet<>(Arrays.asList("addr", "addi", "mulr", "muli", "banr", "bani", "borr", "bori",
          "setr", "seti", "gtir", "gtri", "gtrr", "eqir", "eqri", "eqrr"));

  public static void main(String[] args) {
    Main.readInput();

    for (int opcode = 0; opcode < 16; opcode++) {
      Main.mapOpcodes.put(opcode, new HashSet<>(Main.knownOpcodes));
    }

    for (int i = 0; i < Main.beforeStates.size(); i++) {
      Main.behaviours.add(
          Main.matches(Main.beforeStates.get(i), Main.opcodes.get(i), Main.afterStates.get(i)));
    }

    System.out.print("Number of samples that behave like three or more opcodes (1): ");
    System.out.println(Main.behaviours.stream().filter(b -> b >= 3).count());

    while (Main.mapOpcodes.values().stream().anyMatch(id -> id.size() > 1)) {
      for (Set<String> matchedOpcodes : Main.mapOpcodes.values()) {
        if (matchedOpcodes.size() == 1) {
          Main.removeOpcodes(Main.getSingleElement(matchedOpcodes));
        }
      }
    }

    List<Integer> regs = Arrays.asList(0, 0, 0, 0);

    for (List<Integer> opcode : testProgram) {
      regs = Opcode.exec(Main.getSingleElement(Main.mapOpcodes.get(opcode.get(0))), regs, opcode);
    }

    System.out.println("Value of register 0 after executing the program (2): " + regs.get(0));
  }

  private static String getSingleElement(Set<String> set) {
    return set.toArray()[0].toString();
  }

  private static List<Integer> splitCSV(String line, String separator) {
    return Arrays.asList(line.split(separator)).stream().map(Integer::parseInt)
        .collect(Collectors.toList());
  }

  private static void readInput() {
    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      String line = bufferedReader.readLine();

      while (line.length() > 0) {
        line = line.substring(line.indexOf("[") + 1, line.length() - 1);
        Main.beforeStates.add(Main.splitCSV(line, ", "));

        Main.opcodes.add(Main.splitCSV(bufferedReader.readLine(), " "));

        line = bufferedReader.readLine();
        line = line.substring(line.indexOf("[") + 1, line.length() - 1);
        Main.afterStates.add(Main.splitCSV(line, ", "));

        bufferedReader.skip(1);

        line = bufferedReader.readLine();
      }

      while (line.length() == 0) {
        line = bufferedReader.readLine();
      }

      while (line != null) {
        Main.testProgram.add(Main.splitCSV(line, " "));
        line = bufferedReader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static int matches(List<Integer> before, List<Integer> opcode, List<Integer> after) {
    Set<String> matches = new HashSet<>();

    for (String id : Main.knownOpcodes) {
      if (after.equals(Opcode.exec(id, before, opcode))) {
        matches.add(id);
      }
    }

    Set<String> knownMatches = Main.mapOpcodes.get(opcode.get(0));
    knownMatches.retainAll(matches);

    return matches.size();
  }

  private static void removeOpcodes(String opcode) {
    for (Set<String> matchedOpcodes : Main.mapOpcodes.values()) {
      if (matchedOpcodes.contains(opcode) && matchedOpcodes.size() > 1) {
        matchedOpcodes.remove(opcode);
      }
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
