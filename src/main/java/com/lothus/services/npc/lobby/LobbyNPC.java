package com.lothus.services.npc.lobby;

import com.lothus.Lobby;
import com.lothus.services.Services;
import com.mclothus.bukkit.BukkitCore;
import com.mclothus.bukkit.events.skin.PlayerChangeSkinEvent;
import com.mclothus.bukkit.events.update.UpdateEvent;
import com.mclothus.bukkit.utils.locations.type.LocationType;
import com.mclothus.bukkit.utils.player.PlayerUtil;
import com.mclothus.core.Core;
import com.mclothus.core.servers.ServerInfo;
import com.mclothus.core.servers.type.ServerType;
import lombok.Getter;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.events.NPCInteractEvent;
import net.jitse.npclib.api.skin.Skin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Comparator;
import java.util.List;

import static com.mclothus.core.servers.type.ServerType.*;

public class LobbyNPC implements Listener {
    
    @Getter
    private NPC skywars, bedwars, training, battlepass;

    private JavaPlugin plugin;

    public LobbyNPC(JavaPlugin plugin) {
        this.plugin = plugin;
        skywars = Services.getNpcService().create(Lobby.getDefinitions().getLocation(LocationType.NPC_SKYWARS), new Skin(plugin.getConfig().getString("config.skins.npcs.skywars.lobby.value"), plugin.getConfig().getString("config.skins.npcs.skywars.lobby.signature")), "§2§lSKY WARS", "§a0 jogando agora!");
        bedwars = Services.getNpcService().create(Lobby.getDefinitions().getLocation(LocationType.NPC_BEDWARS), new Skin(plugin.getConfig().getString("config.skins.npcs.bedwars.lobby.value"), plugin.getConfig().getString("config.skins.npcs.bedwars.lobby.signature")),"§2§lBED WARS", "§a0 jogando agora!");
        training = Services.getNpcService().create(Lobby.getDefinitions().getLocation(LocationType.NPC_TRAINING), new Skin(plugin.getConfig().getString("config.skins.npcs.training.value"), plugin.getConfig().getString("config.skins.npcs.training.signature")), "§2§lTREINAMENTO", "§a0 jogando agora!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        showNpcs(player);
    }

    @EventHandler
    public void onChangeSkin(PlayerChangeSkinEvent event) {
        Player player = event.getPlayer();

        showNpcs(player);
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        Services.getNpcService().updateText(skywars, (BukkitCore.getInstance().getOnlineCount(LOBBY_SKYWARS) + BukkitCore.getInstance().getOnlineCount(ROOM_SKYWARS)));
        Services.getNpcService().updateText(bedwars, (BukkitCore.getInstance().getOnlineCount(LOBBY_BEDWARS) + BukkitCore.getInstance().getOnlineCount(ROOM_BEDWARS)));
        Services.getNpcService().updateText(training, (BukkitCore.getInstance().getOnlineCount(LOBBY_DUELS) + BukkitCore.getInstance().getOnlineCount(ROOM_DUELS)));
    }

    @EventHandler
    public void onInteract(NPCInteractEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getWhoClicked().getPlayer();

        ServerType type = (npc == skywars ? ServerType.LOBBY_SKYWARS : npc == training ? ServerType.LOBBY_DUELS : npc == bedwars ? ServerType.LOBBY_BEDWARS : null);

        if (type == null)return;

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

    private void showNpcs(Player player) {
        if (skywars.isShown(player)) {
            skywars.hide(player);
        } else if (bedwars.isShown(player)) {
            bedwars.hide(player);
        } else if (training.isShown(player)) {
            training.hide(player);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    skywars.show(player);
                    bedwars.show(player);
                    training.show(player);
                } catch (Exception e) {

                }
            }
        }.runTaskLater(plugin, 25L);
    }
}
