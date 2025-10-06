package com.julizey.easyafk;

import com.julizey.easyafk.api.AFKManager;
import com.julizey.easyafk.database.DatabaseManager;
import com.julizey.easyafk.gui.*;
import com.julizey.easyafk.hooks.*;
import com.julizey.easyafk.listener.*;
import com.julizey.easyafk.utils.AnimationManager;
import com.julizey.easyafk.utils.Config;
import com.julizey.easyafk.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EasyAFK extends JavaPlugin {

  public static EasyAFK instance;
  public static Config config;
  public static AFKManager manager;

  private AfkPlayerOverviewGUI afkPlayerOverviewGUI;
  private AfkPlayerActionsGUI afkPlayerActionsGUI;
  public AnimationManager animationManager;
  public AfkCheckTask afkChecker;

  public void onLoad() {
    instance = this;
    saveDefaultConfig();
    reloadConfig();
    config = new Config(instance.getConfig());
    Text.reload();
  }

  public void onEnable() {
    manager = new AFKManager();
    afkChecker = new AfkCheckTask();

    // Events
    getServer().getPluginManager().registerEvents(new MoveListener(), this);
    getServer()
        .getPluginManager()
        .registerEvents(new EventListener(), this);

    // Command
    final EasyAFKCommand afkCommand = new EasyAFKCommand();
    getCommand("afk").setExecutor(afkCommand);
    getCommand("afk").setTabCompleter(afkCommand);

    // Hooks
    Bukkit
        .getScheduler()
        .runTaskAsynchronously(
            EasyAFK.instance,
            () -> {
              DatabaseManager.reload(true);
              animationManager = new AnimationManager(config.configFile, "effects");
              if (Bukkit.getPluginManager().isPluginEnabled("TAB")) {
                Hooks.createHook(TabHook.class);
              }
              if (Bukkit.getPluginManager().isPluginEnabled("WorldGuard")) {
                Hooks.createHook(WorldGuardHook.class);
              }
              if (Bukkit.getPluginManager().isPluginEnabled("DiscordSRV")) {
                Hooks.createHook(DiscordSRVHook.class);
              }
              Hooks.load();
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
    Hooks.unload();
    Hooks.clear();
    DatabaseManager.close();
  }

  public void reload() {
    try {
      saveDefaultConfig();
      reloadConfig();
      Text.reload();
      config.reload(getConfig());
      animationManager.reload();
      DatabaseManager.reload(true);
      Hooks.reload();
    } catch (final Exception ex) {
      Text.warn("Failed to reload the configs!");
      ex.printStackTrace();
    }
  }

  public void openOverviewGUI(final Player p) {
    if (afkPlayerOverviewGUI == null) {
      afkPlayerOverviewGUI = new AfkPlayerOverviewGUI();
      getServer().getPluginManager().registerEvents(afkPlayerOverviewGUI, this);
    }
    afkPlayerOverviewGUI.openGUI(p, 1);
  }

  public void openActionGUI(final Player p, final Player target) {
    if (afkPlayerActionsGUI == null) {
      afkPlayerActionsGUI = new AfkPlayerActionsGUI();
      getServer().getPluginManager().registerEvents(afkPlayerActionsGUI, this);
    }
    afkPlayerActionsGUI.openGUI(p, target);
  }
}
