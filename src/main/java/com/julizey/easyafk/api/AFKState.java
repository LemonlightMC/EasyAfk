package com.julizey.easyafk.api;

public class AFKState {
  public static enum AFKMode {
    SOFT,
    HARD;

    private AFKMode() {
    }

    public boolean toBool() {
      return this.equals(HARD);
    }

    public static AFKMode fromBool(boolean value) {
      return value ? HARD : SOFT;
    }
  }

  private AFKMode mode;
  private long last_active;

  public AFKState(AFKMode mode, long last_active) {
    this.mode = mode;
    this.last_active = last_active;
  }
}
