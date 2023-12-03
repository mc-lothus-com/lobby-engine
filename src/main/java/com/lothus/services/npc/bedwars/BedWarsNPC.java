package com.lothus.services.npc.bedwars;

import com.lothus.Lobby;
import com.lothus.menus.play.bedwars.PlayBedMenu;
import com.lothus.menus.play.skywars.PlaySkyMenu;
import com.lothus.player.LobbyPlayer;
import com.lothus.services.Services;
import com.lothus.utils.LocationUtil;
import com.mclothus.bukkit.events.PlayerChangeSkinEvent;
import com.mclothus.bukkit.events.UpdateEvent;
import com.mclothus.bukkit.utils.locations.type.LocationType;
import com.mclothus.bukkit.utils.player.PlayerUtil;
import com.mclothus.core.Core;
import com.mclothus.core.games.room.RoomType;
import com.mclothus.core.games.type.GameType;
import com.mclothus.core.servers.ServerInfo;
import com.mclothus.core.servers.type.ServerType;
import lombok.Getter;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.events.NPCInteractEvent;
import net.jitse.npclib.api.skin.Skin;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Comparator;
import java.util.List;

import static com.mclothus.core.games.room.RoomType.*;
import static com.mclothus.core.games.type.GameType.BED_WARS;
import static com.mclothus.core.servers.type.ServerType.*;

public class BedWarsNPC implements Listener {

    @Getter
    private NPC solo, team, trio, quarteto;

    private JavaPlugin plugin;

    public BedWarsNPC(JavaPlugin plugin) {
        this.plugin = plugin;
        solo = Services.getNpcService().create(Lobby.getDefinitions().getLocation(LocationType.NPC_SOLO), new Skin(plugin.getConfig().getString("config.skins.npcs.bedwars.solo.value"), plugin.getConfig().getString("config.skins.npcs.bedwars.solo.signature")), "§2§lSOLOS", "§a0 jogando agora!");
        team = Services.getNpcService().create(Lobby.getDefinitions().getLocation(LocationType.NPC_TEAM), new Skin(plugin.getConfig().getString("config.skins.npcs.bedwars.team.value"), plugin.getConfig().getString("config.skins.npcs.bedwars.team.signature")),"§2§lDUPLAS", "§a0 jogando agora!");
        trio = Services.getNpcService().create(Lobby.getDefinitions().getLocation(LocationType.NPC_TRIO), new Skin(plugin.getConfig().getString("config.skins.npcs.bedwars.trio.value"), plugin.getConfig().getString("config.skins.npcs.bedwars.trio.signature")), "§2§lTRIOS", "§a0 jogando agora!");
        quarteto = Services.getNpcService().create(Lobby.getDefinitions().getLocation(LocationType.NPC_QUARTETO), new Skin(plugin.getConfig().getString("config.skins.npcs.bedwars.quarteto.value"), plugin.getConfig().getString("config.skins.npcs.bedwars.quarteto.signature")), "§2§lQUARTETOS", "§a0 jogando agora!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (solo.isShown(player)) {
            solo.hide(player);
        } else if (team.isShown(player)) {
            team.hide(player);
        } else if (trio.isShown(player)) {
            trio.hide(player);
        } else if (quarteto.isShown(player)) {
            quarteto.hide(player);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    solo.show(player);
                    team.show(player);
                    trio.show(player);
                    quarteto.show(player);
                } catch (Exception e) {

                }
            }
        }.runTaskLater(plugin, 5L);
    }

    @EventHandler
    public void onChangeSkin(PlayerChangeSkinEvent event) {
        Player player = event.getPlayer();

        solo.hide(player);
        team.hide(player);
        trio.hide(player);
        quarteto.hide(player);
        new BukkitRunnable() {
            @Override
            public void run() {
                solo.show(player);
                team.show(player);
                trio.show(player);
                quarteto.show(player);
            }
        }.runTaskLater(Lobby.getPlugin(), 10L);
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        Player player = event.getPlayer();

        Services.getNpcService().updateText(solo, BED_WARS, SOLO);
        Services.getNpcService().updateText(team, BED_WARS, DUPLAS);
        Services.getNpcService().updateText(trio, BED_WARS, TRIOS);
        Services.getNpcService().updateText(quarteto, BED_WARS, QUARTETOS);
    }

    @EventHandler
    public void onInteract(NPCInteractEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getWhoClicked().getPlayer();

        player.playSound(player.getLocation(), Sound.NOTE_PLING, 2.0f, 2.0f);

        if (npc == solo) {
            PlayBedMenu.open(player, SOLO);
        } else if (npc == team) {
            PlayBedMenu.open(player, DUPLAS);
        } else if (npc == trio) {
            PlayBedMenu.open(player, TRIOS);
        } else if (npc == quarteto) {
            PlayBedMenu.open(player, QUARTETOS);
        }
    }
}
