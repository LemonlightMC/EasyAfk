package com.julizey.easyafk.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AFKStartEvent extends Event {

  private final Player player;
  private final long time;

  private static final HandlerList HANDLERS = new HandlerList();

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public AFKStartEvent(final Player player) {
    super(true);
    if (player == null) {
      throw new IllegalArgumentException("Invalid Player");
    }
    this.player = player;
    this.time = System.currentTimeMillis();
  }

  public Player getPlayer() {
    return player;
  }

  public long getTime() {
    return time;
  }

  @Override
  public int hashCode() {
    return 31 * (31 + player.hashCode()) + (int) (time ^ (time >>> 32));
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final AFKStartEvent other = (AFKStartEvent) obj;
    if (player == null && other.player != null) {
      return false;
    }
    return player.equals(other.player) && time == other.time;
  }

  @Override
  public String toString() {
    return "AFKStartEvent [player=" + player + ", time=" + time + "]";
  }
}
