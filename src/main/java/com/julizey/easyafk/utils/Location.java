package com.julizey.easyafk.utils;

import org.bukkit.entity.Player;

public class Location {

  public double x;
  public double y;
  public double z;
  public float yaw;
  public float pitch;

  public Location(Player player) {
    org.bukkit.Location location = player.getLocation();
    this.x = location.getX();
    this.y = location.getY();
    this.z = location.getZ();
    this.yaw = location.getYaw();
    this.pitch = location.getPitch();
  }

  public Location(double x, double y, double z, float yaw, float pitch) {
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
