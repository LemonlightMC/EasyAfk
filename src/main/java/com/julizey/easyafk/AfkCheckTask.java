package com.julizey.easyafk;

import com.julizey.easyafk.api.AFKState.AFKMode;
import com.julizey.easyafk.database.DatabaseManager;
import com.julizey.easyafk.hooks.Hooks;
import com.julizey.easyafk.hooks.WorldGuardHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AfkCheckTask extends BukkitRunnable {

  public void run() {
    final long currentTime = System.currentTimeMillis();
    final boolean isFull = EasyAFK.config.kickEnabledWhenFull &&
        Bukkit.getOnlinePlayers().size() == Bukkit.getMaxPlayers();

    for (final Player player : Bukkit.getOnlinePlayers()) {
      if (canBypass(player)) {
        continue;
      }

      final long lastActive = DatabaseManager.getLastActive(
          player.getUniqueId());
      if (lastActive <= 0) {
        DatabaseManager.updateLastActive(player.getUniqueId(), currentTime);
        continue;
      }

      if (currentTime - lastActive > EasyAFK.config.afkTimeout) {
        if (EasyAFK.config.kickEnabled && (isFull || currentTime - lastActive > EasyAFK.config.kickTimeout)) {
          Bukkit
              .getScheduler()
              .runTask(EasyAFK.instance, () -> EasyAFK.manager.kickPlayer(player));
        }
      } else if (!EasyAFK.manager.isAFK(player.getUniqueId())) {
        EasyAFK.manager.enableAFK(player, AFKMode.SOFT);
      }
    }
  }

  private boolean canBypass(final Player p) {
    return EasyAFK.config.bypassAfkEnabled && p.hasPermission("easyafk.bypass.afk") ||
        EasyAFK.config.ignoredWorlds.contains(p.getWorld().getName()) ||
        Hooks.predicateHook("worldguard", (WorldGuardHook hook) -> hook.isInAfkBypassSection(p));
  }
}
