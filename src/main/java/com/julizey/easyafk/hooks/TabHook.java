package com.julizey.easyafk.hooks;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.hooks.Hooks.Hook;

import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.placeholder.PlaceholderManager;

public class TabHook extends Hook {

  private static PlaceholderManager placeholderManager = null;

  public TabHook() {
    super("tab");
  }

  public void reload() {
    if (!isEnabled) {
      unload();
      return;
    }

    placeholderManager = TabAPI.getInstance().getPlaceholderManager();
    if (placeholderManager == null) {
      return;
    }
    placeholderManager.registerPlayerPlaceholder(
        "%afk%",
        100,
        player -> isEnabled && EasyAFK.manager.isAFK(player.getUniqueId())
            ? EasyAFK.manager.tabPrefix
            : "");
  }

  public void unload() {
    if (placeholderManager != null) {
      placeholderManager.unregisterPlaceholder("%afk%");
    }
  }

  @Override
  public void load() {
    reload();
  }
}
