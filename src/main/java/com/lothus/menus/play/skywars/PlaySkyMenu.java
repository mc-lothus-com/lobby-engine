package com.lothus.menus.play.skywars;

import com.lothus.menus.play.browser.BrowserMenu;
import com.mclothus.bukkit.utils.items.ItemCreator;
import com.mclothus.core.Core;
import com.mclothus.core.games.GameInfo;
import com.mclothus.core.games.room.RoomType;
import com.mclothus.core.games.state.GameState;
import com.mclothus.core.games.type.GameType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class PlaySkyMenu implements Listener {

    public static void open(Player player, RoomType type) {
        Inventory inventory = Bukkit.createInventory(null, 9*3, "Sky Wars " + type.getName());

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
        if (!inventory.getName().startsWith("Sky Wars")) return;

        RoomType type = (inventory.getName().endsWith("Solo") ? RoomType.SOLO : RoomType.DUPLAS);

        event.setCancelled(true);

        if (event.getRawSlot() == 11) {
            player.chat("/play " + (type == RoomType.SOLO ? "swsolo" : "swteam"));
            return;
        }

        if (event.getRawSlot() == 15) {
            List<GameInfo> games = Core.getGameController().getAll(GameType.SKY_WARS, type).stream().filter(gameInfo -> gameInfo.getState() == GameState.ESPERANDO).collect(Collectors.toList());
            if (games.isEmpty() || games.size() == 0) {
                player.sendMessage("§cNão existem partidas disponíveis para navegar.");
                return;
            }
            BrowserMenu.open(player, GameType.SKY_WARS, type);
            return;
        }
    }
}
