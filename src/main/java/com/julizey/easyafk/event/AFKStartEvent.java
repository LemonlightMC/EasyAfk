package com.julizey.easyafk.event;

import org.bukkit.entity.Player;

public class AFKStartEvent {

  private final Player player;

  /**
   * @param player           The player being set as AFK
   */
  public AFKStartEvent(Player player) {
    this.player = player;
  }

  public Player getPlayer() {
    return player;
  }
}
