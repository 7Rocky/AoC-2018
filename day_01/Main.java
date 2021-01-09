package day_01;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    List<Integer> frequencyChanges = new ArrayList<>();

    try (BufferedReader bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      bufferedReader.lines().map(Integer::parseInt).forEach(frequencyChanges::add);
    } catch (IOException e) {
      e.printStackTrace();
    }

    int result = frequencyChanges.stream().reduce(0, Integer::sum);

    System.out.print("Resulting frequency (1): ");
    System.out.println(result);

    List<Integer> resultingFrequencies = new ArrayList<>();

    result = 0;
    int i = 0;
    int numChanges = frequencyChanges.size();

    while (resultingFrequencies.indexOf(result) == -1) {
      resultingFrequencies.add(result);
      result = resultingFrequencies.get(i) + frequencyChanges.get(i % numChanges);
      i++;
    }

    System.out.print("First frequency reached twice (2): ");
    System.out.println(result);
  }

}
