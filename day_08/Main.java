package day_08;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

public class Main {

  static List<Integer> numbers = null;

  public static void main(String[] args) {
    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      numbers = Arrays.stream(bufferedReader.readLine().split(" ")).map(Integer::parseInt)
          .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }

    Node root = new Node();
    readHeaders(root, 0);

    System.out.println("Sum of metadata entries (1): " + root.sumMetadataEntries());
    System.out.println("Value of root node (2): " + root.getValue());
  }

  private static int readHeaders(Node node, int index) {
    int numChildNodes = numbers.get(index);
    int numMetadataEntries = numbers.get(index + 1);

    index += 2;

    for (int i = 0; i < numChildNodes; i++) {
      Node childNode = new Node();
      index = readHeaders(childNode, index);
      node.addChildNode(childNode);
    }

    for (int i = index; i < index + numMetadataEntries; i++) {
      node.addMetadataEntry(numbers.get(i));
    }

    return index + numMetadataEntries;
  }

}


class Node {

  private List<Node> childNodes = new ArrayList<>();
  private List<Integer> metadataEntries = new ArrayList<>();

  public int sumMetadataEntries() {
    int sum = 0;

    if (!childNodes.isEmpty()) {
      sum += childNodes.stream().map(Node::sumMetadataEntries).reduce(0, Integer::sum);
    }

    return sum + metadataEntries.stream().reduce(0, Integer::sum);
  }

  public int getValue() {
    if (childNodes.isEmpty()) {
      return metadataEntries.stream().reduce(0, Integer::sum);
    }

    return metadataEntries.stream().filter(m -> m - 1 < childNodes.size())
        .map(m -> childNodes.get(m - 1).getValue()).reduce(0, Integer::sum);
  }

  public List<Node> getChildNodes() {
    return childNodes;
  }

  public List<Integer> getMetadataEntries() {
    return metadataEntries;
  }

  public void addChildNode(Node childNode) {
    this.childNodes.add(childNode);
  }

  public void addMetadataEntry(int metadataEntry) {
    this.metadataEntries.add(metadataEntry);
  }

}
