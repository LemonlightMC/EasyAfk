package com.julizey.easyafk.utils;

import com.julizey.easyafk.EasyAFK;
import java.io.File;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class AnimationManager {

  public static final Sound DEFAULT_SOUND = Sound.ENTITY_PLAYER_LEVELUP;
  public static final Particle DEFAULT_PARTICLE = Particle.TOTEM_OF_UNDYING;
  private HashMap<String, Animation> animations;
  private String path;
  private YamlConfiguration default_config;

  public AnimationManager(final String filePath) {
    animations = new HashMap<>();
    this.path = "";
    try {
      final File animationsFile = new File(
        EasyAFK.instance.getDataFolder().getAbsolutePath(),
        filePath + ".yml"
      );
      if (!animationsFile.exists()) {
        EasyAFK.instance.saveResource(filePath + ".yml", false);
      }
      default_config = new YamlConfiguration();
      default_config.load(animationsFile);
      reload(default_config);
    } catch (final Exception e) {
      Text.warn("Failed to load animations!");
      e.printStackTrace();
    }
  }

  public AnimationManager(final FileConfiguration config, final String path) {
    animations = new HashMap<>();
    if (path == null || path.length() == 0) return;
    this.path = path + ".";
    reload(config);
  }

  public void reload() {
    reload(default_config);
  }

  public void reload(final FileConfiguration config) {
    animations.clear();
    for (final String key : config.getKeys(false)) {
      final Animation animation = new Animation(
        SoundContainer.fromMultiple(
          config.getConfigurationSection(path + key + ".sounds")
        ),
        ParticleContainer.fromMultiple(
          config.getConfigurationSection(path + key + ".particles")
        )
      );
      animations.put(key, animation);
    }
  }

  public void play(final CommandSender sender, final String key) {
    if (sender == null || !(sender instanceof Player)) {
      return;
    }
    play((Player) sender, key);
  }

  public void play(final Player p, final String key) {
    if (p == null) return;
    final Animation animation = animations.get(key);
    if (animation == null) return;

    for (final SoundContainer sound : animation.sounds) {
      sound.play(p);
    }
    for (final ParticleContainer particle : animation.particles) {
      particle.play(p);
    }
  }

  public void playEveryone(final String key) {
    final Animation animation = animations.get(key);
    if (animation == null) return;

    for (final Player player : Bukkit.getOnlinePlayers()) {
      for (final SoundContainer sound : animation.sounds) {
        if (!sound.everyone) continue;
        sound.play(player);
      }
      for (final ParticleContainer particle : animation.particles) {
        if (!particle.everyone) continue;
        particle.play(player);
      }
    }
  }

  public record Animation(
    SoundContainer[] sounds,
    ParticleContainer[] particles
  ) {}

  public static record SoundContainer(
    Sound type,
    boolean everyone,
    float pitch,
    float volume
  ) {
    public static SoundContainer[] fromMultiple(
      final ConfigurationSection section
    ) {
      if (section == null) return new SoundContainer[0];
      final String[] keys = section.getKeys(false).toArray(new String[0]);
      final SoundContainer[] containers = new SoundContainer[keys.length];

      for (int i = 0; i < keys.length; i++) {
        final String key = keys[i];
        if (section.isString(key)) {
          containers[i] = fromConfig(section.getString(key), false, 1.0f, 1.0f);
        } else if (!section.getBoolean(key + ".enabled", true)) {
          continue;
        } else {
          containers[i] =
            fromConfig(
              section.getString(key + ".type"),
              section.getBoolean(key + ".everyone", false),
              (float) section.getDouble(key + ".pitch", 1.0d),
              (float) section.getDouble(key + ".volume", 1.0d)
            );
        }
      }
      return containers;
    }

    @SuppressWarnings("deprecation")
    public static SoundContainer fromConfig(
      final String type,
      final boolean everyone,
      float pitch,
      float volume
    ) {
      Sound sound = null;
      try {
        sound = Sound.valueOf(type);
      } catch (final Exception e) {
        Text.warn("Invalid sound type: " + type + "! Using Default!");
        sound = DEFAULT_SOUND;
      }
      if (pitch < 0.0F || pitch > 2.0F) {
        Text.warn("Pitch must be between 0.0 and 2.0! Using Default!");
        pitch = 1.0F;
      }
      if (volume < 0.0F || volume > 1.0F) {
        Text.warn("Volume must be between 0.0 and 1.0! Using Default!");
        volume = 1.0F;
      }
      return new SoundContainer(sound, everyone, pitch, volume);
    }

    public void play(final Player player) {
      if (type == null) return;
      player.playSound(player.getLocation(), type, volume, pitch);
    }
  }

  public static record ParticleContainer(
    Particle type,
    boolean everyone,
    int amount,
    double offsetX,
    double offsetY,
    double offsetZ,
    double extra
  ) {
    public static ParticleContainer[] fromMultiple(
      final ConfigurationSection section
    ) {
      if (section == null) return new ParticleContainer[0];
      final String[] keys = section.getKeys(false).toArray(new String[0]);
      final ParticleContainer[] containers = new ParticleContainer[keys.length];

      for (int i = 0; i < keys.length; i++) {
        final String key = keys[i];
        if (section.isString(key)) {
          containers[i] =
            fromConfig(
              section.getString(key),
              false,
              20,
              0.0d,
              1.0d,
              0.0d,
              0.1d
            );
        } else if (!section.getBoolean(key + ".enabled", true)) {
          continue;
        } else {
          containers[i] =
            fromConfig(
              section.getString(key + ".type"),
              section.getBoolean(key + ".everyone", false),
              section.getInt(key + ".amount", 20),
              section.getDouble(key + ".offsetX", 0.0d),
              section.getDouble(key + ".offsetY", 1.0d),
              section.getDouble(key + ".offsetZ", 0.0d),
              section.getDouble(key + ".extra", 0.1d)
            );
        }
      }
      return containers;
    }

    public static ParticleContainer fromConfig(
      final String type,
      final boolean everyone,
      int amount,
      final double offsetX,
      final double offsetY,
      final double offsetZ,
      final double extra
    ) {
      Particle particle = null;
      if (type == null) {
        Text.warn("Invalid particle type: " + type + "! Using Default!");
        particle = DEFAULT_PARTICLE;
      } else {
        particle = Particle.valueOf(type);
      }
      if (amount < 0) {
        Text.warn("Amount must be non-negative! Using Default!");
        amount = 4;
      }
      return new ParticleContainer(
        particle,
        everyone,
        amount,
        offsetX,
        offsetY,
        offsetZ,
        extra
      );
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
}
