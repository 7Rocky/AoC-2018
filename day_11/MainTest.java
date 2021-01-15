package day_11;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainTest {

  private static final String RESET = "\033[0m";

  private static final String RED_BACKGROUND = "\033[41m";
  private static final String GREEN_BACKGROUND = "\033[42m";

  private static final String RED_BOLD_BRIGHT = "\033[1;91m";
  private static final String GREEN_BOLD_BRIGHT = "\033[1;92m";
  private static final String YELLOW_BOLD_BRIGHT = "\033[1;93m";
  private static final String WHITE_BOLD_BRIGHT = "\033[1;97m";

  static String answer1 = "Top-right corner of 3x3 grid with max power (1): 20,50";
  static String answer2 = "Top-right corner identifier of grid with max power (2): 238,278,9";

  public static void main(String[] args) {
    List<String> outputs = new ArrayList<>();

    try (BufferedReader bufferedReader = new BufferedReader(new FileReader("output.txt"))) {
      bufferedReader.lines().forEach(outputs::add);
    } catch (IOException e) {
      e.printStackTrace();
    }

    int init = Integer.parseInt(outputs.get(0));
    double time = Calendar.getInstance().getTimeInMillis() / 1000.0 - init;

    boolean pass = true;

    List<String> correctOutputs = Arrays.asList(answer1, answer2);

    for (int i = 1; i < outputs.size(); i++) {
      if (correctOutputs.indexOf(outputs.get(i)) == -1) {
        pass = false;
        break;
      }
    }

    System.out.println(pass ? MainTest.getPassMessage() : MainTest.getFailMessage());
    System.out.println(MainTest.getTimeMessage(time));
  }

  private static String getPassMessage() {
    return new StringBuilder(WHITE_BOLD_BRIGHT).append(GREEN_BACKGROUND).append(" PASS ")
        .append(RESET).toString();
  }

  private static String getFailMessage() {
    return new StringBuilder(WHITE_BOLD_BRIGHT).append(RED_BACKGROUND).append(" FAIL ")
        .append(RESET).toString();
  }

  private static String getTimeMessage(double time) {
    return String.format(new StringBuilder(WHITE_BOLD_BRIGHT).append(" Time ")
        .append(time < 3 ? GREEN_BOLD_BRIGHT : time < 20 ? YELLOW_BOLD_BRIGHT : RED_BOLD_BRIGHT)
        .append(" %.3f s").append(RESET).toString(), time).replace(',', '.');
  }

}
