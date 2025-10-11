package com.julizey.easyafk.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.api.AFKState;
import com.julizey.easyafk.utils.Config.AFKModeConfig;
import com.julizey.easyafk.utils.Text.Replaceable;

public class AfkEffects {

  public static void enableAFK(final Player player, final AFKState state) {
    final AFKModeConfig modeConfig = EasyAFK.config.getModeConfig(true, state.getMode());

    if (modeConfig.message) {
      Text.send(player, "messages.afk");
    }
    if (modeConfig.broadcast) {
      final String msg = Text.format(
          "messages.afk-broadcast",
          true,
          true,
          new Replaceable("%player%", player.getDisplayName()));
      for (final Player p : Bukkit.getOnlinePlayers()) {
        p.sendMessage(msg);
      }
    }

    if (modeConfig.title) {
      player.sendTitle(
          EasyAFK.config.afkTitle,
          EasyAFK.config.afkSubtitle,
          10,
          70,
          20);
    }
    if (modeConfig.effects) {
      EasyAFK.instance.animationManager.play(player, "enable");
    }
  }

  public static void disableAFK(final Player player, final AFKState state) {
    final AFKModeConfig modeConfig = EasyAFK.config.getModeConfig(false, state.getMode());

    if (modeConfig.message) {
      Text.send(player, "messages.unafk");
    }
    if (modeConfig.broadcast) {
      final String msg = Text.format(
          "messages.unafk-broadcast",
          true,
          true,
          new Replaceable("%player%", player.getName()));
      for (final Player p : Bukkit.getOnlinePlayers()) {
        p.sendMessage(msg);
      }
    }

    if (modeConfig.title) {
      player.sendTitle(
          EasyAFK.config.unafkTitle,
          EasyAFK.config.unafkSubtitle,
          10,
          70,
          20);
    }
    if (modeConfig.effects) {
      EasyAFK.instance.animationManager.play(player, "disable");
    }
  }
}
