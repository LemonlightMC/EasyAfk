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

  private static final String title = ChatColor.DARK_GRAY + "AFK Players";
  private static final String nextPageTitle = ChatColor.GREEN + "Next Page";
  private static final String prevPageTitle = ChatColor.GREEN + "Previous Page";
  private static final int pageSize = 45;

  public AfkPlayerOverviewGUI() {
  }

  public void openGUI(final Player player, int page) {
    final List<UUID> afkPlayers = new java.util.ArrayList<>(
        EasyAFK.manager.getPlayers().keySet());
    final int totalPages = (int) Math.ceil(afkPlayers.size() / pageSize);
    page = Math.min(page < 1 ? 1 : page > totalPages ? totalPages : page, 20);

    final Inventory inventory = Bukkit.createInventory(
        (InventoryHolder) null,
        54,
        title + " - Page " + page);
    if (!afkPlayers.isEmpty()) {
      final int startIndex = (page - 1) * pageSize;
      final int endIndex = Math.min(startIndex + pageSize, afkPlayers.size());

      for (int i = startIndex; i < endIndex; ++i) {
        final OfflinePlayer p = Bukkit.getOfflinePlayer(afkPlayers.get(i));
        if (p == null)
          continue;
        inventory.addItem(new ItemStack[] { getPlayerHead(p) });
      }
    }

    final ItemStack prevPageItem = createButtonItem(prevPageTitle, Material.ARROW);
    final ItemStack nextPageItem = createButtonItem(nextPageTitle, Material.ARROW);
    if (page > 1) {
      inventory.setItem(45, prevPageItem);
    }
    if (page < totalPages) {
      inventory.setItem(53, nextPageItem);
    }

    player.openInventory(inventory);
  }

  @EventHandler
  public void onInventoryClick(final InventoryClickEvent event) {
    final String title = event.getView().getTitle();
    if (!title.startsWith(title)) {
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

    if (clickedItem.getType() == Material.PLAYER_HEAD) {
      final String playerName = ChatColor.stripColor(
          clickedItem.getItemMeta().getDisplayName());
      EasyAFK.instance.openActionGUI(
          player,
          Bukkit.getPlayerExact(playerName));
    } else if (isButton(clickedItem, prevPageTitle, Material.ARROW)) {
      final int currentPage = Integer.parseInt(
          title.substring(title.lastIndexOf(" ") + 1));
      this.openGUI(player, currentPage - 1);
    } else if (isButton(clickedItem, nextPageTitle, Material.ARROW)) {
      final int currentPage = Integer.parseInt(
          title.substring(title.lastIndexOf(" ") + 1));
      this.openGUI(player, currentPage + 1);
    }
  }

  public ItemStack getPlayerHead(final OfflinePlayer p) {
    final ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
    final ItemMeta meta = head.getItemMeta();
    if (meta instanceof SkullMeta) {
      final SkullMeta skullMeta = (SkullMeta) meta;
      skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
      skullMeta.setDisplayName(ChatColor.YELLOW + p.getName());
      head.setItemMeta(skullMeta);
    } else if (meta != null) {
      meta.setDisplayName(ChatColor.YELLOW + p.getName());
      head.setItemMeta(meta);
    }
    return head;
  }

  public static ItemStack createButtonItem(final String displayName, final Material material) {
    final ItemStack item = new ItemStack(material);
    final ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(displayName);
    item.setItemMeta(meta);
    return item;
  }

  public static boolean isButton(final ItemStack item, final String title, final Material material) {
    final ItemMeta meta = item.getItemMeta();
    return meta != null && meta.getDisplayName().equals(title) && item.getType() == material;
  }
}
