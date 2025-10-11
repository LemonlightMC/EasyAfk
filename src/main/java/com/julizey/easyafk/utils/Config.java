package com.julizey.easyafk.utils;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.hooks.Hooks;

import java.nio.file.Path;
import java.util.List;

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
  public String afkTitle;
  public boolean afkTitleEnabled;
  public String afkSubtitle;
  public boolean afkSubTitleEnabled;
  public boolean afkBroadcastEnabled;

  public String unafkTitle;
  public boolean unafkTitleEnabled;
  public String unafkSubtitle;
  public boolean unafkSubTitleEnabled;
  public boolean unafkBroadcastEnabled;

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
    afkTitle = Text.format("messages.afk-title", true, false);
    afkSubtitle = Text.format("messages.afk-subtitle", true, false);
    afkTitleEnabled = configFile.getBoolean("afk.title", true);
    afkSubTitleEnabled = configFile.getBoolean("afk.subtitle", true);
    afkBroadcastEnabled = configFile.getBoolean("afk.broadcast", false);

    unafkTitle = Text.format("messages.unafk-title", true, false);
    unafkSubtitle = Text.format("messages.unafk-subtitle", true, false);
    unafkTitleEnabled = configFile.getBoolean("unafk.title", true);
    unafkSubTitleEnabled = configFile.getBoolean("unafk.subtitle", true);
    unafkBroadcastEnabled = configFile.getBoolean("unafk.broadcast", false);

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
}
