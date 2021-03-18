package day_07;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

  public static void main(String[] args) {
    Map<String, Step> steps1 = new HashMap<>();
    Map<String, Step> steps2 = new HashMap<>();

    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      bufferedReader.lines().forEach(line -> {
        Main.setSteps(steps1, line);
        Main.setSteps(steps2, line);
      });
    } catch (IOException e) {
      e.printStackTrace();
    }

    int totalSteps = steps1.size();
    var doneSteps1 = new StringBuilder();

    while (doneSteps1.length() < totalSteps) {
      Step step = Main.nextStep(steps1);
      step.setDone(true);
      doneSteps1.append(step.getId());
      Main.removeStep(step.getId(), steps1);
    }

    System.out.println("Steps order (1): " + doneSteps1);

    List<Worker> workers = new ArrayList<>();

    for (int i = 0; i < Worker.NUM_WORKERS; i++) {
      workers.add(new Worker());
    }

    int seconds = 0;
    var doneSteps2 = new StringBuilder();

    while (doneSteps2.length() != totalSteps
        || workers.stream().filter(Worker::isIdle).count() != Worker.NUM_WORKERS) {

      TreeSet<String> possibleSteps = (TreeSet<String>) Main.nextSteps(steps2);

      for (String step : possibleSteps) {
        workers.stream().filter(Worker::isIdle).forEach(worker -> {
          if (!doneSteps2.toString().contains(step)) {
            doneSteps2.append(step);
            worker.doStep(step, steps2);
          }
        });
      }

      workers.stream().forEach(w -> w.decreaseTimer(steps2));
      seconds++;
    }

    System.out.println("Elapsed seconds (2): " + seconds);
  }

  private static void setSteps(Map<String, Step> steps, String line) {
    Pattern pattern =
        Pattern.compile("Step (\\w+) must be finished before step (\\w+) can begin\\.");

    Matcher matcher = pattern.matcher(line);
    matcher.find();

    String requiredId = matcher.group(1);
    String id = matcher.group(2);

    steps.computeIfAbsent(id, Step::new);
    steps.computeIfAbsent(requiredId, Step::new);

    steps.get(id).addRequiredStep(requiredId);
  }

  private static SortedSet<String> nextSteps(Map<String, Step> steps) {
    TreeSet<String> possibleSteps = new TreeSet<>();

    steps.values().stream().filter(step -> step.getRequiredSteps().isEmpty() && !step.isDone())
        .forEach(step -> possibleSteps.add(step.getId()));

    return possibleSteps;
  }

  private static Step nextStep(Map<String, Step> steps) {
    return steps.get(Main.nextSteps(steps).first());
  }

  public static void removeStep(String id, Map<String, Step> steps) {
    steps.values().stream().forEach(step -> step.removeRequiredStep(id));
  }

}


class Worker {

  public static final int NUM_WORKERS = 5;

  private String stepId = "";
  private boolean idle = true;
  private int timer = 0;

  public void doStep(String stepId, Map<String, Step> steps) {
    this.setIdle(false);
    this.setStepId(stepId);
    this.setTimer(steps.get(stepId).getRequiredTime());
  }

  public String getStepId() {
    return stepId;
  }

  public boolean isIdle() {
    return idle;
  }

  public void setStepId(String stepId) {
    this.stepId = stepId;
  }

  public void setIdle(boolean idle) {
    this.idle = idle;
  }

  public void setTimer(int time) {
    this.timer = time;
  }

  public void decreaseTimer(Map<String, Step> steps) {
    if (timer > 0 && !idle)
      timer--;

    if (timer == 0 && !idle) {
      this.setIdle(true);
      steps.get(stepId).setDone(true);
      Main.removeStep(stepId, steps);
      this.setStepId("");
    }
  }

}


class Step {

  public static final int OFFSET = 60;

  private String id;
  private boolean done = false;
  private TreeSet<String> requiredSteps = new TreeSet<>();

  public Step(String id) {
    this.setId(id);
  }

  public int getRequiredTime() {
    return id.charAt(0) - 'A' + 1 + OFFSET;
  }

  public String getId() {
    return id;
  }

  public boolean isDone() {
    return done;
  }

  public SortedSet<String> getRequiredSteps() {
    return requiredSteps;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setDone(boolean done) {
    this.done = done;
  }

  public void addRequiredStep(String name) {
    requiredSteps.add(name);
  }

  public void removeRequiredStep(String name) {
    requiredSteps.remove(name);
  }

}
