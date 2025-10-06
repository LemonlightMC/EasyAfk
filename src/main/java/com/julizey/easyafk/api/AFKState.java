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

    public static AFKMode fromBool(final boolean value) {
      return value ? HARD : SOFT;
    }
  }

  private AFKMode mode;
  private long last_active;

  public AFKState(final AFKMode mode, final long last_active) {
    this.mode = mode;
    this.last_active = last_active;
  }

  public void setLastActive(final long last_active) {
    this.last_active = last_active;
  }

  public long getLastActive() {
    return last_active;
  }

  public void setMode(final AFKMode mode) {
    this.mode = mode;
  }

  public AFKMode getMode() {
    return mode;
  }

  @Override
  public int hashCode() {
    final int result = 31 + ((mode == null) ? 0 : mode.hashCode());
    return 31 * result + (int) (last_active ^ (last_active >>> 32));
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final AFKState other = (AFKState) obj;
    return mode == other.mode && last_active == other.last_active;
  }

  @Override
  public String toString() {
    return "AFKState [mode=" + mode + ", last_active=" + last_active + "]";
  }

}
