package com.julizey.easyafk.hooks;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.utils.Text;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.placeholder.PlaceholderManager;

public class TabIntegration {

  private static PlaceholderManager placeholderManager = null;

  public TabIntegration() {
    reload();
    Text.info("TAB integration has been enabled");
  }

  public void reload() {
    if (!EasyAFK.config.tabEnabled) {
      unload();
      return;
    }

    placeholderManager = TabAPI.getInstance().getPlaceholderManager();
    if (placeholderManager != null) {
      placeholderManager.registerPlayerPlaceholder(
        "%afk%",
        100,
        player ->
          EasyAFK.instance.afkState.afkPlayers.contains(player.getUniqueId())
            ? EasyAFK.config.tabPrefix
            : ""
      );
    }
  }

  public void unload() {
    if (placeholderManager != null) {
      placeholderManager.unregisterPlaceholder("%afk%");
    }
    Text.info("TAB integration has been disabled");
  }
}
