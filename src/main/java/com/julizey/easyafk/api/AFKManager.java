package com.julizey.easyafk.api;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.api.AFKKickEvent.AFKKickReason;
import com.julizey.easyafk.api.AFKState.AFKMode;
import com.julizey.easyafk.api.AFKStopEvent.AFKStopReason;
import com.julizey.easyafk.database.DatabaseManager;
import com.julizey.easyafk.hooks.DiscordSRVHook;
import com.julizey.easyafk.hooks.Hooks;
import com.julizey.easyafk.utils.AfkEffects;
import com.julizey.easyafk.utils.Text;
import com.julizey.easyafk.utils.Text.Replaceable;

public class AFKManager {

  public long kickTime;
  public long kickTimeIncrease;
  public long afkTime;
  public long afkTimeIncrease;
  public String tabPrefix;
  public String channelId;
  private final HashMap<UUID, AFKState> states = new HashMap<UUID, AFKState>();

  public HashMap<UUID, AFKState> getPlayers() {
    return states;
  }

  public void setAFK(final Player player, final AFKMode mode, final boolean isAFK) {
    final boolean current = isAFK(player);
    if (isAFK && !current) {
      enableAFK(player, mode);
    } else if (!isAFK && current) {
      disableAFK(player, AFKStopReason.TOGGLED);
    }
  }

  public void toggleAFK(final Player player, final AFKMode mode) {
    final boolean current = isAFK(player);
    if (current) {
      disableAFK(player, AFKStopReason.TOGGLED);
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
    Hooks.consumeHook("discordsrv", (DiscordSRVHook hook) -> hook.sendMessage(player, true));

    AfkEffects.enableAFK(player);
  }

  public void disableAFK(final Player player, AFKStopReason reason) {
    final UUID playerId = player.getUniqueId();
    final AFKState state = states.get(playerId);
    states.remove(playerId);
    DatabaseManager.removeAfkPlayer(playerId);

    player.setMetadata(
        "afk",
        new FixedMetadataValue(EasyAFK.instance, Boolean.FALSE));
    Bukkit.getPluginManager().callEvent(new AFKStopEvent(player, state, reason));
    Hooks.consumeHook("discordsrv", (DiscordSRVHook hook) -> hook.sendMessage(player, false));

    AfkEffects.disableAFK(player, state);
  }

  public void kickPlayer(final Player p, AFKKickReason reason) {
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

    Bukkit.getPluginManager().callEvent(new AFKKickEvent(p, state, reason));
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

  public void enableTabHook() {
    Hooks.enable("tab");
  }

  public void disableTabHook() {
    Hooks.disable("tab");
  }

  public boolean hasTabHook() {
    return Hooks.isEnabled("tab");
  }

  public void enableWorldGuardHook() {
    Hooks.enable("worldguard");
  }

  public void disableWorldGuardHook() {
    Hooks.disable("worldguard");
  }

  public boolean hasWorldGuardHook() {
    return Hooks.isEnabled("worldguard");
  }

  public void enableDiscordHook() {
    Hooks.enable("discordsrv");
  }

  public void disableDiscordHook() {
    Hooks.disable("discordsrv");
  }

  public boolean hasDiscordHook() {
    return Hooks.isEnabled("discordsrv");
  }

  public String getDiscordChannel() {
    return channelId;
  }

  public void setDiscordChannel(String channelId) {
    this.channelId = channelId;
  }
}
