package com.julizey.easyafk.gui;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.api.AFKKickEvent.AFKKickReason;
import com.julizey.easyafk.api.AFKState.AFKMode;
import com.julizey.easyafk.utils.Text;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class AfkPlayerActionsGUI implements Listener {

  private static final String title = ChatColor.RED + "[AFK]" + ChatColor.DARK_GRAY + "Player Actions";
  private static final String kickItemTitle = ChatColor.RED + "Kick Player";
  private static final String alertItemTitle = ChatColor.YELLOW + "Send Alert";
  private static final String teleportItemTitle = ChatColor.GREEN + "Teleport to Player";
  private static final String toggleAfkItemTitle = ChatColor.YELLOW + "Toggle AFK";

  private static Inventory inv;
  private static HashMap<UUID, UUID> targetPlayers = new HashMap<>();

  public AfkPlayerActionsGUI() {
  }

  public void openGUI(final Player opener, final Player targetPlayer) {
    targetPlayers.put(opener.getUniqueId(), targetPlayer.getUniqueId());
    if (inv != null) {
      opener.openInventory(inv);
    }
    inv = createInventory();
    opener.openInventory(inv);
  }

  @EventHandler
  public void onInventoryClick(final InventoryClickEvent event) {
    final String title = event.getView().getTitle();
    if (!title.equals(title)) {
      return;
    }
    event.setCancelled(true);

    final ItemStack clickedItem = event.getCurrentItem();
    if (clickedItem == null || clickedItem.getType() == Material.AIR) {
      return;
    }
    final Player player = (Player) event.getWhoClicked();
    if (player == null) {
      return;
    }
    final UUID targetPlayerId = (UUID) targetPlayers.get(player.getUniqueId());
    if (targetPlayerId == null) {
      return;
    }
    final Player targetPlayer = Bukkit.getPlayer(targetPlayerId);
    if (targetPlayer == null) {
      Text.warn(
          "Could not find target player, probably the player is offline now");
      return;
    }

    if (AfkPlayerOverviewGUI.isButton(clickedItem, kickItemTitle, Material.BARRIER)) {
      EasyAFK.manager.kickPlayer(targetPlayer, AFKKickReason.KICKED);
      player.sendMessage(
          ChatColor.GREEN + "Successfully kicked " + targetPlayer.getName());
      player.closeInventory();
    } else if (AfkPlayerOverviewGUI.isButton(clickedItem, alertItemTitle, Material.PAPER)) {
      targetPlayer.sendMessage(
          ChatColor.YELLOW +
              "You are marked as AFK. Keep active to prevent getting kicked!");
      player.sendMessage(
          ChatColor.GREEN + "Sent alert to " + targetPlayer.getName());
      player.closeInventory();
    } else if (AfkPlayerOverviewGUI.isButton(clickedItem, teleportItemTitle, Material.COMPASS)
        && targetPlayer != null) {
      player.teleport(targetPlayer);
      player.sendMessage(
          ChatColor.GREEN + "Teleported to " + targetPlayer.getName());
      player.closeInventory();
    } else if (AfkPlayerOverviewGUI.isButton(clickedItem, toggleAfkItemTitle, Material.PISTON)) {
      EasyAFK.manager.toggleAFK(targetPlayer, AFKMode.HARD);
      player.sendMessage(
          ChatColor.GREEN + "Disabled AFK status of " + targetPlayer.getName());
      player.closeInventory();
    }
  }

  private static Inventory createInventory() {
    final Inventory inventory = Bukkit.createInventory(
        (InventoryHolder) null,
        9,
        title);
    final ItemStack kickItem = AfkPlayerOverviewGUI.createButtonItem(kickItemTitle, Material.BARRIER);
    final ItemStack alertItem = AfkPlayerOverviewGUI.createButtonItem(alertItemTitle, Material.PAPER);
    final ItemStack teleportItem = AfkPlayerOverviewGUI.createButtonItem(teleportItemTitle, Material.COMPASS);
    final ItemStack toggleAFKItem = AfkPlayerOverviewGUI.createButtonItem(toggleAfkItemTitle, Material.PISTON);
    inventory.setItem(0, kickItem);
    inventory.setItem(1, alertItem);
    inventory.setItem(2, teleportItem);
    inventory.setItem(3, toggleAFKItem);
    return inventory;
  }
}
