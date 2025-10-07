package com.julizey.easyafk.utils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Formats durations to a readable form
 */
public enum DurationFormatter {
  LONG(false, Integer.MAX_VALUE),
  CONCISE(true, Integer.MAX_VALUE),
  CONCISE_LOW_ACCURACY(true, 3);

  private final int accuracy;
  private final boolean concise;

  DurationFormatter(final boolean concise, final int accuracy) {
    this.concise = concise;
    this.accuracy = accuracy;
  }

  public String format(final Duration duration) {
    return format(duration, concise, accuracy);
  }

  public static String format(final Duration duration, final boolean concise) {
    return format(duration, concise, 8);
  }

  public static String format(
      final Duration duration,
      final boolean concise,
      final int elements) {
    long seconds = duration.getSeconds();
    final StringBuilder output = new StringBuilder();
    int outputSize = 0;

    for (final TimeUnit unit : TimeUnit.values()) {
      final long n = seconds / unit.duration;
      if (n > 0) {
        seconds -= unit.duration * n;
        output.append(' ').append(n).append(unit.toString(concise, n));
        outputSize++;
      }
      if (seconds <= 0 || outputSize >= elements) {
        break;
      }
    }

    if (output.length() == 0) {
      return ("0" + (TimeUnit.SECONDS.toString(concise, 0)));
    }
    return output.substring(1);
  }

  public enum TimeUnit {
    MILLISECONDS(ChronoUnit.MILLIS),
    SECONDS(ChronoUnit.SECONDS),
    MINUTES(ChronoUnit.MINUTES),
    HOURS(ChronoUnit.HOURS),
    DAYS(ChronoUnit.DAYS),
    WEEKS(ChronoUnit.WEEKS),
    MONTHS(ChronoUnit.MONTHS),
    YEARS(ChronoUnit.YEARS);

    public final long duration;
    public final String formalStringPlural;
    public final String formalStringSingular;
    public final String conciseString;

    TimeUnit(final ChronoUnit unit) {
      this.duration = unit.getDuration().toMillis();
      this.formalStringPlural = " " + unit.name().toLowerCase();
      this.formalStringSingular = " " + unit.name().substring(0, unit.name().length() - 1).toLowerCase();
      this.conciseString = String.valueOf(Character.toLowerCase(unit.name().charAt(0)));
    }

    public String toString(final boolean concise, final long n) {
      if (concise) {
        return this.conciseString;
      }
      return n == 1 ? this.formalStringSingular : this.formalStringPlural;
    }
  }
}
