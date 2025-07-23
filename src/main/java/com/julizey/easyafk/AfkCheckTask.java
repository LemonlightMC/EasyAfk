package com.julizey.easyafk;

import com.julizey.easyafk.hooks.DatabaseManager;
import com.julizey.easyafk.utils.Text;
import com.julizey.easyafk.utils.Text.Replaceable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AfkCheckTask extends BukkitRunnable {

  public void run() {
    final long currentTime = System.currentTimeMillis();

    for (final Player player : Bukkit.getOnlinePlayers()) {
      if (
        EasyAFK.config.bypassAfkEnabled &&
        player.hasPermission("easyafk.bypass.afk")
      ) {
        continue;
      }
      if (EasyAFK.config.ignoredWorlds.contains(player.getWorld().getName())) {
        continue;
      }
      if (
        EasyAFK.instance.worldGuardIntegration != null &&
        EasyAFK.instance.worldGuardIntegration.isInAfkBypassSection(player)
      ) {
        continue;
      }

      final long lastActive = DatabaseManager.getLastActive(
        player.getUniqueId()
      );
      if (lastActive <= 0) {
        DatabaseManager.updateLastActive(player.getUniqueId(), currentTime);
        continue;
      }
      if (
        currentTime -
        lastActive >
        EasyAFK.config.kickTimeout +
        EasyAFK.config.afkTimeout &&
        (
          EasyAFK.config.kickEnabled ||
          EasyAFK.config.kickEnabledWhenFull &&
          Bukkit.getOnlinePlayers().size() == Bukkit.getMaxPlayers()
        ) &&
        EasyAFK.instance.afkState.afkPlayers.contains(player.getUniqueId())
      ) {
        Bukkit
          .getScheduler()
          .runTask(EasyAFK.instance, () -> kickPlayer(player));
      } else if (
        currentTime - lastActive > EasyAFK.config.afkTimeout &&
        !EasyAFK.instance.afkState.afkPlayers.contains(player.getUniqueId())
      ) {
        EasyAFK.instance.afkState.toggle(player);
      }
    }
  }

  private void kickPlayer(final Player player) {
    if (
      EasyAFK.config.bypassKickEnabled &&
      player.hasPermission("easyafk.bypass.kick")
    ) {
      return;
    }
    player.kickPlayer(
      Text.format(
        "messages.kick",
        true,
        true,
        new Replaceable("%player%", player.getName())
      )
    );
    if (EasyAFK.config.disableOnKick) {
      EasyAFK.instance.afkState.afkPlayers.remove(player.getUniqueId());
    }
    DatabaseManager.updateLastActive(player.getUniqueId(), -1);
  }
}
