package com.lothus.services.npc.skywars;

import com.lothus.Lobby;
import com.lothus.core.event.update.UpdateEvent;
import com.lothus.core.utils.bukkit.locations.type.LocationType;
import com.lothus.menus.play.skywars.PlaySkyMenu;
import com.lothus.services.Services;
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

import static com.lothus.core.games.room.RoomType.DUPLAS;
import static com.lothus.core.games.room.RoomType.SOLO;
import static com.lothus.core.games.type.GameType.SKY_WARS;

public class SkyWarsNPC implements Listener {

    @Getter
    private NPC solo, team, stats;

    private JavaPlugin plugin;

    public SkyWarsNPC(JavaPlugin plugin) {
        this.plugin = plugin;
        solo = Services.getNpcService().create("skywars-solo", Lobby.getDefinitions().getLocation(LocationType.NPC_SOLO), new Skin(plugin.getConfig().getString("config.skins.npcs.skywars.solo.value"), plugin.getConfig().getString("config.skins.npcs.skywars.solo.signature")), "§2§lSOLOS", "§a0 jogando agora!");
        team = Services.getNpcService().create("skywars-team", Lobby.getDefinitions().getLocation(LocationType.NPC_TEAM), new Skin(plugin.getConfig().getString("config.skins.npcs.skywars.team.value"), plugin.getConfig().getString("config.skins.npcs.skywars.team.signature")),"§2§lDUPLAS", "§a0 jogando agora!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        new BukkitRunnable() {
            @Override
            public void run() {
                Services.getNpcService().show(player, solo);
                Services.getNpcService().show(player, team);
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
        Player player = event.getWhoClicked();

        player.playSound(player.getLocation(), Sound.NOTE_PLING, 2.0f, 2.0f);

        if (npc == solo) {
            PlaySkyMenu.open(player, SOLO);
        } else if (npc == team) {
            PlaySkyMenu.open(player, DUPLAS);
        }
    }
}
