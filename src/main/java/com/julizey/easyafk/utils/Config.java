package com.julizey.easyafk.utils;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.api.AFKState.AFKMode;
import com.julizey.easyafk.hooks.Hooks;

import java.nio.file.Path;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

  public FileConfiguration configFile;

  public long command_cooldown;

  // afk settings
  public List<String> ignoredWorlds;
  public boolean bypassAfkEnabled;
  public boolean bypassKickEnabled;

  public long afkTimeout;
  public long kickTimeout;
  public boolean kickEnabled;
  public boolean kickEnabledWhenFull;

  public boolean disableOnMove;
  public boolean disableOnLeave;
  public boolean disableOnKick;

  // afk checker
  public int interval;
  public int agressiveInterval;
  public boolean checkerEnabled;
  public boolean aggressiveCheckerEnabled;
  public int checkerTickSpread;

  public boolean antiEnabled;
  public boolean antiVehicle;
  public boolean antiWaterFlow;
  public boolean antiBlockBreak;
  public boolean antiBlockPlace;
  public boolean antiMicro;
  public double antiMicroMoveDistance;
  public double antiMicroRotationDistance;
  public double antiMicroJumpDistance;

  // afk/unafk
  public AFKModeConfig afkSoft;
  public AFKModeConfig afkHard;
  public AFKModeConfig afkFake;
  public AFKModeConfig unafkSoft;
  public AFKModeConfig unafkHard;
  public AFKModeConfig unafkFake;

  public String afkTitle;
  public String afkSubtitle;
  public String unafkSubtitle;
  public String unafkTitle;

  // integrations
  public String discordAvatarURL;
  public String discordChannel;
  public boolean discordAFKTime;

  // database
  public String databaseType;
  public boolean clearOnReload = false;
  public String dbPath;
  public String host;
  public String database;
  public String username;
  public String password;
  public int port;

  public static class AFKModeConfig {

    public final boolean disableOnMove;
    public final boolean disableOnPlace;
    public final boolean disableOnBreak;
    public final boolean disableOnInteract;
    public final boolean disableOnDamage;
    public final boolean effects;
    public final boolean message;
    public final boolean broadcast;
    public final boolean title;
    public final boolean subTitle;
    public final boolean discord;
    public final boolean supportsDisable;

    public AFKModeConfig(final ConfigurationSection section, final boolean supportsDisable) {
      disableOnMove = section.getBoolean("disableOnMove", false);
      disableOnPlace = section.getBoolean("disableOnPlace", false);
      disableOnBreak = section.getBoolean("disableOnBreak", false);
      disableOnInteract = section.getBoolean("disableOnInteract", false);
      disableOnDamage = section.getBoolean("disableOnDamage", false);
      effects = section.getBoolean("effects", true);
      broadcast = section.getBoolean("broadcast", false);
      message = section.getBoolean("message", true);
      title = section.getBoolean("title", true);
      subTitle = section.getBoolean("subTitle", true);
      discord = section.getBoolean("discord", true);
      this.supportsDisable = supportsDisable;
    }

  }

  public Config(final FileConfiguration config) {
    reload(config);
  }

  public void reload(final FileConfiguration config) {
    this.configFile = config;
    command_cooldown = configFile.getLong("cooldown", 3) * 1000;

    // afk settings
    EasyAFK.manager.setAfkTime(configFile.getLong("afk.timeout", 1200) * 1000L);
    bypassAfkEnabled = configFile.getBoolean("bypass-afk", true);
    bypassKickEnabled = configFile.getBoolean("bypass-kick", true);
    ignoredWorlds = configFile.getStringList("checker.ignored-worlds");

    disableOnMove = configFile.getBoolean("disable-on-move", true);
    disableOnLeave = configFile.getBoolean("disable-on-leave", true);
    disableOnKick = configFile.getBoolean("disable-on-kick", false);

    EasyAFK.manager.setKickTime(configFile.getLong("kick.timeout") * 1000L);
    kickEnabled = configFile.getBoolean("kick.enabled", true);
    kickEnabledWhenFull = configFile.getBoolean("kick.enabledWhenFull", true);

    // afk checker
    checkerEnabled = configFile.getBoolean("checker.enabled", true);
    interval = configFile.getInt("checker.interval", 20);
    aggressiveCheckerEnabled = configFile.getBoolean("checker.aggressive-enabled", true);
    agressiveInterval = configFile.getInt("checker.aggressive-interval", 2000);
    checkerTickSpread = configFile.getInt("checker.tickSpread", 4);

    antiEnabled = configFile.getBoolean("checker.anti.enabled", false);
    antiVehicle = configFile.getBoolean("checker.anti.infinite-vehicle", false);
    antiWaterFlow = configFile.getBoolean("checker.anti.water-flow", false);
    antiBlockBreak = configFile.getBoolean("checker.anti.block-break", false);
    antiBlockPlace = configFile.getBoolean("checker.anti.block-place", false);

    antiMicro = configFile.getBoolean("checker.anti.micro", false);
    antiMicroMoveDistance = configFile.getDouble("checker.anti.microMoveDistance", 0.2d);
    antiMicroRotationDistance = configFile.getDouble("checker.anti.microRotationDistance", 10.0d);
    antiMicroJumpDistance = configFile.getDouble("checker.anti.microJumpDistance", 0.4d);

    // afk effects
    afkSoft = new AFKModeConfig(configFile.getConfigurationSection("afk.soft"), true);
    afkHard = new AFKModeConfig(configFile.getConfigurationSection("afk.hard"), true);
    afkFake = new AFKModeConfig(configFile.getConfigurationSection("afk.fake"), true);
    unafkSoft = new AFKModeConfig(configFile.getConfigurationSection("unafk.soft"), false);
    unafkHard = new AFKModeConfig(configFile.getConfigurationSection("unafk.hard"), false);
    unafkFake = new AFKModeConfig(configFile.getConfigurationSection("unafk.fake"), false);

    afkTitle = Text.format("messages.afk-title", true, false);
    afkSubtitle = Text.format("messages.afk-subtitle", true, false);
    unafkTitle = Text.format("messages.unafk-title", true, false);
    unafkSubtitle = Text.format("messages.unafk-subtitle", true, false);

    // integrations
    if (configFile.getBoolean("integration.worldguard", true)) {
      Hooks.enable("worldguard");
    }
    if (configFile.getBoolean("integration.tab.enabled", true)) {
      Hooks.enable("tab");
    }
    EasyAFK.manager.setTabPrefix(
        configFile.getString("integration.tab.prefix", "&c[AFK]"));
    if (configFile.getBoolean("integration.discordsrv.enabled", true)) {
      Hooks.enable("discordsrv");
    }
    discordChannel = configFile.getString("integration.discordsrv.channelId");
    discordAvatarURL = configFile.getString("integration.discordsrv.avatarURL");
    discordAFKTime = configFile.getBoolean("integration.discordsrv.withTime", true);

    // database
    clearOnReload = configFile.getBoolean("clear-on-reload", true);
    databaseType = configFile.getString("database", "sqlite");

    if (databaseType.equals("sqlite")) {
      database = configFile.getString("sqlite.database", "easyafk");
      database = Path
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
          "Invalid database type: " + databaseType);
    }
  }

  public AFKModeConfig getModeConfig(final boolean enablingAFK, final AFKMode mode) {
    if (enablingAFK) {
      switch (mode) {
        case AFKMode.SOFT:
          return afkSoft;
        case AFKMode.HARD:
          return afkHard;
        case AFKMode.FAKE:
          return afkFake;
      }
    } else {
      switch (mode) {
        case AFKMode.SOFT:
          return unafkSoft;
        case AFKMode.HARD:
          return unafkHard;
        case AFKMode.FAKE:
          return unafkFake;
      }
    }
    return null;
  }
}
