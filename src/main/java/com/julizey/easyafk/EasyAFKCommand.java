package com.julizey.easyafk;

import com.julizey.easyafk.api.AFKState.AFKMode;
import com.julizey.easyafk.api.AFKStopEvent.AFKStopReason;
import com.julizey.easyafk.utils.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class EasyAFKCommand implements TabExecutor {

  private static HashMap<UUID, Long> cooldowns = new HashMap<>();
  private static HashMap<UUID, String> reasons = new HashMap<>();

  public EasyAFKCommand() {
  }

  public boolean onCommand(
      final CommandSender sender,
      final Command command,
      final String label,
      final String[] args) {
    Bukkit
        .getScheduler()
        .runTaskAsynchronously(
            EasyAFK.instance,
            new Runnable() {
              @Override
              public void run() {
                handleCommand(sender, args);
              }
            });
    return true;
  }

  private void handleCommand(final CommandSender sender, final String[] args) {
    if (sender == null || !(sender instanceof Player)) {
      Text.send(sender, "messages.command-onlyPlayers");
      return;
    }
    Player player = (Player) sender;
    if (checkPermission(player, "easyafk.use")) {
      return;
    }
    if (handleCooldown(player)) {
      return;
    }
    if (args.length == 0) {
      EasyAFK.manager.toggleAFK(player, AFKMode.HARD);
      return;
    }

    switch (args[0].toLowerCase()) {
      case "help" -> {
        sendHelp(player);
      }
      case "status" -> {
        Text.send(
            player,
            "messages.command-status",
            new Text.Replaceable(
                "%status%",
                EasyAFK.manager.isAFK(player)
                    ? "enabled"
                    : "disabled"));
      }
      case "toggle" -> {
        final Player p = getTarget(player, args);
        if (p == null)
          return;
        EasyAFK.manager.toggleAFK(p, AFKMode.HARD);
      }
      case "enable" -> {
        final Player p = getTarget(player, args);
        if (p == null)
          return;
        EasyAFK.manager.enableAFK(p, null);
      }
      case "disable" -> {
        final Player p = getTarget(player, args);
        if (p == null)
          return;
        EasyAFK.manager.disableAFK(p, AFKStopReason.TOGGLED);
      }
      case "reason" -> {
        String reason = "";
        for (int i = 0; i < args.length; i++) {
          reason += args[i];
        }
        reasons.put(player.getUniqueId(), reason);
      }
      case "reload" -> {
        if (!checkPermission(player, "easyafk.admin")) {
          return;
        }
        EasyAFK.instance.reload();
        Text.send(player, "Config reloaded.");
      }
      case "overview" -> {
        if (!checkPermission(player, "easyafk.admin")) {
          return;
        }
        Bukkit
            .getScheduler()
            .runTask(
                EasyAFK.instance,
                () -> {
                  EasyAFK.instance.openOverviewGUI(player);
                });
      }
      default -> {
        Text.send(player, "messages.command-unknown");
      }
    }
  }

  private static boolean checkPermission(
      final Player player,
      final String permission) {
    if (player.hasPermission(permission)) {
      return false;
    }
    Text.send(player, "messages.command-noPermission");
    return true;
  }

  private static void sendHelp(final Player player) {
    Text.send(player, "messages.command-help");
  }

  private static Player getTarget(final Player player, final String[] args) {
    if (args.length < 2) {
      return player;
    }
    if (!checkPermission(player, "easyafk.admin")) {
      Text.send(player, "messages.command-noPermission");
      return null;
    }
    final Player target = Bukkit.getPlayer(args[1]);
    if (target != null) {
      return target;
    }
    Text.send(
        player,
        "messages.command-playerNotFound",
        new Text.Replaceable("%player%", args[1]));
    return player;
  }

  private static boolean handleCooldown(final Player player) {
    final long now = System.currentTimeMillis();
    final UUID uuid = (player).getUniqueId();
    if (!cooldowns.containsKey(uuid)) {
      cooldowns.put(uuid, now);
      return false;
    }
    final long lastUsed = cooldowns.get(uuid);
    if (now - lastUsed > EasyAFK.config.command_cooldown) {
      return false;
    }
    final long secondsLeft = (EasyAFK.config.command_cooldown - (now - lastUsed)) / 1000 + 1;
    Text.send(
        player,
        "messages.command-cooldown",
        new Text.Replaceable("%time%", String.valueOf(secondsLeft)));
    return true;
  }

  @Override
  public List<String> onTabComplete(
      final CommandSender sender,
      final Command label,
      final String cmd,
      final String[] args) {
    final ArrayList<String> tabComplete = new ArrayList<>();

    if (sender.hasPermission("easyafk.use")) {
      tabComplete.add("help");
      tabComplete.add("status");
    }
    if (sender.hasPermission("easyafk.admin")) {
      tabComplete.add("reload");
      tabComplete.add("gui");
      tabComplete.add("toggle");
    }

    if (args.length == 1) {
      return StringUtil.copyPartialMatches(
          args[0],
          tabComplete,
          new ArrayList<>());
    }
    return null;
  }
}
