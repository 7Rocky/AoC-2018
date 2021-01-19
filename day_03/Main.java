package day_03;

import java.io.BufferedReader;
import java.io.FileReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

  public static void main(String[] args) {
    List<Claim> claims = new ArrayList<>();

    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      bufferedReader.lines().forEach(line -> Main.setClaims(claims, line));
    } catch (IOException e) {
      e.printStackTrace();
    }

    int maxLeft = 0;
    int maxTop = 0;
    int maxWidth = 0;
    int maxHeight = 0;

    for (Claim claim : claims) {
      if (claim.getLeft() > maxLeft) {
        maxLeft = claim.getLeft();
      }

      if (claim.getTop() > maxTop) {
        maxTop = claim.getTop();
      }

      if (claim.getWidth() > maxWidth) {
        maxWidth = claim.getWidth();
      }

      if (claim.getHeight() > maxHeight) {
        maxHeight = claim.getHeight();
      }
    }

    List<String> fabric = new ArrayList<>();

    for (int y = 0; y < maxTop + maxHeight; y++) {
      fabric.add(".".repeat(maxLeft + maxWidth));
    }

    Main.setFabric(fabric, claims);

    System.out.print("Number of claim overlaps (1): ");
    System.out.println(Main.countOverlaps(fabric, 'X'));

    System.out.print("Non-overlaping claim ID (2): ");
    System.out.println(Main.findNonOverlapingClaimId(fabric, claims));
  }

  public static int countOverlaps(List<String> fabric, char letter) {
    return fabric.stream().map(r -> (int) r.chars().filter(c -> c == letter).count()).reduce(0,
        Integer::sum);
  }

  private static void setClaims(List<Claim> claims, String line) {
    Pattern pattern = Pattern.compile("#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)");
    Matcher matcher = pattern.matcher(line);

    while (matcher.find()) {
      int id = Integer.parseInt(matcher.group(1));
      int left = Integer.parseInt(matcher.group(2));
      int top = Integer.parseInt(matcher.group(3));
      int width = Integer.parseInt(matcher.group(4));
      int height = Integer.parseInt(matcher.group(5));

      claims.add(new Claim(id, left, top, width, height));
    }
  }

  private static void setFabric(List<String> fabric, List<Claim> claims) {
    for (Claim claim : claims) {
      for (int y = claim.getTop(); y < claim.getTop() + claim.getHeight(); y++) {
        String row = fabric.get(y);
        var newRow = new StringBuilder(row.substring(0, claim.getLeft()));

        for (int x = claim.getLeft(); x < claim.getLeft() + claim.getWidth(); x++) {
          newRow.append(row.charAt(x) == '.' ? '#' : 'X');
        }

        newRow.append(row.substring(claim.getLeft() + claim.getWidth()));
        fabric.set(y, newRow.toString());
      }
    }
  }

  private static int findNonOverlapingClaimId(List<String> fabric, List<Claim> claims) {
    int nonOverlapingClaimId = -1;

    for (Claim claim : claims) {
      nonOverlapingClaimId = claim.getId();

      for (int y = claim.getTop(); y < claim.getTop() + claim.getHeight(); y++) {
        String row = fabric.get(y);

        for (int x = claim.getLeft(); x < claim.getLeft() + claim.getWidth(); x++) {
          if (row.charAt(x) == 'X') {
            nonOverlapingClaimId = -1;
            break;
          }
        }
      }

      if (nonOverlapingClaimId != -1) {
        break;
      }
    }

    return nonOverlapingClaimId;
  }

}


class Claim {

  private int id;
  private int left;
  private int top;
  private int width;
  private int height;

  public Claim(int id, int left, int top, int width, int height) {
    this.setId(id);
    this.setLeft(left);
    this.setTop(top);
    this.setWidth(width);
    this.setHeight(height);
  }

  public int getId() {
    return id;
  }

  public int getLeft() {
    return left;
  }

  public int getTop() {
    return top;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setLeft(int left) {
    this.left = left;
  }

  public void setTop(int top) {
    this.top = top;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public void setHeight(int height) {
    this.height = height;
  }

}
