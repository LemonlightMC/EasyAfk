package com.julizey.easyafk.event;

import com.julizey.easyafk.EasyAFK;
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
  private final Map<UUID, Integer> repeatMoveCount = new HashMap<>();

  public MoveListener() {
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    Bukkit
        .getScheduler()
        .runTaskAsynchronously(EasyAFK.instance, () -> handleMoveEvent(event));
  }

  private void handleMoveEvent(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    UUID playerId = player.getUniqueId();

    if (performsAntiChecks(player)) {
      return;
    }

    DatabaseManager.updateLastActive(playerId, System.currentTimeMillis());
    if (!EasyAFK.config.disableOnMove ||
        !EasyAFK.instance.afkState.afkPlayers.contains(playerId)) {
      return;
    }
    EasyAFK.instance.afkState.disableAFK(player);
  }

  public boolean performsAntiChecks(Player player) {
    if (EasyAFK.config.antiVehicle && player.isInsideVehicle()) {
      return true;
    }
    if (EasyAFK.config.antiWaterFlow && isInInfiniteWaterFlow(player)) {
      return true;
    }

    UUID playerId = player.getUniqueId();
    Location lastLoc = lastLocations.get(playerId);
    Location currLoc = new Location(player);

    if (EasyAFK.config.antiMicroMove) {
      double dx = Math.abs(lastLoc.x - currLoc.x);
      double dz = Math.abs(lastLoc.z - currLoc.z);
      if (dx < EasyAFK.config.antiMicroMoveDistance &&
          dz < EasyAFK.config.antiMicroMoveDistance) {
        lastLocations.put(playerId, currLoc.clone());
        return true;
      }
    }

    if (EasyAFK.config.antiRotationOnly) {
      double dYaw = Math.abs(lastLoc.yaw - currLoc.yaw);
      double dPitch = Math.abs(lastLoc.pitch - currLoc.pitch);
      if (dYaw < EasyAFK.config.antiRotationDistance &&
          dPitch < EasyAFK.config.antiRotationDistance) {
        lastLocations.put(playerId, currLoc.clone());
        return true;
      }
    }

    if (EasyAFK.config.antiJump) {
      double dy = Math.abs(lastLoc.y - currLoc.y);
      if (dy < EasyAFK.config.antiJumpDistance) {
        lastLocations.put(playerId, currLoc.clone());
        return true;
      }
    }

    if (EasyAFK.config.antiMacroPattern) {
      int count = repeatMoveCount.getOrDefault(playerId, 0);
      double dx = Math.abs(lastLoc.x - currLoc.x);
      double dz = Math.abs(lastLoc.z - currLoc.z);
      if (dx < EasyAFK.config.antiMicroMoveDistance &&
          dz < EasyAFK.config.antiMicroMoveDistance) {
        count++;
      } else {
        count = 0;
      }
      repeatMoveCount.put(playerId, count);
      if (count > 10) {
        lastLocations.put(playerId, currLoc.clone());
        return true;
      }
    }
    lastLocations.put(playerId, currLoc.clone());
    return false;
  }

  private boolean isInInfiniteWaterFlow(Player player) {
    Block block = player.getLocation().getBlock();
    BlockFace[] faces = {
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.SOUTH,
        BlockFace.WEST,
    };
    for (BlockFace face : faces) {
      Block adjacentBlock = block.getRelative(face);
      if (adjacentBlock.getType() == Material.WATER &&
          adjacentBlock.getRelative(face).getType() == Material.WATER) {
        return true;
      }
    }
    return false;
  }
}
