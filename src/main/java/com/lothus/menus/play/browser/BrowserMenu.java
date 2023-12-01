package com.lothus.menus.play.browser;

import com.lothus.engines.sync.data.type.DataType;
import com.lothus.engines.sync.platform.Platform;
import com.lothus.engines.sync.player.games.bedwars.BedPlayer;
import com.lothus.engines.sync.player.games.skywars.SkyPlayer;
import com.lothus.engines.sync.player.maps.FavoriteMap;
import com.lothus.menus.play.bedwars.PlayBedMenu;
import com.lothus.menus.play.skywars.PlaySkyMenu;
import com.mclothus.bukkit.utils.items.ItemCreator;
import com.mclothus.bukkit.utils.player.PlayerUtil;
import com.mclothus.core.Core;
import com.mclothus.core.games.GameInfo;
import com.mclothus.core.games.room.RoomType;
import com.mclothus.core.games.state.GameState;
import com.mclothus.core.games.type.GameType;
import com.mclothus.core.player.party.Party;
import com.mclothus.core.player.party.packet.PacketParty;
import com.mclothus.core.storage.redis.channels.RedisChannel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class BrowserMenu implements Listener {

    public static void open(Player player, GameType gameType, RoomType type) {
        SkyPlayer skyPlayer = Platform.getSkyPlatform().getSkyPlayerController().getAccount(player.getUniqueId());
        Inventory inventory = Bukkit.createInventory(null, 9 * 6,
                "Navegador - " + (gameType == GameType.SKY_WARS ? "Sky Wars " : "Bed Wars ") + type.getName()
        );

        int slot = 9;

        for (GameInfo gameInfo : Core.getGameController().getAll(gameType, type)) {
            if (gameInfo.getState() == GameState.PREPARANDO || gameInfo.getState() == GameState.EM_JOGO || gameInfo.getState() == GameState.ENCERRANDO || gameInfo.getState() == GameState.REINICIANDO)
                continue;

            slot++;
            if (slot == 17) slot += 2;
            if (slot == 26) slot += 2;
            if (slot >= 36) break;

            if (skyPlayer.getFavoriteMap(gameInfo.getDisplay()) == null) {
                inventory.setItem(slot, new ItemCreator(Material.MAP,
                        "§a" + gameInfo.getDisplay() + " §b#" + gameInfo.getId()
                ).setLore(
                        "",
                        "§fJogadores: §a" + gameInfo.getPlayers(),
                        "§fEstado da Partida: " + (gameInfo.getState().equals(GameState.ESPERANDO) ? "§aAguardando" : (gameInfo.getState().equals(GameState.PREPARANDO) ? "§6Preparando" : (gameInfo.getState().equals(GameState.EM_JOGO) ? "§cEm Jogo" : gameInfo.getState() == GameState.ENCERRANDO ? "§cEncerrando" : "§4Reiniciando"))),
                        "§fMáximo de Jogadores: §6" + gameInfo.getMaxPlayers(),
                        "",
                        "§eClique para entrar."
                ).build());
            } else {
                inventory.setItem(slot, new ItemCreator(Material.MAP,
                        "§6✰ §a" + gameInfo.getDisplay() + " §b#" + gameInfo.getId()
                ).setLore(
                        "",
                        "§fJogadores: §a" + gameInfo.getPlayers(),
                        "§fEstado da Partida: " + (gameInfo.getState().equals(GameState.ESPERANDO) ? "§aAguardando" : (gameInfo.getState().equals(GameState.PREPARANDO) ? "§6Preparando" : (gameInfo.getState().equals(GameState.EM_JOGO) ? "§cEm Jogo" : gameInfo.getState() == GameState.ENCERRANDO ? "§cEncerrando" : "§4Reiniciando"))),
                        "§fMáximo de Jogadores: §6" + gameInfo.getMaxPlayers(),
                        "",
                        "§eClique para entrar."
                ).build());
            }
        }


        inventory.setItem(49, new ItemCreator(Material.ARROW,
                "§cVoltar"
        ).build());

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack item = event.getCurrentItem();


        if (item == null) return;
        if (inventory == null) return;

        if (item.getType() == Material.AIR) return;

        if (!(inventory.getName().startsWith("Navegador"))) return;

        event.setCancelled(true);

        GameType gameType = null;
        RoomType type = null;

        if (inventory.getName().contains("Sky Wars")) {
            gameType = GameType.SKY_WARS;
        } else {
            gameType = GameType.BED_WARS;
        }

        for (RoomType t : RoomType.values()) {
            if (inventory.getName().contains(t.getName())) {
                type = t;
            }
        }

        if (event.getRawSlot() == 49) {
            if (gameType == GameType.SKY_WARS) {
                PlaySkyMenu.open(player, type);
                return;
            } else {
                PlayBedMenu.open(player, type);
            }
        }

        for (GameInfo gameInfo : Core.getGameController().getAll()) {
            if (gameInfo == null) return;
            if (gameInfo.getRoomType() != type) continue;
            if (gameInfo.getState() == GameState.PREPARANDO || gameInfo.getState() == GameState.EM_JOGO || gameInfo.getState() == GameState.ENCERRANDO)
                continue;


            if (item.getItemMeta().getDisplayName().contains(gameInfo.getDisplay())) {
                if (item.getItemMeta().getDisplayName().split("#")[1].equals(gameInfo.getId())) {
                    if (event.isLeftClick()) {
                        Party party = Core.getDataParty().get(player.getUniqueId());

                        if (party != null) {
                            if (!party.isLeader(player.getUniqueId())) {
                                player.sendMessage("§cApenas o líder do grupo pode entrar em uma partida.");
                                return;
                            }

                            if (party.size() > gameInfo.getMaxPlayers()) {
                                player.sendMessage("§cO grupo não pode entrar nessa partida, pois excede o limite de jogadores.");
                                return;
                            }

                            if (party.size() >= (gameInfo.getMaxPlayers() - gameInfo.getPlayers())) {
                                player.sendMessage("§cO grupo não pode entrar nessa partida, pois excede o limite de jogadores.");
                                return;
                            }
                        }

                        switch (PlayerUtil.connect(player.getUniqueId(), Core.getServerController().get(gameInfo.getName()))) {
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

                    if (event.isRightClick()) {
                        if (gameType == GameType.SKY_WARS) {
                            SkyPlayer skyPlayer = Platform.getSkyPlatform().getSkyPlayerController().getAccount(player.getUniqueId());
                            if (skyPlayer.getFavoriteMap(gameInfo.getDisplay()) == null) {
                                skyPlayer.addFavoriteMap(new FavoriteMap(gameInfo.getDisplay(), type));
                                player.sendMessage("§aMapa adicionado aos favoritos!");
                                open(player, gameType, type);
                                Platform.getDataPlayer().update((type == RoomType.SOLO ? DataType.SKY_WARS_SOLO : type == RoomType.DUPLAS ? DataType.SKY_WARS_TEAM : DataType.SKY_WARS_RANKED), skyPlayer);
                                return;
                            } else {
                                skyPlayer.removeFavoriteMap(gameInfo.getDisplay());
                                player.sendMessage("§aMapa removido dos favoritos!");
                                open(player, gameType, type);
                                Platform.getDataPlayer().update((type == RoomType.SOLO ? DataType.SKY_WARS_SOLO : type == RoomType.DUPLAS ? DataType.SKY_WARS_TEAM : DataType.SKY_WARS_RANKED), skyPlayer);
                                return;
                            }
                        } else {
                            BedPlayer skyPlayer = Platform.getBedPlatform().getBedPlayerController().getAccount(player.getUniqueId());
                            if (skyPlayer.getFavoriteMap(gameInfo.getDisplay()) == null) {
                                skyPlayer.addFavoriteMap(new FavoriteMap(gameInfo.getDisplay(), type));
                                player.sendMessage("§aMapa adicionado aos favoritos!");
                                open(player, gameType, type);
                                Platform.getDataPlayer().update((type == RoomType.SOLO ? DataType.BED_WARS_SOLO : type == RoomType.DUPLAS ? DataType.BED_WARS_TEAM : type == RoomType.TRIOS ? DataType.BED_WARS_TRIO : DataType.BED_WARS_QUARTETO), skyPlayer);
                                return;
                            } else {
                                skyPlayer.removeFavoriteMap(gameInfo.getDisplay());
                                player.sendMessage("§aMapa removido dos favoritos!");
                                open(player, gameType, type);
                                Platform.getDataPlayer().update((type == RoomType.SOLO ? DataType.BED_WARS_SOLO : type == RoomType.DUPLAS ? DataType.BED_WARS_TEAM : type == RoomType.TRIOS ? DataType.BED_WARS_TRIO : DataType.BED_WARS_QUARTETO), skyPlayer);
                                return;
                            }
                        }
                    }
                    return;
                }
            }
        }
    }

}
