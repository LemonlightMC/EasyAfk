package com.julizey.easyafk.api;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.api.AFKState.AFKMode;
import com.julizey.easyafk.database.DatabaseManager;
import com.julizey.easyafk.hooks.TabIntegration;
import com.julizey.easyafk.hooks.WorldGuardIntegration;
import com.julizey.easyafk.utils.AfkEffects;

public class AFKManager {

  private int kickTime;
  private int kickTimeIncrease;
  public HashMap<UUID, AFKState> states = new HashMap<UUID, AFKState>();

  public boolean checkAFK(Player player) {
    return false;
  }

  public HashMap<UUID, AFKState> getPlayers() {
    return states;
  }

  public void setAFK(Player player, AFKMode mode, boolean isAFK) {
    boolean current = isAFK(player);
    if (isAFK && !current) {
      enableAFK(player, mode);
    } else if (!isAFK && current) {
      disableAFK(player);
    }
  }

  public void toggleAFK(Player player, AFKMode mode) {
    boolean current = isAFK(player);
    if (current) {
      disableAFK(player);
    } else {
      enableAFK(player, mode);
    }
  }

  public boolean isAFK(Player player) {
    return states.containsKey(player.getUniqueId());
  }

  public boolean isAFK(UUID uuid) {
    return states.containsKey(uuid);
  }

  public void enableAFK(Player player, AFKMode mode) {
    final UUID playerId = player.getUniqueId();
    states.put(playerId, new AFKState(mode, System.currentTimeMillis()));
    player.setMetadata(
        "afk",
        new FixedMetadataValue(EasyAFK.instance, mode));
    if (!DatabaseManager.containsAfkPlayer(playerId)) {
      DatabaseManager.addAfkPlayer(playerId, mode);
    }

    AfkEffects.enableAFK(player);
  }

  public void disableAFK(Player player) {
    final UUID playerId = player.getUniqueId();
    AFKState state = states.get(playerId);
    states.remove(playerId);

    player.setMetadata(
        "afk",
        new FixedMetadataValue(EasyAFK.instance, Boolean.FALSE));
    if (DatabaseManager.containsAfkPlayer(playerId)) {
      DatabaseManager.removeAfkPlayer(playerId);
    }
    Bukkit.getPluginManager().callEvent(new AFKStopEvent(player, state));
    AfkEffects.disableAFK(player, state);
  }

  public void setAfkKickTime(int seconds) {
    this.kickTime = seconds;
    this.kickTimeIncrease = 0;
  }

  public void setAfkKickTime(int seconds, int increase) {
    this.kickTime = seconds;
    this.kickTimeIncrease = increase;
  }

  public void enableTabCompatIntegration() {
    if (EasyAFK.instance.tabIntegration == null) {
      EasyAFK.instance.tabIntegration = new TabIntegration();
    }
  }

  public void disableTabCompatIntegration() {
    if (EasyAFK.instance.tabIntegration != null) {
      EasyAFK.instance.tabIntegration.unload();
      EasyAFK.instance.tabIntegration = null;
    }
  }

  public boolean hasTabIntegration() {
    return EasyAFK.instance.tabIntegration != null;
  }

  public void disableWorldGuardCompatIntegration() {
    if (EasyAFK.instance.worldGuardIntegration != null) {
      EasyAFK.instance.worldGuardIntegration.unload();
      EasyAFK.instance.worldGuardIntegration = null;
    }
  }

  public void enableWorldGuardIntegration() {
    if (EasyAFK.instance.worldGuardIntegration == null) {
      EasyAFK.instance.worldGuardIntegration = new WorldGuardIntegration();
    }
  }

  public boolean hasWorldGuardIntegration() {
    return EasyAFK.instance.worldGuardIntegration != null;
  }
}
