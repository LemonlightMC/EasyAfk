package com.julizey.easyafk;

import com.julizey.easyafk.database.DatabaseManager;
import com.julizey.easyafk.event.MoveListener;
import com.julizey.easyafk.event.PlayerQuitListener;
import com.julizey.easyafk.gui.AfkPlayerActionsGUI;
import com.julizey.easyafk.gui.AfkPlayerOverviewGUI;
import com.julizey.easyafk.hooks.TabIntegration;
import com.julizey.easyafk.hooks.WorldGuardIntegration;
import com.julizey.easyafk.state.AfkState;
import com.julizey.easyafk.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyAFK extends JavaPlugin {

  public static EasyAFK instance;
  public static Config config;

  public TabIntegration tabIntegration = null;
  public WorldGuardIntegration worldGuardIntegration = null;
  public AfkPlayerOverviewGUI afkPlayerOverviewGUI;
  public AfkPlayerActionsGUI afkPlayerActionsGUI;
  public AfkCheckTask afkChecker;
  public AfkState afkState;

  public void onLoad() {
    instance = this;
    saveDefaultConfig();
    config = new Config(instance.getConfig());
    reload(false);
  }

  public void onEnable() {
    afkState = new AfkState();
    afkChecker = new AfkCheckTask();
    afkPlayerOverviewGUI = new AfkPlayerOverviewGUI();
    afkPlayerActionsGUI = new AfkPlayerActionsGUI();

    getServer().getPluginManager().registerEvents(new MoveListener(), this);
    getServer()
      .getPluginManager()
      .registerEvents(new PlayerQuitListener(), this);
    getServer().getPluginManager().registerEvents(afkPlayerOverviewGUI, this);
    getServer().getPluginManager().registerEvents(afkPlayerActionsGUI, this);
    EasyAFKCommand afkCommand = new EasyAFKCommand();

    getCommand("afk").setExecutor(afkCommand);
    getCommand("afk").setTabCompleter(afkCommand);
    Bukkit
      .getScheduler()
      .runTaskAsynchronously(
        EasyAFK.instance,
        () -> {
          if (Bukkit.getPluginManager().isPluginEnabled("TAB")) {
            tabIntegration = new TabIntegration();
          }
          if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
            worldGuardIntegration = new WorldGuardIntegration();
          }
          DatabaseManager.setup();
        }
      );

    afkChecker.runTaskTimer(this, 20L, 20L);
  }

  public void onDisable() {
    if (afkChecker != null) afkChecker.cancel();
    if (tabIntegration != null) {
      tabIntegration.unload();
      tabIntegration = null;
    }
    if (worldGuardIntegration != null) {
      worldGuardIntegration.unload();
      worldGuardIntegration = null;
    }
    DatabaseManager.close();
  }

  public void reload(boolean full) {
    try {
      instance.saveDefaultConfig();
      instance.reloadConfig();
      Text.reload();
      config.reload(getConfig());
      DatabaseManager.reload();

      if (full) {
        if (!DatabaseManager.isConnected()) {
          DatabaseManager.setup();
        }
        if (tabIntegration != null) {
          tabIntegration.reload();
        }
        if (worldGuardIntegration != null) {
          worldGuardIntegration.reload();
        }
      }
    } catch (Exception ex) {
      Text.warn("Failed to reload the configs!");
      ex.printStackTrace();
    }
  }
}
