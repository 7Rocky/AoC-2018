package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    BufferedReader bufferedReader = null;

    try {
      bufferedReader = new BufferedReader(new FileReader("input.txt"));
    } catch (FileNotFoundException e) { }

    List<Integer> frequencyChanges = new ArrayList<>();

    bufferedReader.lines().map(Integer::parseInt).forEach(frequencyChanges::add);

    int result = frequencyChanges.stream().reduce(0, Integer::sum);

    System.out.println("Resulting frequency (1): " + result);

    List<Integer> resultingFrequencies = new ArrayList<>();

    result = 0;
    int i = 0;
    int numChanges = frequencyChanges.size();

    while (resultingFrequencies.indexOf(result) == -1) {
      resultingFrequencies.add(result);
      result = resultingFrequencies.get(i) + frequencyChanges.get(i % numChanges);
      i++;
    }

    System.out.println("First frequency reached twice (2): " + result);
  }

}
