package com.julizey.easyafk;

import com.julizey.easyafk.utils.ParticleContainer;
import com.julizey.easyafk.utils.SoundContainer;
import com.julizey.easyafk.utils.Text;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

  public FileConfiguration configFile;

  public long command_cooldown = 3000;

  // afk check task
  public long kickTimeout;
  public boolean kickEnabled;
  public boolean kickEnabledWhenFull;
  public long afkTimeout;
  public boolean bypassAfkEnabled;
  public boolean bypassKickEnabled;
  public List<String> ignoredWorlds;

  // afk state
  public String afkTitle;
  public String afkSubtitle;
  public String unafkTitle;
  public String unafkSubtitle;
  public boolean afkBroadcastEnabled;
  public boolean afkTitleEnabled;
  public boolean unafkBroadcastEnabled;
  public boolean unafkTitleEnabled;

  // effects
  public HashSet<SoundContainer> soundEnableContainers = new HashSet<SoundContainer>();
  public HashSet<ParticleContainer> particleEnableContainers = new HashSet<ParticleContainer>();
  public HashSet<SoundContainer> soundDisableContainers = new HashSet<SoundContainer>();
  public HashSet<ParticleContainer> particleDisableContainers = new HashSet<ParticleContainer>();

  // listener
  public boolean antiVehicle;
  public boolean antiWaterFlow;
  public boolean antiBlockBreak;
  public boolean antiBlockPlace;
  public boolean antiMicroMove;
  public double antiMicroMoveDistance;
  public boolean antiRotationOnly;
  public double antiRotationDistance;
  public boolean antiMacroPattern;
  public double antiMacroPatternDistance;
  public boolean antiJump;
  public double antiJumpDistance;

  public boolean disableOnMove;
  public boolean disableOnLeave;
  public boolean disableOnKick;

  // integrations
  public boolean worldGuardEnabled;
  public boolean tabEnabled;
  public String tabPrefix;

  // database
  public String databaseType;
  public boolean clearOnReload = false;
  public String dbPath;
  public String host;
  public String database;
  public String username;
  public String password;
  public int port;

  public Config(final FileConfiguration config) {
    reload(config);
  }

  public void reload(final FileConfiguration config) {
    this.configFile = config;
    command_cooldown = configFile.getLong("cooldown", 3) * 1000;

    // afk check task
    kickTimeout = configFile.getLong("kick.timeout") * 1000L;
    kickEnabled = configFile.getBoolean("kick.enabled", true);
    kickEnabledWhenFull = configFile.getBoolean("kick.enabledWhenFull", true);
    afkTimeout = configFile.getLong("afk.timeout", 1200) * 1000L;
    bypassAfkEnabled = configFile.getBoolean("bypass-afk", true);
    bypassKickEnabled = configFile.getBoolean("bypass-kick", true);
    ignoredWorlds = configFile.getStringList("ignored-worlds");

    disableOnMove = configFile.getBoolean("disable-on-move", true);
    disableOnLeave = configFile.getBoolean("disable-on-leave", true);
    disableOnKick = configFile.getBoolean("disable-on-kick", false);

    // afk state
    afkTitle = Text.format("messages.afk-title", true, false);
    afkSubtitle = Text.format("messages.afk-subtitle", true, false);
    unafkTitle = Text.format("messages.unafk-title", true, false);
    unafkSubtitle = Text.format("messages.unafk-subtitle", true, false);
    afkBroadcastEnabled = configFile.getBoolean("afk.broadcast.enabled", false);
    afkTitleEnabled = configFile.getBoolean("afk.title.enabled", true);
    unafkBroadcastEnabled =
      configFile.getBoolean("unafk.broadcast.enabled", false);
    unafkTitleEnabled = configFile.getBoolean("unafk.title.enabled", true);

    // effects
    reloadEffects();

    // integrations
    worldGuardEnabled = configFile.getBoolean("integration.worldguard", true);
    tabEnabled = configFile.getBoolean("integration.tab.enabled", true);
    tabPrefix =
      Text.convertColor(
        configFile.getString("integration.tab.prefix", "&c[AFK]")
      );

    // anti
    antiVehicle = configFile.getBoolean("anti.infinite-vehicle", false);
    antiWaterFlow = configFile.getBoolean("anti.water-flow", false);

    antiBlockBreak = configFile.getBoolean("anti.block-break", false);
    antiBlockPlace = configFile.getBoolean("anti.block-place", false);

    antiMicroMove = configFile.getBoolean("microMove", false);
    antiMicroMoveDistance = configFile.getDouble("microMoveDistance", 0.3F);
    antiRotationOnly = configFile.getBoolean("rotationOnly", false);
    antiRotationDistance = configFile.getDouble("rotationDistance", 10.0F);
    antiMacroPattern = configFile.getBoolean("macroPattern", false);
    antiMacroPatternDistance =
      configFile.getDouble("macroPatternDistance", 0.3F);
    antiJump = configFile.getBoolean("anti.jump", false);
    antiJumpDistance = configFile.getDouble("anti.jumpDistance", 0.3F);

    // database
    clearOnReload = configFile.getBoolean("clear-on-reload", true);
    databaseType = configFile.getString("database", "sqlite");

    if (databaseType.equals("sqlite")) {
      database = configFile.getString("sqlite.database", "easyafk");
      database =
        Path
          .of(EasyAFK.instance.getDataFolder().getAbsolutePath(), database)
          .toAbsolutePath()
          .toString() +
        ".db";
    } else if (databaseType.equals("mysql")) {
      host = configFile.getString("mysql.host", "localhost");
      port = configFile.getInt("mysql.port", 3306);
      database = configFile.getString("mysql.database", "easyafk");
      username = configFile.getString("mysql.username", "root");
      password = configFile.getString("mysql.password", "");
    } else {
      throw new IllegalArgumentException(
        "Invalid database type: " + databaseType
      );
    }
  }

  public void reloadEffects() {
    final boolean withEnableParticles = configFile.getBoolean(
      "afk.animation.enabled.particles",
      true
    );
    final boolean withEnableSounds = configFile.getBoolean(
      "afk.animation.enabled.sounds",
      true
    );
    final boolean withDisableParticles = configFile.getBoolean(
      "unafk.animation.enabled.particles",
      true
    );
    final boolean withDisableSounds = configFile.getBoolean(
      "unafk.animation.enabled.sounds",
      true
    );

    ConfigurationSection section;

    particleEnableContainers.clear();
    if (withEnableParticles) {
      section = configFile.getConfigurationSection("afk.animation.particles");
      particleEnableContainers = ParticleContainer.fromMultiple(section);
    }

    particleDisableContainers.clear();
    if (withDisableParticles) {
      section = configFile.getConfigurationSection("unafk.animation.particles");
      particleDisableContainers = ParticleContainer.fromMultiple(section);
    }

    soundEnableContainers.clear();
    if (withEnableSounds) {
      section = configFile.getConfigurationSection("afk.animation.sounds");
      soundEnableContainers = SoundContainer.fromMultiple(section);
    }

    soundDisableContainers.clear();
    if (withDisableSounds) {
      section = configFile.getConfigurationSection("unafk.animation.sounds");
      soundDisableContainers = SoundContainer.fromMultiple(section);
    }
  }
}
