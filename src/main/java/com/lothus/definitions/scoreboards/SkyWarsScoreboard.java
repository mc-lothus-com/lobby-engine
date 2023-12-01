package com.lothus.definitions.scoreboards;

import com.lothus.engines.sync.data.type.DataType;
import com.lothus.engines.sync.platform.Platform;
import com.lothus.engines.sync.player.games.bedwars.BedPlayer;
import com.lothus.engines.sync.player.games.bedwars.stats.BedStats;
import com.lothus.engines.sync.player.games.skywars.SkyPlayer;
import com.lothus.engines.sync.player.games.skywars.stats.SkyStats;
import com.lothus.services.Services;
import com.mclothus.bukkit.api.scoreboard.TScoreboard;
import com.mclothus.core.Core;
import com.mclothus.core.servers.ServerInfo;
import com.mclothus.core.servers.type.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class SkyWarsScoreboard extends TScoreboard {

    public SkyWarsScoreboard(Player player) {
        super(
                player,
                "dummy",
                Core.getServerInfo().getConfiguration().getScoreboardTitle()
        );
    }

    public SkyWarsScoreboard(UUID uniqueId) {
        super(
                Bukkit.getPlayer(uniqueId),
                "dummy",
                Core.getServerInfo().getConfiguration().getScoreboardTitle()
        );
    }

    @Override
    public void create() {

        int players = Services.getNpcService().getOnlineCount(
                ServerType.PROXY
        );

        SkyPlayer skyPlayer = Platform.getSkyPlatform().getSkyPlayerController().getAccount(player.getUniqueId());
        SkyStats skySolo = Platform.getSkyPlatform().getSkyPlayerController().get(DataType.SKY_WARS_SOLO, player.getUniqueId());
        SkyStats skyTeam = Platform.getSkyPlatform().getSkyPlayerController().get(DataType.SKY_WARS_TEAM, player.getUniqueId());

        setRow(1, "");
        setRow(2, "§fSeu nível: " + skyPlayer.getLevelColor().getColor() + "[" + skyPlayer.getLevel() + "✧]");
        setRow(3, "§fXP: §7(" + skyPlayer.getXp() + "/500)");
        setRow(4, "");
        setRow(5, "§aSolo:");
        setRow(6, " §fVitórias: §a" + skySolo.getWins());
        setRow(7, " §fWinstreak: §a" + skySolo.getCurrentWinstreak());
        setRow(8, "§aDuplas:");
        setRow(9, " §fVitórias: §a" + skyTeam.getWins());
        setRow(10, " §fWinstreak: §a" + skyTeam.getCurrentWinstreak());
        setRow(11, "");
        setRow(12, "§fCoins: §a" + skyPlayer.getCoins());
        setRow(13, "§fPlayers: §b" + players);
        setRow(14, "");
        setRow(15, "§awww.mc-lothus.com");
    }

    @Override
    public void update() {
        SkyPlayer skyPlayer = Platform.getSkyPlatform().getSkyPlayerController().getAccount(player.getUniqueId());
        SkyStats skySolo = Platform.getSkyPlatform().getSkyPlayerController().get(DataType.SKY_WARS_SOLO, player.getUniqueId());
        SkyStats skyTeam = Platform.getSkyPlatform().getSkyPlayerController().get(DataType.SKY_WARS_TEAM, player.getUniqueId());

        int players = Services.getNpcService().getOnlineCount(
                ServerType.PROXY
        );

        setDisplayName(Core.getServerInfo().getConfiguration().getScoreboardTitle());
        setRow(1, "");
        setRow(2, "§fSeu nível: " + skyPlayer.getLevelColor().getColor() + "[" + skyPlayer.getLevel() + "✧]");
        setRow(3, "§fXP: §7(" + skyPlayer.getXp() + "/500)");
        setRow(4, "");
        setRow(5, "§aSolo:");
        setRow(6, " §fVitórias: §a" + skySolo.getWins());
        setRow(7, " §fWinstreak: §a" + skySolo.getCurrentWinstreak());
        setRow(8, "§aDuplas:");
        setRow(9, " §fVitórias: §a" + skyTeam.getWins());
        setRow(10, " §fWinstreak: §a" + skyTeam.getCurrentWinstreak());
        setRow(11, "");
        setRow(12, "§fCoins: §a" + skyPlayer.getCoins());
        setRow(13, "§fPlayers: §b" + players);
        setRow(14, "");
        setRow(15, Core.getServerInfo().getConfiguration().getScoreboardFooter());
    }
}
