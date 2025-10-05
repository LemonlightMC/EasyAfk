package com.julizey.easyafk.gui;

import com.julizey.easyafk.EasyAFK;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class AfkPlayerOverviewGUI implements Listener {

  private final String title;

  public AfkPlayerOverviewGUI() {
    this.title = ChatColor.DARK_GRAY + "AFK Players";
  }

  public void openGUI(Player player, int page) {
    List<UUID> afkPlayers = new java.util.ArrayList<>(
        EasyAFK.instance.manager.afkPlayers);
    int pageSize = 45;
    int totalPages = (int) Math.ceil(
        (double) afkPlayers.size() / (double) pageSize);
    if (page < 1) {
      page = 1;
    } else if (page > totalPages) {
      page = totalPages;
    }

    Inventory inventory = Bukkit.createInventory(
        (InventoryHolder) null,
        54,
        this.title + " - Page " + page);
    if (!afkPlayers.isEmpty()) {
      int startIndex = (page - 1) * pageSize;
      int endIndex = Math.min(startIndex + pageSize, afkPlayers.size());

      for (int i = startIndex; i < endIndex; ++i) {
        OfflinePlayer p = Bukkit.getOfflinePlayer(afkPlayers.get(i));
        if (p == null)
          continue;
        inventory.addItem(new ItemStack[] { getPlayerHead(p) });
      }
    }

    ItemStack prevPageItem = this.createButtonItem(ChatColor.GREEN + "Previous Page", Material.ARROW);
    ItemStack nextPageItem = this.createButtonItem(ChatColor.GREEN + "Next Page", Material.ARROW);
    if (page > 1) {
      inventory.setItem(45, prevPageItem);
    }

    if (page < totalPages) {
      inventory.setItem(53, nextPageItem);
    }

    player.openInventory(inventory);
  }

  private ItemStack getPlayerHead(OfflinePlayer p) {
    ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
    ItemMeta meta = head.getItemMeta();
    if (meta instanceof SkullMeta) {
      SkullMeta skullMeta = (SkullMeta) meta;
      skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
      skullMeta.setDisplayName(ChatColor.YELLOW + p.getName());
      head.setItemMeta(skullMeta);
    } else if (meta != null) {
      meta.setDisplayName(ChatColor.YELLOW + p.getName());
      head.setItemMeta(meta);
    }
    return head;
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
    if (!title.startsWith(this.title)) {
      return;
    }
    event.setCancelled(true);
    ItemStack clickedItem = event.getCurrentItem();
    Player player = (Player) event.getWhoClicked();
    if (clickedItem == null || clickedItem.getType() == Material.AIR) {
      return;
    }

    if (clickedItem.getType() == Material.PLAYER_HEAD) {
      String playerName = ChatColor.stripColor(
          clickedItem.getItemMeta().getDisplayName());
      EasyAFK.instance.afkPlayerActionsGUI.openGUI(
          player,
          Bukkit.getPlayer(playerName));
    } else if (clickedItem.getType() == Material.ARROW) {
      int currentPage = Integer.parseInt(
          title.substring(title.lastIndexOf(" ") + 1));
      if (clickedItem
          .getItemMeta()
          .getDisplayName()
          .equals(ChatColor.GREEN + "Previous Page")) {
        this.openGUI(player, currentPage - 1);
      } else if (clickedItem
          .getItemMeta()
          .getDisplayName()
          .equals(ChatColor.GREEN + "Next Page")) {
        this.openGUI(player, currentPage + 1);
      }
    }
  }
}
