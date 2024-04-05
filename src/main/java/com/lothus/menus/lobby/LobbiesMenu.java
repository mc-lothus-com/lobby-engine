package com.lothus.menus.lobby;

import com.lothus.api.AbstractMenu;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.utils.bukkit.ItemCreator;
import com.lothus.core.utils.bukkit.player.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;

public class LobbiesMenu extends AbstractMenu {

    private Inventory inventory = getInventory();

    public LobbiesMenu() {
        super(
                "Lobbies",
                (9 * 3)
        );
    }

    @Override
    public void items(Player player) {
        int slot = 9;

        Comparator<ServerInfo> comparator = Comparator.comparing(ServerInfo::getId);
        List<ServerInfo> list = Core.getServerController().get(Core.getServerInfo().getType());
        list.sort(comparator);

        for (ServerInfo serverInfo : list) {
            slot++;
            if (slot == 17) slot += 2;
            if (slot == 26) slot += 2;
            if (slot == 35) slot += 2;

            if (serverInfo.getName().equals(Core.getServerInfo().getName())) {
                inventory.setItem(
                        slot,
                        new ItemCreator(Material.INK_SACK, "§a" + serverInfo.getType().getName() + serverInfo.getId()).setLore(
                                "§7Jogadores: " + Core.getServerInfo().getPlayers() + "/" + Core.getServerInfo().getConfiguration().getMaxPlayers(),
                                "§cVocê está aqui.").setId(5).build()
                );
            } else {
                inventory.setItem(
                        slot,
                        new ItemCreator(Material.INK_SACK, "§a" + serverInfo.getType().getName() + serverInfo.getId()).setLore(
                                "§7Jogadores: " + serverInfo.getPlayers() + "/" + serverInfo.getConfiguration().getMaxPlayers(),
                                "§eClique para conectar.").setId(10).build()
                );
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack currentItem = event.getCurrentItem();

        if (clickedInventory == null) return;
        if (currentItem.getType() == Material.AIR) return;

        if (!clickedInventory.getName().equalsIgnoreCase(getTitle())) return;

        event.setCancelled(true);

        for (ServerInfo s : Core.getServerController().get(Core.getServerInfo().getType())) {
            if (currentItem.getItemMeta().getDisplayName().endsWith("#" + s.getId())) {
                if (s.getPort() == Core.getServerInfo().getPort()) {
                    player.sendMessage("§cVocê já está conectado a este servidor.");
                    return;
                }

                player.closeInventory();
                switch (PlayerUtil.connect(player.getUniqueId(), s)) {
                    case SERVER_NULL:
                        player.sendMessage("§cO servidor solicitado é inválido.");
                        break;
                    case SERVER_MAINTENANCE:
                        player.sendMessage("§cO servidor solicitado está em manutenção.");
                        break;
                    case SERVER_FULL_AND_ROOM:
                        player.sendMessage("§cA sala solicitada está cheia.");
                        break;
                    case SERVER_FULL:
                        player.sendMessage("§cO servidor solicitado está cheio.");
                        break;
                }
                return;
            }
        }
    }
}