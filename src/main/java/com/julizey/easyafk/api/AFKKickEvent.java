package com.julizey.easyafk.api;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AFKKickEvent extends Event {

  private final Player player;
  private final long duration;
  private final AFKKickReason reason;

  private static final HandlerList HANDLERS = new HandlerList();

  public static HandlerList getHandlerList() {
    return HANDLERS;
  }

  @Override
  public HandlerList getHandlers() {
    return HANDLERS;
  }

  public AFKKickEvent(final Player player, final AFKState state, AFKKickReason reason) {
    super(true);
    if (player == null) {
      throw new IllegalArgumentException("Invalid Player");
    }
    if (state == null) {
      throw new IllegalArgumentException("Invalid State");
    }
    this.player = player;
    this.duration = System.currentTimeMillis() - state.getLastActive();
    this.reason = reason;
  }

  public Player getPlayer() {
    return player;
  }

  public long getDuration() {
    return duration;
  }

  public AFKKickReason getReason() {
    return reason;
  }

  public Location getLocation() {
    return player.getLocation();
  }

  public World getWorld() {
    return player.getWorld();
  }

  @Override
  public int hashCode() {
    int result = 31 + player.hashCode();
    result = 31 * result + (int) (duration ^ (duration >>> 32));
    return 31 * result + reason.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final AFKKickEvent other = (AFKKickEvent) obj;
    if (player == null && other.player != null || reason == null && other.reason != null) {
      return false;
    }
    return player.equals(other.player) && duration == other.duration && reason.equals(other.reason);
  }

  @Override
  public String toString() {
    return "AFKKickEvent [player=" + player + ", duration=" + duration + ", reason=" + reason + "]";
  }

  public static enum AFKKickReason {
    KICKED("Kicked"),
    DISCONNECT("Disconnected"),
    TOO_LONG_AFK("Too long AFK"),
    SERVER_FULL("Server was full");

    private String message;

    private AFKKickReason(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }
}
