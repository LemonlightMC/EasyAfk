package com.julizey.easyafk.state;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.database.DatabaseManager;
import com.julizey.easyafk.utils.ParticleContainer;
import com.julizey.easyafk.utils.SoundContainer;
import com.julizey.easyafk.utils.Text;
import com.julizey.easyafk.utils.Text.Replaceable;
import java.util.HashSet;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class AfkState {

  public HashSet<UUID> afkPlayers = new HashSet<UUID>();

  public void toggle(final Player player) {
    if (afkPlayers.contains(player.getUniqueId())) {
      disableAFK(player);
    } else {
      enableAFK(player);
    }
  }

  public void enableAFK(final Player player) {
    Text.send(player, "messages.afk");

    if (EasyAFK.config.afkBroadcastEnabled) {
      final String msg = Text.format(
        "messages.afk-broadcast",
        true,
        true,
        new Replaceable("%player%", player.getDisplayName())
      );
      for (final Player p : Bukkit.getOnlinePlayers()) {
        p.sendMessage(msg);
      }
    }

    if (EasyAFK.config.afkTitleEnabled) {
      player.sendTitle(
        EasyAFK.config.afkTitle,
        EasyAFK.config.afkSubtitle,
        10,
        70,
        20
      );
    }

    for (final ParticleContainer particle : EasyAFK.config.particleEnableContainers) {
      particle.play(player);
    }
    for (final SoundContainer sound : EasyAFK.config.soundEnableContainers) {
      sound.play(player);
    }

    final UUID playerId = player.getUniqueId();
    afkPlayers.add(playerId);
    player.setMetadata(
      "afk",
      new FixedMetadataValue(EasyAFK.instance, Boolean.TRUE)
    );
    if (!DatabaseManager.containsAfkPlayer(playerId)) {
      DatabaseManager.addAfkPlayer(playerId);
    }
  }

  public void disableAFK(final Player player) {
    Text.send(player, "messages.unafk");
    if (EasyAFK.config.unafkBroadcastEnabled) {
      final String msg = Text.format(
        "messages.unafk-broadcast",
        true,
        true,
        new Replaceable("%player%", player.getName())
      );
      for (final Player p : Bukkit.getOnlinePlayers()) {
        p.sendMessage(msg);
      }
    }

    if (EasyAFK.config.unafkTitleEnabled) {
      player.sendTitle(
        EasyAFK.config.unafkTitle,
        EasyAFK.config.unafkSubtitle,
        10,
        70,
        20
      );
    }

    for (final ParticleContainer particle : EasyAFK.config.particleDisableContainers) {
      particle.play(player);
    }
    for (final SoundContainer sound : EasyAFK.config.soundDisableContainers) {
      sound.play(player);
    }

    final UUID playerId = player.getUniqueId();
    afkPlayers.remove(playerId);
    player.setMetadata(
      "afk",
      new FixedMetadataValue(EasyAFK.instance, Boolean.FALSE)
    );

    if (DatabaseManager.containsAfkPlayer(playerId)) {
      DatabaseManager.removeAfkPlayer(playerId);
    }
  }

  public boolean isAfk(final Player player) {
    return afkPlayers.contains(player.getUniqueId());
  }
}
