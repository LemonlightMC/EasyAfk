package com.julizey.easyafk.utils;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.hooks.Hooks;

import java.nio.file.Path;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {

  public FileConfiguration configFile;

  public long command_cooldown;
  // afk check task
  public long interval;
  public long kickTimeout;
  public boolean kickEnabled;
  public boolean kickEnabledWhenFull;
  public long afkTimeout;
  public boolean bypassAfkEnabled;
  public boolean bypassKickEnabled;
  public List<String> ignoredWorlds;

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

  // listener
  public boolean antiVehicle;
  public boolean antiWaterFlow;
  public boolean antiBlockBreak;
  public boolean antiBlockPlace;
  public boolean antiMicroMove;
  public double antiMicroMoveDistance;
  public boolean antiRotationOnly;
  public double antiRotationDistance;
  public boolean antiJump;
  public double antiJumpDistance;

  public boolean disableOnMove;
  public boolean disableOnLeave;
  public boolean disableOnKick;

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

    // afk check task
    interval = configFile.getLong("checker.interval", 1) * 20L;
    EasyAFK.manager.setKickTime(configFile.getLong("kick.timeout") * 1000L);
    kickEnabled = configFile.getBoolean("kick.enabled", true);
    kickEnabledWhenFull = configFile.getBoolean("kick.enabledWhenFull", true);
    EasyAFK.manager.setAfkTime(configFile.getLong("afk.timeout", 1200) * 1000L);
    bypassAfkEnabled = configFile.getBoolean("bypass-afk", true);
    bypassKickEnabled = configFile.getBoolean("bypass-kick", true);
    ignoredWorlds = configFile.getStringList("ignored-worlds");

    disableOnMove = configFile.getBoolean("disable-on-move", true);
    disableOnLeave = configFile.getBoolean("disable-on-leave", true);
    disableOnKick = configFile.getBoolean("disable-on-kick", false);

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

    // anti
    antiVehicle = configFile.getBoolean("anti.infinite-vehicle", false);
    antiWaterFlow = configFile.getBoolean("anti.water-flow", false);

    antiBlockBreak = configFile.getBoolean("anti.block-break", false);
    antiBlockPlace = configFile.getBoolean("anti.block-place", false);

    antiMicroMove = configFile.getBoolean("anti.microMove", false);
    antiMicroMoveDistance = configFile.getDouble("anti.microMoveDistance", 0.3F);
    antiRotationOnly = configFile.getBoolean("anti.rotationOnly", false);
    antiRotationDistance = configFile.getDouble("anti.rotationDistance", 10.0F);
    antiJump = configFile.getBoolean("anti.jump", false);
    antiJumpDistance = configFile.getDouble("anti.jumpDistance", 0.3F);

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
