package main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;

import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    BufferedReader br = null;

    try {
      br = new BufferedReader(new FileReader("./input.txt"));
    } catch (FileNotFoundException e) { }

    List<Integer> frequencyChanges = new ArrayList<Integer>();

    br.lines().forEach(line -> frequencyChanges.add(Integer.parseInt(line)));

    int result = frequencyChanges.stream().reduce(0, Integer::sum);

    System.out.println("Resulting frequency (1): " + result);

    List<Integer> resultingFrequencies = new ArrayList<Integer>();

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
