package com.julizey.easyafk.hooks;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.hooks.Hooks.Hook;
import com.julizey.easyafk.utils.Text;
import com.julizey.easyafk.utils.Text.Replaceable;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.EmbedBuilder;
import github.scarsz.discordsrv.dependencies.jda.api.entities.TextChannel;
import java.awt.Color;
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
  public void sendMessage(Player p, boolean isAFK) {
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

  public void sendEmbed(Player player, boolean isStartingAFK, long timeAFK) {
    String startMessage = Text.format("message.discord-afkMessage", true, false,
        new Replaceable("%player%", player.getName()));
    String stopMessage = Text.format("message.discord-unafkMessage", true, false,
        new Replaceable("%player%", player.getName()));
    String timeMessage = Text.format("message.discord-afkTimeMessage", true, false,
        new Replaceable("%player%", player.getName()));
    String title = isStartingAFK ? startMessage : stopMessage;
    String avatarURL = EasyAFK.config.discordAvatarURL.replace("%uuid%",
        player.getUniqueId().toString().replace("-", ""));
    EmbedBuilder embedBuilder = new EmbedBuilder()
        .setAuthor(title, null, avatarURL)
        .setColor(isStartingAFK ? Color.GREEN : Color.RED);
    if (!isStartingAFK && EasyAFK.config.discordAFKTime) {
      embedBuilder.addField(timeMessage, String.valueOf(timeAFK), true);
    }
    channel.sendMessageEmbeds(embedBuilder.build()).queue();
  }

  public static void create() {
    new DiscordSRVHook();
  }
}