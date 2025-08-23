package com.julizey.easyafk.event;

import org.bukkit.entity.Player;

public class AFKStartEvent {

  private final Player player;
  private final long time;

  public AFKStartEvent(Player player) {
    this.player = player;
    this.time = System.currentTimeMillis();
  }

  public Player getPlayer() {
    return player;
  }

  public long getTime() {
    return time;
  }
}
