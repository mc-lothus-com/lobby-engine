package com.lothus.definitions.scoreboards;

import com.lothus.engines.sync.data.type.DataType;
import com.lothus.engines.sync.platform.Platform;
import com.lothus.engines.sync.player.games.bedwars.BedPlayer;
import com.lothus.engines.sync.player.games.bedwars.stats.BedStats;
import com.lothus.services.Services;
import com.mclothus.bukkit.api.scoreboard.TScoreboard;
import com.mclothus.core.Core;
import com.mclothus.core.servers.type.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BedWarsScoreboard extends TScoreboard {

    public BedWarsScoreboard(Player player) {
        super(
                player,
                "dummy",
                Core.getServerInfo().getConfiguration().getScoreboardTitle()
        );
    }

    public BedWarsScoreboard(UUID uniqueId) {
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
        BedPlayer bedPlayer = Platform.getBedPlatform().getBedPlayerController().getAccount(player.getUniqueId());
        BedStats bedSolo = Platform.getBedPlatform().getBedPlayerController().get(DataType.BED_WARS_SOLO, player.getUniqueId());
        BedStats bedTeam = Platform.getBedPlatform().getBedPlayerController().get(DataType.BED_WARS_TEAM, player.getUniqueId());

        setRow(1, "");
        setRow(2, "§fSeu nível: " + bedPlayer.getLevelColor().getColor() + "[" + bedPlayer.getLevel() + "✧]");
        setRow(3, "§fXP: §7(" + bedPlayer.getXp() + "/500)");
        setRow(4, "");
        setRow(5, "§aSolo:");
        setRow(6, " §fVitórias: §a" + bedSolo.getWins());
        setRow(7, " §fWinstreak: §a" + bedSolo.getCurrentWinstreak());
        setRow(8, "§aDuplas:");
        setRow(9, " §fVitórias: §a" + bedTeam.getWins());
        setRow(10, " §fWinstreak: §a" + bedTeam.getCurrentWinstreak());
        setRow(11, "");
        setRow(12, "§fCoins: §a" + bedPlayer.getCoins());
        setRow(13, "§fPlayers: §b" + players);
        setRow(14, "");
        setRow(15, Core.getServerInfo().getConfiguration().getScoreboardFooter());
    }

    @Override
    public void update() {

        int players = Services.getNpcService().getOnlineCount(
                ServerType.PROXY
        );
        BedPlayer bedPlayer = Platform.getBedPlatform().getBedPlayerController().getAccount(player.getUniqueId());
        BedStats bedSolo = Platform.getBedPlatform().getBedPlayerController().get(DataType.BED_WARS_SOLO, player.getUniqueId());
        BedStats bedTeam = Platform.getBedPlatform().getBedPlayerController().get(DataType.BED_WARS_TEAM, player.getUniqueId());

        setDisplayName(Core.getServerInfo().getConfiguration().getScoreboardTitle());
        setRow(1, "");
        setRow(2, "§fSeu nível: " + bedPlayer.getLevelColor().getColor() + "[" + bedPlayer.getLevel() + "✧]");
        setRow(3, "§fXP: §7(" + bedPlayer.getXp() + "/500)");
        setRow(4, "");
        setRow(5, "§aSolo:");
        setRow(6, " §fVitórias: §a" + bedSolo.getWins());
        setRow(7, " §fWinstreak: §a" + bedSolo.getCurrentWinstreak());
        setRow(8, "§aDuplas:");
        setRow(9, " §fVitórias: §a" + bedTeam.getWins());
        setRow(10, " §fWinstreak: §a" + bedTeam.getCurrentWinstreak());
        setRow(11, "");
        setRow(12, "§fCoins: §a" + bedPlayer.getCoins());
        setRow(13, "§fPlayers: §b" + players);
        setRow(14, "");
        setRow(15, Core.getServerInfo().getConfiguration().getScoreboardFooter());
    }
}
