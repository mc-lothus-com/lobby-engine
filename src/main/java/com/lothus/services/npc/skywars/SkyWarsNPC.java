package com.lothus.services.npc.skywars;

import com.lothus.Lobby;
import com.lothus.definitions.locations.type.LocationType;
import com.lothus.menus.play.skywars.PlaySkyMenu;
import com.lothus.player.LobbyPlayer;
import com.lothus.services.Services;
import com.lothus.utils.LocationUtil;
import com.mclothus.bukkit.api.skin.SkinLoader;
import com.mclothus.bukkit.events.PlayerChangeSkinEvent;
import com.mclothus.bukkit.events.UpdateEvent;
import com.mclothus.bukkit.utils.player.PlayerUtil;
import com.mclothus.core.Core;
import com.mclothus.core.player.LothPlayer;
import com.mclothus.core.servers.ServerInfo;
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
import static com.mclothus.core.games.type.GameType.SKY_WARS;
import static com.mclothus.core.servers.type.ServerType.LOBBY;

public class SkyWarsNPC implements Listener {

    @Getter
    private NPC solo, team, stats;

    private JavaPlugin plugin;

    public SkyWarsNPC(JavaPlugin plugin) {
        this.plugin = plugin;
        solo = Services.getNpcService().create(Lobby.getDefinitions().getLocation(LocationType.NPC_SOLO), new Skin(plugin.getConfig().getString("config.skins.npcs.skywars.solo.value"), plugin.getConfig().getString("config.skins.npcs.skywars.solo.signature")), "§2§lSOLOS", "§a0 jogando agora!");
        team = Services.getNpcService().create(Lobby.getDefinitions().getLocation(LocationType.NPC_TEAM), new Skin(plugin.getConfig().getString("config.skins.npcs.skywars.team.value"), plugin.getConfig().getString("config.skins.npcs.skywars.team.signature")),"§2§lDUPLAS", "§a0 jogando agora!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        if (solo.isShown(player)) {
            solo.hide(player);
        } else if (team.isShown(player)) {
            team.hide(player);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    solo.show(player);
                    team.show(player);
                } catch (Exception e) {

                }
            }
        }.runTaskLater(plugin, 10L);
    }

    @EventHandler
    public void onChangeSkin(PlayerChangeSkinEvent event) {
        Player player = event.getPlayer();
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        if (solo.isShown(player)) {
            solo.hide(player);
        } else if (team.isShown(player)) {
            team.hide(player);
        } else if (stats.isShown(player)) {
            stats.hide(player);
        }


        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    solo.show(player);
                    team.show(player);
                } catch (Exception e) {

                }
            }
        }.runTaskLater(plugin, 10L);
    }


    @EventHandler
    public void onUpdate(UpdateEvent event) {
        Player player = event.getPlayer();

        Services.getNpcService().updateText(solo, SKY_WARS, SOLO);
        Services.getNpcService().updateText(team, SKY_WARS, DUPLAS);
    }

    @EventHandler
    public void onInteract(NPCInteractEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getWhoClicked().getPlayer();

        player.playSound(player.getLocation(), Sound.NOTE_PLING, 2.0f, 2.0f);

        if (npc == solo) {
            PlaySkyMenu.open(player, SOLO);
        } else if (npc == team) {
            PlaySkyMenu.open(player, DUPLAS);
        }
    }
}
