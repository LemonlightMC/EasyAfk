package com.julizey.easyafk;

import com.julizey.easyafk.utils.Text;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class EasyAFKCommand implements TabExecutor {

  public static HashMap<UUID, Long> cooldowns = new HashMap<>();

  public EasyAFKCommand() {}

  public boolean onCommand(
    CommandSender sender,
    Command command,
    String label,
    String[] args
  ) {
    Bukkit
      .getScheduler()
      .runTaskAsynchronously(
        EasyAFK.instance,
        new Runnable() {
          @Override
          public void run() {
            handleCommand(sender, args);
          }
        }
      );
    return true;
  }

  private void handleCommand(CommandSender sender, String[] args) {
    if (!checkPermission(sender, "easyafk.use")) {
      return;
    }
    if (!handleCooldown(sender)) {
      return;
    }
    if (args.length == 0) {
      if (!(sender instanceof Player)) {
        Text.send(sender, "messages.command-onlyPlayers");
        return;
      }
      EasyAFK.instance.afkState.toggle((Player) sender);
      return;
    }

    switch (args[0].toLowerCase()) {
      case "help" -> {
        sendHelp(sender);
      }
      case "status" -> {
        if (!(sender instanceof Player)) {
          Text.send(sender, "messages.command-onlyPlayers");
          return;
        }
        Text.send(
          sender,
          "messages.command-status",
          new Text.Replaceable(
            "%status%",
            EasyAFK.instance.afkState.isAfk((Player) sender)
              ? "enabled"
              : "disabled"
          )
        );
      }
      case "toggle" -> {
        if (!(sender instanceof Player)) {
          Text.send(sender, "messages.command-onlyPlayers");
          return;
        }
        Player p = getTarget((Player) sender, args);
        if (p == null) return;
        EasyAFK.instance.afkState.toggle(p);
      }
      case "enable" -> {
        if (!(sender instanceof Player)) {
          Text.send(sender, "messages.command-onlyPlayers");
          return;
        }
        Player p = getTarget((Player) sender, args);
        if (p == null) return;
        EasyAFK.instance.afkState.enableAFK(p);
      }
      case "disable" -> {
        if (!(sender instanceof Player)) {
          Text.send(sender, "messages.command-onlyPlayers");
          return;
        }
        Player p = getTarget((Player) sender, args);
        if (p == null) return;
        EasyAFK.instance.afkState.disableAFK(p);
      }
      case "reload" -> {
        if (!checkPermission(sender, "easyafk.admin")) {
          return;
        }
        EasyAFK.instance.reload(true);
        Text.send(sender, "Config reloaded.");
        if (!(sender instanceof ConsoleCommandSender)) {
          Text.info("Config reloaded.");
        }
      }
      case "gui" -> {
        if (!checkPermission(sender, "easyafk.admin")) {
          return;
        }
        if (!(sender instanceof Player)) {
          Text.send(sender, "messages.command-onlyPlayers");
          return;
        }
        Bukkit
          .getScheduler()
          .runTask(
            EasyAFK.instance,
            () -> {
              EasyAFK.instance.afkPlayerOverviewGUI.openGUI((Player) sender, 1);
            }
          );
      }
      default -> {
        Text.send(sender, "messages.command-unknown");
      }
    }
  }

  private static boolean checkPermission(
    CommandSender sender,
    String permission
  ) {
    if (!sender.hasPermission(permission)) {
      Text.send(sender, "messages.command-noPermission");
      return false;
    }
    return true;
  }

  private static void sendHelp(CommandSender sender) {
    Text.send(sender, "messages.command-help");
  }

  private Player getTarget(Player sender, String[] args) {
    if (args.length < 2) {
      return sender;
    }
    if (!checkPermission(sender, "easyafk.admin")) {
      Text.send(sender, "messages.command-noPermission");
      return null;
    }
    Player target = Bukkit.getPlayer(args[1]);
    if (target != null) {
      return target;
    }
    Text.send(
      sender,
      "messages.command-playerNotFound",
      new Text.Replaceable("%player%", args[1])
    );
    return sender;
  }

  private boolean handleCooldown(CommandSender sender) {
    long now = System.currentTimeMillis();
    UUID uuid = ((Player) sender).getUniqueId();
    if (!cooldowns.containsKey(uuid)) {
      cooldowns.put(uuid, now);
      return false;
    }
    long lastUsed = cooldowns.get(uuid);
    if (now - lastUsed > EasyAFK.config.command_cooldown) {
      return false;
    }
    long secondsLeft =
      (EasyAFK.config.command_cooldown - (now - lastUsed)) / 1000 + 1;
    Text.send(
      sender,
      "messages.command-cooldown",
      new Text.Replaceable("%time%", String.valueOf(secondsLeft))
    );
    return true;
  }

  @Override
  public List<String> onTabComplete(
    CommandSender sender,
    Command label,
    String cmd,
    String[] args
  ) {
    ArrayList<String> tabComplete = new ArrayList<>();

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
        new ArrayList<>()
      );
    }
    return null;
  }
}
