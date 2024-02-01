package de.thnuernberg.sep.gruppe5.be.entity;

public enum Season {
  Winter,
  Sommer;

  @Override
  public String toString() {
    return switch (this.ordinal()) {
      case 0 -> "Wintersemester";
      case 1 -> "Sommersemester";
      default -> null;
    };
  }
}
