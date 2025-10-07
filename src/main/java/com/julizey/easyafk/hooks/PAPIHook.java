package com.julizey.easyafk.hooks;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Duration;

import org.bukkit.OfflinePlayer;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.hooks.Hooks.Hook;
import com.julizey.easyafk.utils.DurationFormatter;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPIHook extends Hook {

  private PAPIExpansionHook expansionHook;

  public PAPIHook() {
    super("papi");
  }

  public void load() {
    if (expansionHook != null && expansionHook.isRegistered()) {
      return;
    }
    if (expansionHook == null) {
      expansionHook = new PAPIExpansionHook();
    }
    if (!expansionHook.canRegister()) {
      return;
    }
    expansionHook.register();
  }

  public void reload() {
    if (isEnabled) {
      load();
    } else {
      unload();
    }
  }

  public void unload() {
    if (expansionHook != null && expansionHook.isRegistered()) {
      expansionHook.unregister();
      expansionHook = null;
    }
  }

  public static class PAPIExpansionHook extends PlaceholderExpansion {
    @Override
    public boolean persist() {
      return true;
    }

    @Override
    public boolean canRegister() {
      return true;
    }

    @Override
    public String getAuthor() {
      return EasyAFK.instance.getDescription().getAuthors().toString();
    }

    @Override
    public String getIdentifier() {
      return EasyAFK.instance.getName();
    }

    @Override
    public String getVersion() {
      return EasyAFK.instance.getDescription().getVersion();
    }

    @Override
    public String onRequest(final OfflinePlayer offlinePayer, final String identifier) {
      if (identifier.equalsIgnoreCase("afk")) {
        return EasyAFK.manager.isAFK(offlinePayer.getUniqueId()) ? "true" : "false";
      } else if (identifier.equalsIgnoreCase("afk_mode")) {
        return EasyAFK.manager.getPlayers().get(offlinePayer.getUniqueId()).getMode().toString();
      } else if (identifier.equalsIgnoreCase("afk_lastActive_date")) {
        final long time = EasyAFK.manager.getPlayers().get(offlinePayer.getUniqueId()).getLastActive();
        return formatDate(time);
      } else if (identifier.equalsIgnoreCase("afk_lastActive_time")) {
        final long time = EasyAFK.manager.getPlayers().get(offlinePayer.getUniqueId()).getLastActive();
        return formatTime(time);
      } else if (identifier.equalsIgnoreCase("afk_players")) {
        return EasyAFK.manager.getPlayers().size() + "";
      } else if (identifier.equalsIgnoreCase("afk_players")) {
        return EasyAFK.manager.getPlayers().size() + "";
      }
      return null;
    }
  }

  private static String formatDate(final long time) {
    return new SimpleDateFormat("HH:mm:ss").format(new Date(time));
  }

  private static String formatTime(final long time) {
    return DurationFormatter.format(Duration.ofMillis(System.currentTimeMillis() - time), true);
  }
}