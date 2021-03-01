package day_18;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

  public static void main(String[] args) {
    List<String> map = new ArrayList<>();
    Map<String, Integer> situations = new HashMap<>();

    Main.initMap(map);

    int i = 0;

    for (; i < 10; i++) {
      map = Main.change(map);
      situations.put(map.toString(), i);
    }

    System.out.println("Resource value of the lumber (1): " + lumberValue(map));

    int minutes = 1000000000;

    for (; i < minutes; i++) {
      map = Main.change(map);
      String mapString = map.toString();

      if (!situations.containsKey(mapString)) {
        situations.put(mapString, i);
      } else {
        int start = situations.get(mapString);
        i = (minutes - start) / (i - start) * (i - start) + start;
      }
    }

    System.out.println("Resource value of the lumber (2): " + lumberValue(map));
  }

  private static int lumberValue(List<String> map) {
    int wooded = map.stream().mapToInt(r -> Main.count('|', r)).sum();
    int lumberyard = map.stream().mapToInt(r -> Main.count('#', r)).sum();

    return wooded * lumberyard;
  }

  private static int count(char c, String s) {
    return (int) s.chars().filter(a -> a == c).count();
  }

  private static List<String> change(List<String> map) {
    List<String> newMap = new ArrayList<>(map);

    for (int j = 1; j < map.size() - 1; j++) {
      for (int i = 1; i < map.get(j).length() - 1; i++) {
        Main.changeAcre(i, j, map, newMap);
      }
    }

    return newMap;
  }

  private static void changeAcre(int i, int j, List<String> map, List<String> newMap) {
    String row = map.get(j);
    String surrounding = map.get(j - 1).substring(i - 1, i + 2) + row.substring(i - 1, i + 2)
        + map.get(j + 1).substring(i - 1, i + 2);

    int treesCount = Main.count('|', surrounding);
    int lumberyardsCount = Main.count('#', surrounding);

    if (row.charAt(i) == '.' && treesCount >= 3) {
      newMap.set(j, newMap.get(j).substring(0, i) + "|" + newMap.get(j).substring(i + 1));
    }

    if (row.charAt(i) == '|' && lumberyardsCount >= 3) {
      newMap.set(j, newMap.get(j).substring(0, i) + "#" + newMap.get(j).substring(i + 1));
    }

    if (row.charAt(i) == '#' && (lumberyardsCount < 2 || treesCount < 1)) {
      newMap.set(j, newMap.get(j).substring(0, i) + "." + newMap.get(j).substring(i + 1));
    }
  }

  private static void initMap(List<String> map) {
    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      bufferedReader.lines().forEach(line -> map.add(' ' + line + ' '));
      map.add(" ".repeat(map.get(0).length()));
      map.add(0, " ".repeat(map.get(0).length()));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
