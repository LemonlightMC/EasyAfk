package com.julizey.easyafk.utils;

import java.util.HashSet;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ParticleContainer {

  public static ParticleContainer fromConfig(
    final ConfigurationSection section
  ) {
    return new ParticleContainer(
      section.getString("type"),
      section.getInt("amount", 20),
      section.getDouble("offsetX", 1.0D),
      section.getDouble("offsetY", 1.0D),
      section.getDouble("offsetZ", 1.0D),
      section.getDouble("extra", 0.1D)
    );
  }

  public static HashSet<ParticleContainer> fromMultiple(
    final ConfigurationSection section
  ) {
    final HashSet<ParticleContainer> containers = new HashSet<ParticleContainer>();
    if (section == null) return containers;
    for (String key : section.getKeys(false)) {
      key += ".";
      containers.add(
        new ParticleContainer(
          section.getString(key + "type"),
          section.getInt(key + "amount", 20),
          section.getDouble(key + "offsetX", 1.0D),
          section.getDouble(key + "offsetY", 1.0D),
          section.getDouble(key + "offsetZ", 1.0D),
          section.getDouble(key + "extra", 0.1D)
        )
      );
    }
    return containers;
  }

  private final Particle type;
  private final int amount;
  private final double offsetX;
  private final double offsetY;

  private final double offsetZ;

  private final double extra;

  public ParticleContainer(
    final String type,
    final int amount,
    final double offsetX,
    final double offsetY,
    final double offsetZ,
    final double extra
  ) {
    if (type == null) {
      Text.warn("Invalid particle type: " + type);
      this.type = null;
    } else {
      this.type = Particle.valueOf(type);
    }
    if (amount < 0) {
      Text.warn("Amount must be non-negative");
      this.amount = 0;
    } else {
      this.amount = amount;
    }
    this.offsetX = offsetX;
    this.offsetY = offsetY;
    this.offsetZ = offsetZ;
    this.extra = extra;
  }

  public void play(final Player player) {
    if (type == null) return;
    player
      .getWorld()
      .spawnParticle(
        type,
        player.getLocation(),
        amount,
        offsetX,
        offsetY,
        offsetZ,
        extra
      );
  }
}
