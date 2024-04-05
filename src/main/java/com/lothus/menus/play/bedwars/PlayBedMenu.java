package com.lothus.menus.play.bedwars;

import com.lothus.core.Core;
import com.lothus.core.games.GameInfo;
import com.lothus.core.games.room.RoomType;
import com.lothus.core.games.type.GameType;
import com.lothus.core.utils.bukkit.ItemCreator;
import com.lothus.menus.play.browser.BrowserMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayBedMenu implements Listener {

    public static void open(Player player, RoomType type) {
        Inventory inventory = Bukkit.createInventory(null, 9*3, "Bed Wars " + type.getName());

        inventory.setItem(11, new ItemCreator(Material.ENDER_PEARL, "§aPartida Aleatória")
                .setLore(
                        "§eClique para entrar."
                ).build());

        inventory.setItem(15, new ItemCreator(Material.PAPER, "§aSelecione um Mapa")
                .setLore(
                        "§7Selecione um mapa para jogar.",
                        "§7Clique para expandir."
                ).build());

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack item = event.getCurrentItem();

        if (item == null)return;
        if (inventory == null) return;
        if (!inventory.getName().startsWith("Bed Wars")) return;

        RoomType type = (inventory.getName().endsWith("Solo") ? RoomType.SOLO : inventory.getName().endsWith("Duplas") ? RoomType.DUPLAS : inventory.getName().endsWith("Trios") ? RoomType.TRIOS : inventory.getName().endsWith("Quarteto") ? RoomType.QUARTETOS : RoomType.RANQUEADO);

        event.setCancelled(true);

        if (event.getRawSlot() == 11) {
            player.chat("/play " + (type == RoomType.SOLO ? "bwsolo" : type == RoomType.DUPLAS ? "bwteam" : type == RoomType.TRIOS ? "bwtrio" : type == RoomType.QUARTETOS ? "bwquarteto" : "bwranked"));
            return;
        }

        if (event.getRawSlot() == 15) {
            if (item.getType() != Material.PAPER)return;
            List<GameInfo> games = Core.getGameController().getAll(GameType.BED_WARS, type);
            if (games.isEmpty() || games.size() == 0) {
                player.sendMessage("§cNão existem partidas disponíveis para navegar.");
                return;
            }
            BrowserMenu.open(player, GameType.BED_WARS, type);
            return;
        }
    }
}
