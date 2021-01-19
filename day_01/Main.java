package day_01;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Main {

  public static void main(String[] args) {
    List<Integer> frequencyChanges = new ArrayList<>();

    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      bufferedReader.lines().map(Integer::parseInt).forEach(frequencyChanges::add);
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.print("Resulting frequency (1): ");
    System.out.println(frequencyChanges.stream().reduce(0, Integer::sum));

    Set<Integer> resultingFrequencies = new TreeSet<>();

    int result = 0;
    int numChanges = frequencyChanges.size();

    for (int i = 0; resultingFrequencies.add(result); i++) {
      result += frequencyChanges.get(i % numChanges);
    }

    System.out.println("First frequency reached twice (2): " + result);
  }

}
