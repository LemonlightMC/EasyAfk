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
import com.julizey.easyafk.utils.Text;
import com.julizey.easyafk.utils.Text.Replaceable;

public class AFKManager {

  public long kickTime;
  public long kickTimeIncrease;
  public long afkTime;
  public long afkTimeIncrease;
  public String tabPrefix;
  private final HashMap<UUID, AFKState> states = new HashMap<UUID, AFKState>();

  public HashMap<UUID, AFKState> getPlayers() {
    return states;
  }

  public void setAFK(final Player player, final AFKMode mode, final boolean isAFK) {
    final boolean current = isAFK(player);
    if (isAFK && !current) {
      enableAFK(player, mode);
    } else if (!isAFK && current) {
      disableAFK(player);
    }
  }

  public void toggleAFK(final Player player, final AFKMode mode) {
    final boolean current = isAFK(player);
    if (current) {
      disableAFK(player);
    } else {
      enableAFK(player, mode);
    }
  }

  public boolean isAFK(final Player player, final AFKMode mode) {
    final AFKState state = states.get(player.getUniqueId());
    return state != null && state.getMode().equals(mode);
  }

  public boolean isAFK(final UUID uuid, final AFKMode mode) {
    final AFKState state = states.get(uuid);
    return state != null && state.getMode().equals(mode);
  }

  public boolean isAFK(final Player player) {
    return states.containsKey(player.getUniqueId());
  }

  public boolean isAFK(final UUID uuid) {
    return states.containsKey(uuid);
  }

  public void enableAFK(final Player player, final AFKMode mode) {
    final UUID playerId = player.getUniqueId();
    states.put(playerId, new AFKState(mode, System.currentTimeMillis()));
    DatabaseManager.addAfkPlayer(playerId, mode);
    player.setMetadata(
        "afk",
        new FixedMetadataValue(EasyAFK.instance, mode));
    Bukkit.getPluginManager().callEvent(new AFKStartEvent(player));

    AfkEffects.enableAFK(player);
  }

  public void disableAFK(final Player player) {
    final UUID playerId = player.getUniqueId();
    final AFKState state = states.get(playerId);
    states.remove(playerId);
    DatabaseManager.removeAfkPlayer(playerId);

    player.setMetadata(
        "afk",
        new FixedMetadataValue(EasyAFK.instance, Boolean.FALSE));

    Bukkit.getPluginManager().callEvent(new AFKStopEvent(player, state));
    AfkEffects.disableAFK(player, state);
  }

  public void kickPlayer(final Player p) {
    if (EasyAFK.config.bypassKickEnabled && p.hasPermission("easyafk.bypass.kick")) {
      return;
    }
    final UUID playerId = p.getUniqueId();
    final AFKState state = states.get(playerId);
    states.remove(playerId);

    if (EasyAFK.config.disableOnKick) {
      DatabaseManager.removeAfkPlayer(playerId);
      p.setMetadata(
          "afk",
          new FixedMetadataValue(EasyAFK.instance, Boolean.FALSE));
    }

    Bukkit.getPluginManager().callEvent(new AFKKickEvent(p, state));
    DatabaseManager.updateLastActive(playerId, -1);
    p.kickPlayer(
        Text.format(
            "messages.kick",
            true,
            true,
            new Replaceable("%player%", p.getName())));
  }

  public void setAfkTime(final long seconds) {
    this.afkTime = seconds;
    this.afkTimeIncrease = 0;
  }

  public void setAfkTime(final long seconds, final long increase) {
    this.afkTime = seconds;
    this.afkTimeIncrease = increase;
  }

  public long getAfkTime() {
    return afkTime;
  }

  public long getAfkTimeIncrease() {
    return afkTimeIncrease;
  }

  public void setKickTime(final long seconds) {
    this.kickTime = seconds;
    this.kickTimeIncrease = 0;
  }

  public void setKickTime(final long seconds, final long increase) {
    this.kickTime = seconds;
    this.kickTimeIncrease = increase;
  }

  public long getKickTime() {
    return kickTime;
  }

  public long getKickTimeIncrease() {
    return kickTimeIncrease;
  }

  public String getTabPrefix() {
    return tabPrefix;
  }

  public void setTabPrefix(final String tabPrefix) {
    this.tabPrefix = Text.convertColor(tabPrefix);
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
