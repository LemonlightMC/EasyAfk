package com.julizey.easyafk.hooks;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.utils.Text;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

public class WorldGuardIntegration {

  public static WorldGuardPlatform platform;
  private StateFlag AFK_BYPASS = new StateFlag("afk-bypass", false);

  public WorldGuardIntegration() {
    reload();
    Text.info("WorldGuard integration has been enabled");
  }

  public boolean isInAfkBypassSection(final Player player) {
    if (platform == null) {
      return false;
    }
    final RegionManager regions = platform
      .getRegionContainer()
      .get(BukkitAdapter.adapt(player.getWorld()));

    if (regions == null) {
      return false;
    }
    final ApplicableRegionSet set = regions.getApplicableRegions(
      BukkitAdapter.asBlockVector(player.getLocation())
    );
    for (final ProtectedRegion region : set) {
      if (
        !region.getFlags().containsKey(AFK_BYPASS) ||
        region.getFlag(AFK_BYPASS) != State.ALLOW
      ) {
        return false;
      }
    }
    return true;
  }

  public void reload() {
    if (!EasyAFK.config.worldGuardEnabled) {
      unload();
      return;
    }
    platform = WorldGuard.getInstance().getPlatform();
    final FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

    try {
      registry.register(AFK_BYPASS);
    } catch (final Exception ex) {
      final Flag<?> existing = registry.get(AFK_BYPASS.getName());
      if (existing instanceof StateFlag) {
        AFK_BYPASS = (StateFlag) existing;
      } else {
        platform = null;
        Text.warn("Could not register WorldGuard flag " + AFK_BYPASS.getName());
        ex.printStackTrace();
      }
    }
  }

  public void unload() {
    platform = null;
    Text.info("WorldGuard integration has been disabled");
  }
}
