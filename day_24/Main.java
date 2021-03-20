package day_24;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.stream.Collectors;

public class Main {

  public static void main(String[] args) {
    Set<Group> immuneTeam = new TreeSet<>();
    Set<Group> infectionTeam = new TreeSet<>();

    Main.setTeams(immuneTeam, infectionTeam);

    int remainingUnits = Main.battle(immuneTeam, infectionTeam);

    System.out.println("Group units remaining (1): " + remainingUnits);

    immuneTeam.removeIf(g -> g.getUnits() <= 0);
    infectionTeam.removeIf(g -> g.getUnits() <= 0);

    for (int boost = 1; immuneTeam.isEmpty() || remainingUnits == -1; boost++) {
      Main.setTeams(immuneTeam, infectionTeam);

      for (Group g : immuneTeam) {
        g.getDamage().boost(boost);
      }

      remainingUnits = Main.battle(immuneTeam, infectionTeam);

      immuneTeam.removeIf(g -> g.getUnits() <= 0);
      infectionTeam.removeIf(g -> g.getUnits() <= 0);
    }

    System.out.println("Remaining units with minimum boost to win (2): " + remainingUnits);
  }

  public static void setTeams(Set<Group> immuneTeam, Set<Group> infectionTeam) {
    try (var bufferedReader = new BufferedReader(new FileReader("input.txt"))) {
      Main.addToTeam(immuneTeam, bufferedReader);
      Main.addToTeam(infectionTeam, bufferedReader);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static int battle(Set<Group> immuneTeam, Set<Group> infectionTeam) {
    while (!immuneTeam.isEmpty() && !infectionTeam.isEmpty()) {
      immuneTeam = reSortTeam(immuneTeam);
      infectionTeam = reSortTeam(infectionTeam);

      Main.selectTargets(infectionTeam, immuneTeam);
      Main.selectTargets(immuneTeam, infectionTeam);

      Set<Group> attackedGroups =
          new TreeSet<>(Comparator.comparingInt(g -> -g.getAttackedBy().getInitiative()));

      immuneTeam.stream().filter(g -> g.getAttackedBy() != null).forEach(attackedGroups::add);
      infectionTeam.stream().filter(g -> g.getAttackedBy() != null).forEach(attackedGroups::add);

      boolean hurted = false;

      for (Group group : attackedGroups) {
        if (group.getUnits() > 0) {
          hurted |= group.receiveAttack();
        }
      }

      immuneTeam.removeIf(g -> g.getUnits() <= 0);
      infectionTeam.removeIf(g -> g.getUnits() <= 0);

      if (!hurted) {
        return -1;
      }
    }

    if (immuneTeam.isEmpty()) {
      return infectionTeam.stream().mapToInt(Group::getUnits).sum();
    }

    return immuneTeam.stream().mapToInt(Group::getUnits).sum();
  }

  public static Set<Group> reSortTeam(Set<Group> team) {
    Set<Group> newTeam = new TreeSet<>();
    team.stream().forEach(newTeam::add);
    return newTeam;
  }

  public static void selectTargets(Set<Group> attackingTeam, Set<Group> defendingTeam) {
    for (Group group : attackingTeam) {
      int maxDealtDamage = 0;
      Group target = null;

      for (Group enemy : defendingTeam.stream().filter(g -> g.getAttackedBy() == null)
          .collect(Collectors.toCollection(() -> new TreeSet<>(Group::compareTo)))) {
        int dealtDamage = enemy.getDealtDamage(group);

        if (maxDealtDamage < dealtDamage || (maxDealtDamage == dealtDamage && target != null
            && (target.effectivePower() < enemy.effectivePower()
                || (target.effectivePower() == enemy.effectivePower()
                    && target.getInitiative() < enemy.getInitiative())))) {
          maxDealtDamage = dealtDamage;
          target = enemy;
        }
      }

      if (target != null && maxDealtDamage > 0) {
        target.setAttackedBy(group);
      }
    }
  }

  public static void addToTeam(Set<Group> team, BufferedReader bufferedReader) {
    Pattern completePattern = Pattern.compile(
        "(\\d+) units each with (\\d+) hit points \\(?([\\s\\w,;]*)\\)? with an attack that does (\\d+) (\\w+) damage at initiative (\\d+)");
    Pattern reducedPattern = Pattern.compile(
        "(\\d+) units each with (\\d+) hit points (\\s?)with an attack that does (\\d+) (\\w+) damage at initiative (\\d+)");

    team.clear();

    try {
      String line = bufferedReader.readLine();

      while (line != null && !line.isEmpty()) {
        if (line.contains(":")) {
          line = bufferedReader.readLine();
          continue;
        }

        Matcher matcher = reducedPattern.matcher(line);
        Set<String> weakTo = new TreeSet<>();
        Set<String> immuneTo = new TreeSet<>();

        if (line.contains("(")) {
          matcher = completePattern.matcher(line);
        }

        matcher.find();

        int units = Integer.parseInt(matcher.group(1));
        int hp = Integer.parseInt(matcher.group(2));

        if (line.contains("(")) {
          weakTo = Main.getWeakOrImmune("weak", matcher.group(3));
          immuneTo = Main.getWeakOrImmune("immune", matcher.group(3));
        }

        int amount = Integer.parseInt(matcher.group(4));
        String type = matcher.group(5);
        int initiative = Integer.parseInt(matcher.group(6));

        team.add(new Group(units, hp, weakTo, immuneTo, new Damage(amount, type), initiative));

        line = bufferedReader.readLine();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Set<String> getWeakOrImmune(String kind, String info) {
    if (!info.contains(kind)) {
      return new TreeSet<>();
    }

    if (!info.contains(";")) {
      return Arrays.stream(info.substring(kind.length() + " to ".length()).split(", "))
          .collect(Collectors.toSet());
    }

    int kindIndex = info.indexOf(kind);
    int colonIndex = info.indexOf(";");

    if (kindIndex > colonIndex) {
      return Arrays
          .stream(info.substring(colonIndex + 2 + kind.length() + " to ".length()).split(", "))
          .collect(Collectors.toSet());
    }

    return Arrays.stream(info.substring(kind.length() + " to ".length(), colonIndex).split(", "))
        .collect(Collectors.toSet());
  }

}


class Damage {

  private int amount;
  private String type;

  public Damage(int amount, String type) {
    this.setAmount(amount);
    this.setType(type);
  }

  public void boost(int boost) {
    amount += boost;
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Damage) {
      Damage damage = (Damage) object;
      return amount == damage.getAmount() && type.equals(damage.getType());
    }

    return false;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  @Override
  public String toString() {
    return amount + " " + type;
  }

  public int getAmount() {
    return amount;
  }

  public String getType() {
    return type;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public void setType(String type) {
    this.type = type;
  }

}


class Group implements Comparable<Group> {

  private int units;
  private int hp;
  private Set<String> weakTo;
  private Set<String> immuneTo;
  private Damage damage;
  private int initiative;
  private Group attackedBy = null;

  public Group(int units, int hp, Set<String> weakTo, Set<String> immuneTo, Damage damage,
      int initiative) {
    this.setUnits(units);
    this.setHp(hp);
    this.setWeakTo(weakTo);
    this.setImmuneTo(immuneTo);
    this.setDamage(damage);
    this.setInitiative(initiative);
  }

  public int getDealtDamage(Group attacker) {
    if (weakTo.contains(attacker.getDamage().getType())) {
      return 2 * attacker.effectivePower();
    }

    if (immuneTo.contains(attacker.getDamage().getType())) {
      return 0;
    }

    return attacker.effectivePower();
  }

  public boolean receiveAttack() {
    if (attackedBy.getUnits() <= 0) {
      attackedBy = null;
      return false;
    }

    int killedUnits = this.getDealtDamage(attackedBy) / hp;
    units -= killedUnits;
    attackedBy = null;

    return killedUnits != 0;
  }

  @Override
  public int compareTo(Group group) {
    if (this.effectivePower() == group.effectivePower()) {
      return group.getInitiative() - initiative;
    }

    return group.effectivePower() - this.effectivePower();
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof Group) {
      Group group = (Group) object;
      return units == group.getUnits() && hp == group.getHp() && weakTo.equals(group.getWeakTo())
          && immuneTo.equals(group.getImmuneTo()) && damage.equals(group.getDamage())
          && initiative == group.getInitiative();
    }

    return false;
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  @Override
  public String toString() {
    return units + " units each with " + hp + " hit points (weak to " + weakTo + "; immune to "
        + immuneTo + ") with an attack that does " + damage + " damage at initiative " + initiative;
  }

  public int effectivePower() {
    return units * damage.getAmount();
  }

  public int getUnits() {
    return units;
  }

  public int getHp() {
    return hp;
  }

  public Set<String> getWeakTo() {
    return weakTo;
  }

  public Set<String> getImmuneTo() {
    return immuneTo;
  }

  public Damage getDamage() {
    return damage;
  }

  public int getInitiative() {
    return initiative;
  }

  public Group getAttackedBy() {
    return attackedBy;
  }

  public void setUnits(int units) {
    this.units = units;
  }

  public void setHp(int hp) {
    this.hp = hp;
  }

  public void setWeakTo(Set<String> weakTo) {
    this.weakTo = weakTo;
  }

  public void setImmuneTo(Set<String> immuneTo) {
    this.immuneTo = immuneTo;
  }

  public void setDamage(Damage damage) {
    this.damage = damage;
  }

  public void setInitiative(int initiative) {
    this.initiative = initiative;
  }

  public void setAttackedBy(Group attackedBy) {
    this.attackedBy = attackedBy;
  }

}
