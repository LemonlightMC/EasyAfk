package com.julizey.easyafk.utils;

import org.bukkit.entity.Player;

public class Location {

  public double x;
  public double y;
  public double z;
  public float yaw;
  public float pitch;

  public Location(final Player player) {
    final org.bukkit.Location location = player.getLocation();
    this.x = location.getX();
    this.y = location.getY();
    this.z = location.getZ();
    this.yaw = location.getYaw();
    this.pitch = location.getPitch();
  }

  public Location(final double x, final double y, final double z, final float yaw, final float pitch) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.yaw = yaw;
    this.pitch = pitch;
  }

  public Location clone() {
    return new Location(x, y, z, yaw, pitch);
  }
}
