package com.julizey.easyafk.event;

import com.julizey.easyafk.EasyAFK;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event) {
    if (!EasyAFK.config.disableOnLeave) {
      return;
    }
    Player player = event.getPlayer();
    if (EasyAFK.instance.manager.isAFK(player)) {
      EasyAFK.instance.manager.disableAFK(player);
    }
  }
}
