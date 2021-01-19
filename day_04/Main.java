package day_04;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

  public static void main(String[] args) {
    Set<String> sortedLines = new TreeSet<>();

    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      bufferedReader.lines().forEach(sortedLines::add);
    } catch (IOException e) {
      e.printStackTrace();
    }

    Map<Integer, Guard> guards = new HashMap<>();

    int id = 0;

    for (String line : sortedLines) {
      if (line.contains("#")) {
        id = Main.setGuardsIds(guards, line);
        continue;
      }

      setGuardsSleepingTime(guards, id, line);
    }

    int max = 0;
    Guard sleepyGuard = null;

    for (Guard guard : guards.values()) {
      int minutes = guard.getMinutesAsleep();

      if (minutes > max) {
        max = minutes;
        sleepyGuard = guard;
      }
    }

    int[] result = new int[2];

    if (sleepyGuard != null) {
      result = sleepyGuard.getTimeMostAsleep();

      System.out.print("Guard sleeping most of the time (1): ");
      System.out.println((sleepyGuard.getId() * result[0]));
    }

    max = 0;
    result = new int[] {0, 0};

    for (Guard guard : guards.values()) {
      result = guard.getTimeMostAsleep();

      if (result[1] > max) {
        max = result[1];
        sleepyGuard = guard;
      }
    }

    if (sleepyGuard != null) {
      result = sleepyGuard.getTimeMostAsleep();

      System.out.print("Guard sleeping most at a specific minute (2): ");
      System.out.println((sleepyGuard.getId() * result[0]));
    }
  }

  private static int setGuardsIds(Map<Integer, Guard> guards, String line) {
    Pattern patternId = Pattern.compile("#(\\d+)");
    Matcher matcher = patternId.matcher(line);

    int id = 0;

    while (matcher.find()) {
      id = Integer.parseInt(matcher.group(1));
      guards.computeIfAbsent(id, Guard::new);
    }

    return id;
  }

  private static void setGuardsSleepingTime(Map<Integer, Guard> guards, int id, String line) {
    Pattern patternDate = Pattern.compile("\\[(\\d+)-(\\d+)-(\\d+) (\\d+):(\\d+)\\]");
    Matcher matcher = patternDate.matcher(line);

    while (matcher.find()) {
      int year = Integer.parseInt(matcher.group(1));
      int month = Integer.parseInt(matcher.group(2));
      int day = Integer.parseInt(matcher.group(3));
      int hour = Integer.parseInt(matcher.group(4));
      int minutes = Integer.parseInt(matcher.group(5));

      LocalDateTime date = LocalDateTime.of(year, month, day, hour, minutes);
      Guard guard = guards.get(id);

      if (line.contains("falls asleep")) {
        guard.addFallAsleeps(date);
      } else if (line.contains("wakes up")) {
        guard.addWakeUps(date);
      }

      guards.put(id, guard);
    }
  }

}


class Guard {

  private int id;
  private Map<String, List<LocalDateTime>> wakeUps = new HashMap<>();
  private Map<String, List<LocalDateTime>> fallAsleeps = new HashMap<>();

  public Guard(int id) {
    this.setId(id);
  }

  public int[] getTimeMostAsleep() {
    List<String> times = this.generateSleepTime();
    int[] result = new int[] {0, 0};

    for (int m = 0; m < 60; m++) {
      int count = 0;

      for (String time : times) {
        if (time.charAt(m) == '#') {
          count++;
        }
      }

      if (count > result[1]) {
        result = new int[] {m, count};
      }
    }

    return result;
  }

  public int getMinutesAsleep() {
    List<String> times = this.generateSleepTime();

    return times.stream().map(t -> (int) t.chars().filter(c -> c == '#').count()).reduce(0,
        Integer::sum);
  }

  private List<String> generateSleepTime() {
    List<String> times = new ArrayList<>();

    for (String monthDay : wakeUps.keySet()) {
      var time = new StringBuilder();

      List<LocalDateTime> wus = wakeUps.get(monthDay);
      List<LocalDateTime> fas = fallAsleeps.get(monthDay);

      int w = 0;
      int f = 0;
      String state = ".";

      for (int i = 0; i < 60; i++) {
        if (w < wus.size() && wus.get(w).getMinute() == i) {
          state = ".";
          w++;
        }

        if (f < fas.size() && fas.get(f).getMinute() == i) {
          state = "#";
          f++;
        }

        time.append(state);
      }

      times.add(time.toString());
    }

    return times;
  }

  public int getId() {
    return id;
  }

  public Map<String, List<LocalDateTime>> getWakeUps() {
    return wakeUps;
  }

  public Map<String, List<LocalDateTime>> getFallAsleeps() {
    return fallAsleeps;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void addWakeUps(LocalDateTime date) {
    List<LocalDateTime> dates = null;
    String monthDay = date.getMonthValue() + "-" + date.getDayOfMonth();

    if (wakeUps.get(monthDay) == null) {
      dates = new ArrayList<>();
    } else {
      dates = wakeUps.get(monthDay);
    }

    dates.add(date);
    wakeUps.put(monthDay, dates);
  }

  public void addFallAsleeps(LocalDateTime date) {
    List<LocalDateTime> dates = null;
    String monthDay = date.getMonthValue() + "-" + date.getDayOfMonth();

    if (fallAsleeps.get(monthDay) == null) {
      dates = new ArrayList<>();
    } else {
      dates = fallAsleeps.get(monthDay);
    }

    dates.add(date);
    fallAsleeps.put(monthDay, dates);
  }

}
