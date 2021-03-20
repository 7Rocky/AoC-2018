package day_24;

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

  static String answer1 = "Group units remaining (1): 19381";
  static String answer2 = "Remaining units with minimum boost to win (2): 3045";

  public static void main(String[] args) {
    if (args.length == 1 && args[0].equals("time")) {
      System.out.println(Calendar.getInstance().getTimeInMillis());
      return;
    }

    List<String> outputs = new ArrayList<>();

    try (var bufferedReader = new BufferedReader(new FileReader("output.txt"))) {
      bufferedReader.lines().forEach(outputs::add);
    } catch (IOException e) {
      e.printStackTrace();
    }

    long init = Long.parseLong(outputs.get(0));
    double time = (Calendar.getInstance().getTimeInMillis() - init) / 1000.0;

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
    return WHITE_BOLD_BRIGHT + GREEN_BACKGROUND + " PASS " + RESET;
  }

  private static String getFailMessage() {
    return WHITE_BOLD_BRIGHT + RED_BACKGROUND + " FAIL " + RESET;
  }

  private static String getTimeMessage(double time) {
    return WHITE_BOLD_BRIGHT + " Time "
        + (time < 3 ? GREEN_BOLD_BRIGHT : time < 20 ? YELLOW_BOLD_BRIGHT : RED_BOLD_BRIGHT)
        + String.format(" %.3f s" + RESET, time).replace(',', '.');
  }

}
