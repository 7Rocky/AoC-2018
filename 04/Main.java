package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

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
    BufferedReader bufferedReader = null;

    try {
      bufferedReader = new BufferedReader(new FileReader("./input.txt"));
    } catch (FileNotFoundException e) { }

    Pattern patternId = Pattern.compile("#(\\d+)");
    Pattern patternDate = Pattern.compile("\\[(\\d+)-(\\d+)-(\\d+)\\s(\\d+):(\\d+)\\]");

    Set<String> sortedLines = new TreeSet<>();

    bufferedReader.lines().forEach(sortedLines::add);

    Map<Integer, Guard> guards = new HashMap<>();

    int id = 0;

    for (String line : sortedLines) {
      if (line.contains("#")) {
        Matcher matcher = patternId.matcher(line);

        while (matcher.find()) {
          id = Integer.parseInt(matcher.group(1));

          if (guards.get(id) == null) guards.put(id, new Guard(id));
        }

        continue;
      }

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

    int max = 0;
    Guard sleepyGuard = null;

    for (Guard guard : guards.values()) {
      int minutes = guard.getMinutesAsleep();

      if(minutes > max) {
        max = minutes;
        sleepyGuard = guard;
      }
    }

    int[] result = sleepyGuard.getTimeMostAsleep();

    System.out.print("Guard sleeping most of the time (1): ");
    System.out.println((sleepyGuard.getId() * result[0]));

    max = 0;
    result = new int[]{ 0, 0 };

    for (Guard guard : guards.values()) {
      result = guard.getTimeMostAsleep();

      if(result[1] > max) {
        max = result[1];
        sleepyGuard = guard;
      }
    }

    result = sleepyGuard.getTimeMostAsleep();

    System.out.print("Guard sleeping most at a specific minute (2): ");
    System.out.println((sleepyGuard.getId() * result[0]));
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
    int[] result = new int[]{ 0, 0 };

    for (int m = 0; m < 60; m++) {
      int count = 0;

      for (String time : times) {
        if (time.charAt(m) == '#') count++;
      }

      if (count > result[1]) result = new int[]{ m, count };
    }

    return result;
  }

  public int getMinutesAsleep() {
    List<String> times = this.generateSleepTime();
    int minutes = 0;

    for (String time : times) {
      minutes += (int) time.chars().filter(c -> c == '#').count();
    }

    return minutes;
  }

  private List<String> generateSleepTime() {
    List<String> times = new ArrayList<>();

    for (String monthDay : wakeUps.keySet()) {
      StringBuilder time = new StringBuilder();

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
    String monthDay = new StringBuilder()
      .append(date.getMonthValue())
      .append("-")
      .append(date.getDayOfMonth())
      .toString();

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
    String monthDay = new StringBuilder()
      .append(date.getMonthValue())
      .append("-")
      .append(date.getDayOfMonth())
      .toString();

    if (fallAsleeps.get(monthDay) == null) {
      dates = new ArrayList<>();
    } else {
      dates = fallAsleeps.get(monthDay);
    }

    dates.add(date);
    fallAsleeps.put(monthDay, dates);
  }

}
