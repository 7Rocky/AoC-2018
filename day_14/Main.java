package day_14;

public class Main {

  static int input = 513401;
  static StringBuilder score = new StringBuilder("37");
  static int[] elves = new int[] {0, 1};

  public static void main(String[] args) {
    String inputString = input + "";
    int inputLength = inputString.length();

    while (true) {
      Main.nextRecipe();

      int index = Main.score.length() - inputLength - 1;

      if (index > 0 && Main.score.substring(index).indexOf(inputString) != -1) {
        break;
      }
    }

    System.out.print("Scores of the recipes (1): ");
    System.out.println(Main.score.substring(Main.input, Main.input + 10));

    System.out.print("Number of recipes before input string (2): ");
    System.out.println(Main.score.indexOf(inputString));
  }

  private static void nextRecipe() {
    int[] currentRecipes = new int[2];

    currentRecipes[0] = Integer.parseInt(Main.score.substring(Main.elves[0], Main.elves[0] + 1));
    currentRecipes[1] = Integer.parseInt(Main.score.substring(Main.elves[1], Main.elves[1] + 1));

    Main.score.append(currentRecipes[0] + currentRecipes[1]);

    Main.elves[0] = (Main.elves[0] + currentRecipes[0] + 1) % Main.score.length();
    Main.elves[1] = (Main.elves[1] + currentRecipes[1] + 1) % Main.score.length();
  }

}
