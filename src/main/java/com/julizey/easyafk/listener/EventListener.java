package com.julizey.easyafk.listener;

import com.julizey.easyafk.EasyAFK;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
  // Cancel damage if the player is AFK
  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Player) {
      Player player = (Player) event.getEntity();
      if (EasyAFK.instance.manager.isAFK(player)) {
        event.setCancelled(true);
      }
    }
  }

  // Prevent player from moving while AFK
  @EventHandler
  public void onPlayerMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    if (EasyAFK.instance.manager.isAFK(player)) {
      // Cancel movement if the player is AFK and attempting to move
      event.setCancelled(true);
    }
  }

  // Prevent interaction if AFK
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    Player player = event.getPlayer();
    if (EasyAFK.instance.manager.isAFK(player)) {
      event.setCancelled(true); // Cancel interaction if the player is AFK
    }
  }

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
