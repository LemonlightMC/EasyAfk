package com.julizey.easyafk.hooks;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.hooks.Hooks.Hook;
import com.julizey.easyafk.utils.Text;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class DiscordSRVHook extends Hook {

  private static TextChannel channel;

  public DiscordSRVHook() {
    super("discordsrv");
  }

  public void load() {
    reload();
  }

  public void unload() {
    channel = null;
  }

  public void reload() {
    if (!isEnabled) {
      channel = null;
      return;
    }
    channel = DiscordSRV.getPlugin().getMainGuild().getTextChannelById(EasyAFK.manager.channelId);
    if (channel == null) {
      Text.warn("Failed to connect to Text Channel: " + channel);
    }
  }

  @EventHandler
  public void send(Player p, boolean isAFK) {
    if (!isEnabled) {
      return;
    }
    String message;
    if (isAFK) {
      message = ":zzz: **" + p.getName() + "** is now AFK.";
    } else {
      message = ":wave: **" + p.getName() + "** is no longer AFK.";
    }
    channel.sendMessage(message).queue();
  }

  public static void create() {
    new DiscordSRVHook();
  }
}