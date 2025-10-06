package com.julizey.easyafk;

import com.julizey.easyafk.api.AFKState.AFKMode;
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

  public static HashMap<UUID, Long> cooldowns = new HashMap<>();

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
    if (!(sender instanceof Player)) {
      Text.send(sender, "messages.command-onlyPlayers");
      return;
    }
    if (checkPermission(sender, "easyafk.use")) {
      return;
    }
    if (handleCooldown(sender)) {
      return;
    }
    if (args.length == 0) {
      EasyAFK.manager.toggleAFK((Player) sender, AFKMode.HARD);
      return;
    }

    switch (args[0].toLowerCase()) {
      case "help" -> {
        sendHelp(sender);
      }
      case "status" -> {
        Text.send(
            sender,
            "messages.command-status",
            new Text.Replaceable(
                "%status%",
                EasyAFK.manager.isAFK((Player) sender)
                    ? "enabled"
                    : "disabled"));
      }
      case "toggle" -> {
        final Player p = getTarget((Player) sender, args);
        if (p == null)
          return;
        EasyAFK.manager.toggleAFK(p, AFKMode.HARD);
      }
      case "enable" -> {
        final Player p = getTarget((Player) sender, args);
        if (p == null)
          return;
        EasyAFK.manager.enableAFK(p, null);
      }
      case "disable" -> {
        final Player p = getTarget((Player) sender, args);
        if (p == null)
          return;
        EasyAFK.manager.disableAFK(p);
      }
      case "reload" -> {
        if (!checkPermission(sender, "easyafk.admin")) {
          return;
        }
        EasyAFK.instance.reload(true);
        Text.send(sender, "Config reloaded.");
      }
      case "gui" -> {
        if (!checkPermission(sender, "easyafk.admin")) {
          return;
        }
        Bukkit
            .getScheduler()
            .runTask(
                EasyAFK.instance,
                () -> {
                  EasyAFK.instance.openOverviewGUI((Player) sender);
                });
      }
      default -> {
        Text.send(sender, "messages.command-unknown");
      }
    }
  }

  private static boolean checkPermission(
      final CommandSender sender,
      final String permission) {
    if (sender.hasPermission(permission)) {
      return false;
    }
    Text.send(sender, "messages.command-noPermission");
    return true;
  }

  private static void sendHelp(final CommandSender sender) {
    Text.send(sender, "messages.command-help");
  }

  private Player getTarget(final Player sender, final String[] args) {
    if (args.length < 2) {
      return sender;
    }
    if (!checkPermission(sender, "easyafk.admin")) {
      Text.send(sender, "messages.command-noPermission");
      return null;
    }
    final Player target = Bukkit.getPlayer(args[1]);
    if (target != null) {
      return target;
    }
    Text.send(
        sender,
        "messages.command-playerNotFound",
        new Text.Replaceable("%player%", args[1]));
    return sender;
  }

  private boolean handleCooldown(final CommandSender sender) {
    final long now = System.currentTimeMillis();
    final UUID uuid = ((Player) sender).getUniqueId();
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
        sender,
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
