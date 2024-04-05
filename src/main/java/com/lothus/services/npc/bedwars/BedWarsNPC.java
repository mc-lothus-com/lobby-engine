package com.lothus.services.npc.bedwars;

import com.lothus.Lobby;
import com.lothus.core.event.update.UpdateEvent;
import com.lothus.core.utils.bukkit.locations.type.LocationType;
import com.lothus.menus.play.bedwars.PlayBedMenu;
import com.lothus.player.LobbyPlayer;
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

import static com.lothus.core.games.room.RoomType.*;
import static com.lothus.core.games.type.GameType.BED_WARS;

public class BedWarsNPC implements Listener {

    @Getter
    private NPC solo, team, trio, quarteto, ranked;

    private JavaPlugin plugin;

    public BedWarsNPC(JavaPlugin plugin) {
        this.plugin = plugin;
        solo = Services.getNpcService().create("bwsolo", Lobby.getDefinitions().getLocation(LocationType.NPC_SOLO), new Skin(plugin.getConfig().getString("config.skins.npcs.bedwars.solo.value"), plugin.getConfig().getString("config.skins.npcs.bedwars.solo.signature")), "§2§lSOLO", "§a0 jogando!");
        team = Services.getNpcService().create("bwteam", Lobby.getDefinitions().getLocation(LocationType.NPC_TEAM), new Skin(plugin.getConfig().getString("config.skins.npcs.bedwars.team.value"), plugin.getConfig().getString("config.skins.npcs.bedwars.team.signature")),"§2§lDUPLA", "§a0 jogando!");
        trio = Services.getNpcService().create("bwtrio", Lobby.getDefinitions().getLocation(LocationType.NPC_TRIO), new Skin(plugin.getConfig().getString("config.skins.npcs.bedwars.trio.value"), plugin.getConfig().getString("config.skins.npcs.bedwars.trio.signature")), "§2§lTRIO", "§a0 jogando!");
        quarteto = Services.getNpcService().create("bwquar", Lobby.getDefinitions().getLocation(LocationType.NPC_QUARTETO), new Skin(plugin.getConfig().getString("config.skins.npcs.bedwars.quarteto.value"), plugin.getConfig().getString("config.skins.npcs.bedwars.quarteto.signature")), "§2§lQUARTETO", "§a0 jogando!");
        ranked = Services.getNpcService().create("bwrank", Lobby.getDefinitions().getLocation(LocationType.NPC_RANKED), new Skin(plugin.getConfig().getString("config.skins.npcs.bedwars.ranked.value"), plugin.getConfig().getString("config.skins.npcs.bedwars.ranked.signature")), "§2§lRANQUEADO", "§a0 jogando!");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LobbyPlayer lobbyPlayer = Services.getPlayerService().get(player.getUniqueId());

        Services.getNpcService().show(player, solo);
        Services.getNpcService().show(player, team);
        Services.getNpcService().show(player, trio);
        Services.getNpcService().show(player, quarteto);
        Services.getNpcService().show(player, ranked);
        Services.getNpcService().show(player, lobbyPlayer.getLeagueNPC());
        Services.getNpcService().show(player, lobbyPlayer.getStatsNPC());
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        Player player = event.getPlayer();

        Services.getNpcService().updateText(solo, BED_WARS, SOLO);
        Services.getNpcService().updateText(team, BED_WARS, DUPLAS);
        Services.getNpcService().updateText(trio, BED_WARS, TRIOS);
        Services.getNpcService().updateText(quarteto, BED_WARS, QUARTETOS);
        Services.getNpcService().updateText(ranked, BED_WARS, RANQUEADO);
    }

    @EventHandler
    public void onInteract(NPCInteractEvent event) {
        Player player = event.getWhoClicked();
        NPC npc = event.getNPC();

        player.playSound(player.getLocation(), Sound.NOTE_PLING, 2.0f, 2.0f);

        if (npc == solo) {
            PlayBedMenu.open(player, SOLO);
        } else if (npc == team) {
            PlayBedMenu.open(player, DUPLAS);
        } else if (npc == trio) {
            PlayBedMenu.open(player, TRIOS);
        } else if (npc == quarteto) {
            PlayBedMenu.open(player, QUARTETOS);
        } else if (npc == ranked) {
            PlayBedMenu.open(player, RANQUEADO);
        }
    }
}
