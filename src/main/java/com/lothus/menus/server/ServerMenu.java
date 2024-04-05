package com.lothus.menus.server;

import com.lothus.Lobby;
import com.lothus.api.AbstractMenu;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.servers.type.ServerType;
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

public class ServerMenu extends AbstractMenu {

    private Inventory inventory = getInventory();

    public ServerMenu() {
        super(
                "Modos de Jogo",
                (9*4)
        );
    }

    @Override
    public void items(Player player) {
        inventory.setItem(10, new ItemCreator(Material.BOOKSHELF).setDisplayName("§aLobby Principal")
                .setLore(
                        (Core.getServerInfo().getType() == ServerType.LOBBY ? "§cVocê já está aqui." : "§eClique para conectar.")
                ).build());
        inventory.setItem(19, new ItemCreator(Material.DIAMOND).setDisplayName("§bContribua com a Lothus").setLore(
                "§7Você pode ajudar a Lothus tornando-se",
                "§dMaster§7!",
                "",
                "§7Além de ajudar a rede se manter online",
                "§7você ainda terá acesso a vários",
                "§7benefícios exclusivos!",
                "",
                "§eClique para copiar o link da loja."
        ).build());

        inventory.setItem(12, new ItemCreator(Material.GRASS).setDisplayName("§aSky Wars").setLore(
                "§7" + (Lobby.getDefinitions().getOnlineCount(ServerType.LOBBY_SKYWARS) + Lobby.getDefinitions().getOnlineCount(ServerType.ROOM_SKYWARS)) + " jogando agora!",
                "§eClique para conectar-se."
        ).build());
        inventory.setItem(13, new ItemCreator(Material.BED).setDisplayName("§aBed Wars").setLore(
                "§7" + (Lobby.getDefinitions().getOnlineCount(ServerType.LOBBY_BEDWARS) + Lobby.getDefinitions().getOnlineCount(ServerType.ROOM_BEDWARS)) + " jogando agora!",
                "§eClique para conectar-se."
        ).build());
        inventory.setItem(14, new ItemCreator(Material.DIAMOND_CHESTPLATE).setDisplayName("§aTreinamento").setLore(
                "§7" + (Lobby.getDefinitions().getOnlineCount(ServerType.LOBBY_DUELS) +Lobby.getDefinitions().getOnlineCount(ServerType.ROOM_DUELS)) + " jogando agora!",
                "§eClique para conectar-se."
        ).build());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();
        ItemStack itemStack = event.getCurrentItem();
        LothPlayer l = Core.getPlayerController().get(player.getUniqueId());

        if (itemStack == null) return;
        if (clickedInventory == null) return;

        if (!clickedInventory.getName().equalsIgnoreCase("Modos de Jogo")) return;

        event.setCancelled(true);

        if (event.getRawSlot() == 19) {
            player.sendMessage("§eContribua com a Lothus: §bhttps://mc-lothus.com/itens");
            return;
        }

        ServerType type = (event.getRawSlot() == 12 ? ServerType.LOBBY_SKYWARS : event.getRawSlot() == 13 ? ServerType.LOBBY_BEDWARS : event.getRawSlot() == 14 ? ServerType.LOBBY_DUELS : event.getRawSlot() == 10 ?  ServerType.LOBBY : null);

        if (type == null)return;

        if (Core.getServerInfo().getType() == type) {
            player.sendMessage("§cVocê já está em um lobby.");
            return;
        }

        Comparator<ServerInfo> comparator = Comparator.comparing(ServerInfo::getPlayers);
        List<ServerInfo> list = Core.getServerController().get(type);
        list.sort(comparator);

        if (list.isEmpty()) {
            player.sendMessage("§cNossos servidores estão indisponíveis no momento.");
            return;
        }

        ServerInfo serverInfo = list.get(0);

        if (serverInfo == null) {
            player.sendMessage("§cNossos servidores estão indisponíveis no momento.");
            return;
        }

        switch (PlayerUtil.connect(player.getUniqueId(), serverInfo)) {
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
    }

}
