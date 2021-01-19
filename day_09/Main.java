package day_09;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

  static List<Marble> marblesCircle = new ArrayList<>();
  static List<Player> players = new ArrayList<>();

  static int numPlayers = 0;
  static int numRounds = 0;

  public static void main(String[] args) {
    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      String input = bufferedReader.readLine();

      Pattern pattern = Pattern.compile("(\\d+) players; last marble is worth (\\d+) points");
      Matcher matcher = pattern.matcher(input);

      while (matcher.find()) {
        numPlayers = Integer.parseInt(matcher.group(1));
        numRounds = Integer.parseInt(matcher.group(2));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    resetPlayers();
    resetMarblesCircle();
    play();

    long maxScore = players.stream().map(Player::getScore).max(Long::compareTo).orElse(-1L);

    System.out.println("Maximum score (1): " + maxScore);

    numRounds *= 100;

    resetPlayers();
    resetMarblesCircle();
    play();

    maxScore = players.stream().map(Player::getScore).max(Long::compareTo).orElse(-1L);

    System.out.println("Maximum score (2): " + maxScore);
  }

  private static void resetPlayers() {
    players.clear();

    for (int i = 0; i < numPlayers; i++) {
      players.add(new Player());
    }
  }

  private static void resetMarblesCircle() {
    marblesCircle.clear();

    for (int i = 0; i <= numRounds; i++) {
      marblesCircle.add(new Marble(i));
    }
  }

  private static void play() {
    Marble currentMarble = marblesCircle.get(0);

    for (int round = 1; round <= numRounds; round++) {
      Marble newMarble = marblesCircle.get(round);
      Player player = players.get((round - 1) % numPlayers);

      if (currentMarble != null) {
        if (round == 1) {
          marblesCircle.get(round).setNextClockWise(marblesCircle.get(0));
          marblesCircle.get(round).setNextCounterClockWise(marblesCircle.get(0));
          marblesCircle.get(0).setNextClockWise(marblesCircle.get(round));
          marblesCircle.get(0).setNextCounterClockWise(marblesCircle.get(round));
        } else if (round % 23 != 0) {
          Marble marble1 = currentMarble.getNextClockWise();
          Marble marble2 = currentMarble.getNextClockWise().getNextClockWise();

          marble1.setNextClockWise(newMarble);
          newMarble.setNextCounterClockWise(marble1);
          newMarble.setNextClockWise(marble2);
          marble2.setNextCounterClockWise(newMarble);
        } else {
          long currentScore = player.getScore();
          Marble removedMarble = currentMarble.getNextCounterClockWise().getNextCounterClockWise()
              .getNextCounterClockWise().getNextCounterClockWise().getNextCounterClockWise()
              .getNextCounterClockWise().getNextCounterClockWise();
          long removedMarbleNumber = removedMarble.getNumber();
          removedMarble.getNextCounterClockWise()
              .setNextClockWise(removedMarble.getNextClockWise());
          removedMarble.getNextClockWise()
              .setNextCounterClockWise(removedMarble.getNextCounterClockWise());

          newMarble = removedMarble.getNextClockWise();

          removedMarble.setNextClockWise(null);
          removedMarble.setNextCounterClockWise(null);

          player.setScore(currentScore + round + removedMarbleNumber);
        }

        currentMarble = newMarble;
      }
    }
  }

}


class Player {

  private long score = 0L;

  public long getScore() {
    return score;
  }

  public void setScore(long score) {
    this.score = score;
  }

}


class Marble {

  private int number;
  private Marble nextClockWise;
  private Marble nextCounterClockWise;

  public Marble(int number) {
    this.setNumber(number);
  }

  public int getNumber() {
    return number;
  }

  public Marble getNextClockWise() {
    return nextClockWise;
  }

  public Marble getNextCounterClockWise() {
    return nextCounterClockWise;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public void setNextClockWise(Marble nextClockWise) {
    this.nextClockWise = nextClockWise;
  }

  public void setNextCounterClockWise(Marble nextCounterClockWise) {
    this.nextCounterClockWise = nextCounterClockWise;
  }

}
