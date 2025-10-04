package com.julizey.easyafk;

import com.julizey.easyafk.database.DatabaseManager;
import com.julizey.easyafk.event.MoveListener;
import com.julizey.easyafk.event.PlayerQuitListener;
import com.julizey.easyafk.gui.AfkPlayerActionsGUI;
import com.julizey.easyafk.gui.AfkPlayerOverviewGUI;
import com.julizey.easyafk.hooks.TabIntegration;
import com.julizey.easyafk.hooks.WorldGuardIntegration;
import com.julizey.easyafk.state.AfkState;
import com.julizey.easyafk.utils.AnimationManager;
import com.julizey.easyafk.utils.Config;
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
  public AnimationManager animationManager;
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
    animationManager = new AnimationManager(config.configFile, "effects");
    afkPlayerOverviewGUI = new AfkPlayerOverviewGUI();
    afkPlayerActionsGUI = new AfkPlayerActionsGUI();

    // Events
    getServer().getPluginManager().registerEvents(new MoveListener(), this);
    getServer()
        .getPluginManager()
        .registerEvents(new PlayerQuitListener(), this);
    getServer().getPluginManager().registerEvents(afkPlayerOverviewGUI, this);
    getServer().getPluginManager().registerEvents(afkPlayerActionsGUI, this);

    // Command
    EasyAFKCommand afkCommand = new EasyAFKCommand();
    getCommand("afk").setExecutor(afkCommand);
    getCommand("afk").setTabCompleter(afkCommand);

    // Hooks
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
            });

    afkChecker.runTaskTimerAsynchronously(
        this,
        config.interval,
        config.interval);
  }

  public void onDisable() {
    if (afkChecker != null) {
      afkChecker.cancel();
    }
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
      saveDefaultConfig();
      reloadConfig();
      Text.reload();
      config.reload(getConfig());
      animationManager.reload();
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
