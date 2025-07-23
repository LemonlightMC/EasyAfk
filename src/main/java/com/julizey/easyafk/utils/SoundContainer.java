package com.julizey.easyafk.utils;

import java.util.HashSet;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class SoundContainer {

  public static SoundContainer fromConfig(final ConfigurationSection section) {
    return new SoundContainer(
      section.getString("type"),
      (float) section.getDouble("pitch", 1.0D),
      (float) section.getDouble("volume", 1.0D)
    );
  }

  public static HashSet<SoundContainer> fromMultiple(
    final ConfigurationSection section
  ) {
    final HashSet<SoundContainer> containers = new HashSet<SoundContainer>();
    if (section == null) return containers;
    for (String key : section.getKeys(false)) {
      key += ".";
      containers.add(
        new SoundContainer(
          section.getString(key + "type"),
          (float) section.getDouble(key + "pitch", 1.0D),
          (float) section.getDouble(key + "volume", 1.0D)
        )
      );
    }
    return containers;
  }

  private final Sound type;

  private final float pitch;

  private final float volume;

  @SuppressWarnings("deprecation")
  public SoundContainer(
    final String type,
    final float pitch,
    final float volume
  ) {
    if (type == null) {
      Text.warn("Invalid sound type: " + type);
      this.type = null;
    } else {
      this.type = Sound.valueOf(type);
    }
    if (pitch < 0.0F || pitch > 2.0F) {
      Text.warn("Pitch must be between 0.0 and 2.0");
      this.pitch = 1.0F;
    } else {
      this.pitch = pitch;
    }
    if (volume < 0.0F || volume > 1.0F) {
      Text.warn("Volume must be between 0.0 and 1.0");
      this.volume = 1.0F;
    } else {
      this.volume = volume;
    }
  }

  public void play(final Player player) {
    if (type == null) return;
    player.playSound(player.getLocation(), type, volume, pitch);
  }
}
