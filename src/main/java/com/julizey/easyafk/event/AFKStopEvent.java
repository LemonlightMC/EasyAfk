package com.julizey.easyafk.event;

import org.bukkit.entity.Player;

public class AFKStopEvent {

  private final Player player;
  private final long duration;

  public AFKStopEvent(Player player, long time) {
    this.player = player;
    this.duration = System.currentTimeMillis() - time;
  }

  public Player getPlayer() {
    return player;
  }

  public long getTotalTime() {
    return duration;
  }
}
