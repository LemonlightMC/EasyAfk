package com.julizey.easyafk.listener;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.api.AFKStopEvent.AFKStopReason;
import com.julizey.easyafk.database.DatabaseManager;
import com.julizey.easyafk.utils.Location;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MoveListener implements Listener {

  private final Map<UUID, Location> lastLocations = new HashMap<>();

  public MoveListener() {
  }

  @EventHandler
  public void onMove(final PlayerMoveEvent event) {
    Bukkit
        .getScheduler()
        .runTaskAsynchronously(EasyAFK.instance, () -> handleMoveEvent(event));
  }

  private void handleMoveEvent(final PlayerMoveEvent event) {
    final Player player = event.getPlayer();
    final UUID playerId = player.getUniqueId();

    if (performsAntiChecks(player)) {
      return;
    }

    DatabaseManager.updateLastActive(playerId, System.currentTimeMillis());
    if (!EasyAFK.config.disableOnMove ||
        !EasyAFK.manager.isAFK(playerId)) {
      return;
    }
    EasyAFK.manager.disableAFK(player, AFKStopReason.MOVED);
  }

  public boolean performsAntiChecks(final Player player) {
    if (EasyAFK.config.antiVehicle && player.isInsideVehicle()) {
      return true;
    }
    if (EasyAFK.config.antiWaterFlow && isInInfiniteWaterFlow(player)) {
      return true;
    }

    final UUID playerId = player.getUniqueId();
    final Location lastLoc = lastLocations.get(playerId);
    final Location currLoc = new Location(player);

    if (EasyAFK.config.antiMicroMove) {
      final double dx = Math.abs(lastLoc.x - currLoc.x);
      final double dy = Math.abs(lastLoc.y - currLoc.y);
      final double dz = Math.abs(lastLoc.z - currLoc.z);
      if (dx < EasyAFK.config.antiMicroMoveDistance &&
          dy < EasyAFK.config.antiMicroMoveDistance &&
          dz < EasyAFK.config.antiMicroMoveDistance) {
        lastLocations.put(playerId, currLoc.clone());
        return true;
      }
    }

    if (EasyAFK.config.antiRotationOnly) {
      final double dYaw = Math.abs(lastLoc.yaw - currLoc.yaw);
      final double dPitch = Math.abs(lastLoc.pitch - currLoc.pitch);
      if (dYaw < EasyAFK.config.antiRotationDistance &&
          dPitch < EasyAFK.config.antiRotationDistance) {
        lastLocations.put(playerId, currLoc.clone());
        return true;
      }
    }

    if (EasyAFK.config.antiJump) {
      final double dy = Math.abs(lastLoc.y - currLoc.y);
      if (dy < EasyAFK.config.antiJumpDistance) {
        lastLocations.put(playerId, currLoc.clone());
        return true;
      }
    }

    lastLocations.put(playerId, currLoc.clone());
    return false;
  }

  private boolean isInInfiniteWaterFlow(final Player player) {
    final Block block = player.getLocation().getBlock();
    final BlockFace[] faces = {
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.SOUTH,
        BlockFace.WEST,
    };
    for (final BlockFace face : faces) {
      final Block adjacentBlock = block.getRelative(face);
      if (adjacentBlock.getType() == Material.WATER &&
          adjacentBlock.getRelative(face).getType() == Material.WATER) {
        return true;
      }
    }
    return false;
  }
}
