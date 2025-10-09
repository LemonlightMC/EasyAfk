package com.julizey.easyafk.listener;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.api.AFKState.AFKMode;
import com.julizey.easyafk.api.AFKStopEvent.AFKStopReason;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {
  // Cancel damage if the player is Hard AFK
  @EventHandler
  public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
    if (event.getEntity() instanceof Player) {
      final Player player = (Player) event.getEntity();
      if (EasyAFK.manager.isAFK(player, AFKMode.HARD)) {
        event.setCancelled(true);
      }
    }
  }

  // Prevent player from moving while Hard AFK
  @EventHandler
  public void onPlayerMove(final PlayerMoveEvent event) {
    final Player player = event.getPlayer();
    if (EasyAFK.manager.isAFK(player, AFKMode.HARD)) {
      event.setCancelled(true);
    }
  }

  // Prevent interaction if Hard AFK
  @EventHandler
  public void onPlayerInteract(final PlayerInteractEvent event) {
    final Player player = event.getPlayer();
    if (EasyAFK.manager.isAFK(player)) {
      event.setCancelled(true);
    }
  }

  // Disable AFK (optionally) if Player Quits
  @EventHandler
  public void onPlayerQuit(final PlayerQuitEvent event) {
    if (!EasyAFK.config.disableOnLeave) {
      return;
    }
    final Player player = event.getPlayer();
    if (EasyAFK.manager.isAFK(player)) {
      EasyAFK.manager.disableAFK(player, AFKStopReason.DISCONNECT);
    }
  }
}
