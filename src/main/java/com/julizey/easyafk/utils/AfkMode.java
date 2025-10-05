package com.julizey.easyafk.utils;

public enum AfkMode {
  SOFT,
  HARD;

  private AfkMode() {
  }

  public boolean toBool() {
    return this.equals(HARD);
  }

  public static AfkMode fromBool(boolean value) {
    return value ? HARD : SOFT;
  }
}
