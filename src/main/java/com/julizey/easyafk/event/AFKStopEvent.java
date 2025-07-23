package com.julizey.easyafk.event;

import org.bukkit.entity.Player;

public class AFKStopEvent {

  private final Player player;

  /**
   * @param player           The player being set as AFK
   */
  public AFKStopEvent(Player player) {
    this.player = player;
  }

  public Player getPlayer() {
    return player;
  }

  public long getTotalTime() {
    return 0L;
  }
}
