package com.julizey.easyafk.gui;

import com.julizey.easyafk.EasyAFK;
import com.julizey.easyafk.utils.Text;
import com.julizey.easyafk.utils.Text.Replaceable;
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
import org.bukkit.inventory.meta.ItemMeta;

public class AfkPlayerActionsGUI implements Listener {

  private String title = ChatColor.DARK_GRAY + "Player Actions";
  private static HashMap<UUID, UUID> targetPlayers = new HashMap<>();

  public AfkPlayerActionsGUI() {
  }

  public void openGUI(Player opener, Player targetPlayer) {
    targetPlayers.put(opener.getUniqueId(), targetPlayer.getUniqueId());
    Inventory inventory = Bukkit.createInventory(
        (InventoryHolder) null,
        9,
        this.title);
    ItemStack kickItem = this.createButtonItem(ChatColor.RED + "Kick Player", Material.BARRIER);
    ItemStack alertItem = this.createButtonItem(ChatColor.YELLOW + "Send Alert", Material.PAPER);
    ItemStack teleportItem = this.createButtonItem(
        ChatColor.GREEN + "Teleport to Player",
        Material.COMPASS);
    ItemStack toggleAFKItem = this.createButtonItem(ChatColor.YELLOW + "Toggle AFK", Material.PISTON);
    inventory.setItem(0, kickItem);
    inventory.setItem(1, alertItem);
    inventory.setItem(2, teleportItem);
    inventory.setItem(3, toggleAFKItem);
    opener.openInventory(inventory);
  }

  private ItemStack createButtonItem(String displayName, Material material) {
    ItemStack item = new ItemStack(material);
    ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(displayName);
    item.setItemMeta(meta);
    return item;
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    String title = event.getView().getTitle();
    if (!title.equals(this.title)) {
      return;
    }
    event.setCancelled(true);
    ItemStack clickedItem = event.getCurrentItem();
    Player player = (Player) event.getWhoClicked();
    UUID targetPlayerId = (UUID) targetPlayers.get(player.getUniqueId());

    if (clickedItem == null ||
        clickedItem.getType() == Material.AIR ||
        targetPlayerId == null) {
      return;
    }
    Player targetPlayer = Bukkit.getPlayer(targetPlayerId);
    if (targetPlayer == null) {
      Text.warn(
          "Could not find target player, probably the player is offline now");
      return;
    }

    if (clickedItem.getType() == Material.BARRIER &&
        clickedItem
            .getItemMeta()
            .getDisplayName()
            .equals(ChatColor.RED + "Kick Player")) {
      targetPlayer.kickPlayer(
          Text.format(
              "messages.kick",
              true,
              true,
              new Replaceable("player", targetPlayer.getName())));
      player.sendMessage(
          ChatColor.GREEN + "Successfully kicked " + targetPlayer.getName());
      player.closeInventory();
    } else if (clickedItem.getType() == Material.PAPER &&
        clickedItem
            .getItemMeta()
            .getDisplayName()
            .equals(ChatColor.YELLOW + "Send Alert")) {
      targetPlayer.sendMessage(
          ChatColor.YELLOW +
              "You are marked as AFK. Keep active to prevent getting kicked!");
      player.sendMessage(
          ChatColor.GREEN + "Sent alert to " + targetPlayer.getName());
      player.closeInventory();
    } else if (clickedItem.getType() == Material.COMPASS &&
        clickedItem
            .getItemMeta()
            .getDisplayName()
            .equals(ChatColor.GREEN + "Teleport to Player")
        &&
        targetPlayer != null) {
      player.teleport(targetPlayer);
      player.sendMessage(
          ChatColor.GREEN + "Teleported to " + targetPlayer.getName());
      player.closeInventory();
    } else if (clickedItem.getType() == Material.PISTON &&
        clickedItem
            .getItemMeta()
            .getDisplayName()
            .equals(ChatColor.YELLOW + "Toggle AFK")) {
      EasyAFK.manager.disableAFK(targetPlayer);
      player.sendMessage(
          ChatColor.GREEN + "Disabled AFK status of " + targetPlayer.getName());
      player.closeInventory();
    }
  }
}
