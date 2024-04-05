package com.lothus.player;

import com.lothus.Lobby;
import com.lothus.core.Core;
import com.lothus.core.api.scoreboard.TScoreboard;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.utils.bukkit.locations.type.LocationType;
import com.lothus.definitions.scoreboards.BedWarsScoreboard;
import com.lothus.definitions.scoreboards.LobbyScoreboard;
import com.lothus.definitions.scoreboards.SkyWarsScoreboard;
import com.lothus.instance.type.InstanceType;
import com.lothus.services.Services;
import com.lothus.sync.stats.platform.Platform;
import com.lothus.sync.stats.player.games.bedwars.BedPlayer;
import com.lothus.sync.stats.player.games.bedwars.league.BedWarsLeague;
import com.lothus.sync.stats.player.games.skywars.SkyPlayer;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.Setter;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.skin.Skin;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import java.util.Arrays;
import java.util.UUID;

@Getter @Setter
public class LobbyPlayer {

    private UUID uniqueId;

    private TScoreboard scoreboard;

    private NPC leagueNPC, statsNPC;

    public LobbyPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;

        this.scoreboard = (
                Lobby.getInstanceType() == InstanceType.MAIN ? new LobbyScoreboard(uniqueId) :
                Lobby.getInstanceType() == InstanceType.SKY_WARS ? new SkyWarsScoreboard(uniqueId) :
                new BedWarsScoreboard(uniqueId)
        );

        if (Lobby.getInstanceType() == InstanceType.MAIN)return;

        statsNPC();
        leagueNPC();

    }

    private void statsNPC() {
        int kills = 0;
        int wins = 0;

        GameProfile profile = ((CraftPlayer) Bukkit.getPlayer(uniqueId)).getProfile();
        Property property = null;
        try {
            property = profile.getProperties().get("textures").iterator().next();
        } catch (Exception e) {}

        if (Lobby.getInstanceType() == InstanceType.BED_WARS) {
            BedPlayer player = Platform.getBedPlatform().getBedPlayerController().getAccount(uniqueId);

            if (player.getLeague() == null) {
                player.setLeagueId(BedWarsLeague.INICIANTE.getId());
            }

            kills = player.getTotalKills();
            wins = player.getTotalWins();

        } else if (Lobby.getInstanceType() == InstanceType.SKY_WARS) {
            SkyPlayer skyPlayer = Platform.getSkyPlatform().getSkyPlayerController().getAccount(uniqueId);

            kills = skyPlayer.getTotalKills();
            wins = skyPlayer.getTotalWins();
        }

        statsNPC = Services.getNpcService().create(
                "S" +uniqueId.toString().substring(0,5),
                Lobby.getDefinitions().getLocation(LocationType.NPC_STATS),
                new Skin((property == null ? "" : property.getValue()), (property == null ? "" : property.getSignature())),
                "§2§lESTATÍSTICAS",
                "§7Veja as suas estatísticas!",
                "§7",
                "§fVítimas §a" + kills,
                "§fVitórias: §a" + wins,
                "§7",
                "§eClique para mais informações.");
    }
    private void leagueNPC() {
        GameProfile profile = ((CraftPlayer) Bukkit.getPlayer(uniqueId)).getProfile();

        Property property = null;
        try {
            property = profile.getProperties().get("textures").iterator().next();
        } catch (Exception e) {}

        BedPlayer player = Platform.getBedPlatform().getBedPlayerController().getAccount(uniqueId);

        if (player.getLeague() == null) {
            player.setLeagueId(BedWarsLeague.INICIANTE.getId());
        }

        player.getLeague();
        leagueNPC = Services.getNpcService().create(
                "L" + uniqueId.toString().substring(0,5),
                Lobby.getDefinitions().getLocation(LocationType.NPC_MISSIONS),
                new Skin((property == null ? "" : property.getValue()), (property == null ? "" : property.getSignature())),
                "§2§lLIGA",
                "§7Siga o seu progresso na liga!",
                "§7",
                "§fLiga: " + BedWarsLeague.getTag(player.getLeague()),
                "§fProgresso: " + progressBar(player.getPoints(), BedWarsLeague.nextLevel(player.getLeague()).getPoints()),
                "§fPontos: §7" + player.getPoints(),
                "§7",
                "§eClique para abrir.");
    }

    public void updateLeagueNPC() {
        BedPlayer player = Platform.getBedPlatform().getBedPlayerController().getAccount(uniqueId);

        leagueNPC.getText().set(3, "§fLiga: " + BedWarsLeague.getTag(player.getLeague()));
        leagueNPC.getText().set(4, "§fProgresso: " + progressBar(player.getPoints(), BedWarsLeague.nextLevel(player.getLeague()).getPoints()));
        leagueNPC.getText().set(5, "§fPontos: §7" + player.getPoints());

    }

    private static String progressBar(int valorAtual, int valorMaximo) {
        int progresso = (int) (((double) valorAtual / valorMaximo) * 10);

        StringBuilder barra = new StringBuilder("§f[");
        for (int i = 0; i < 10; i++) {
            if (i < progresso) {
                barra.append("§a▓");
            } else {
                barra.append("§7▓");
            }
        }
        double i = ((double) valorAtual / valorMaximo) * 100;
        barra.append("§f] §c(" + (Math.round(i)) + "%)");

        return barra.toString();
    }
}
