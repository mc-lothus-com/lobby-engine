package com.lothus.definitions.scoreboards;

import com.lothus.core.Core;
import com.lothus.core.api.scoreboard.TScoreboard;
import com.lothus.core.servers.type.ServerType;
import com.lothus.services.Services;
import com.lothus.sync.stats.data.type.DataType;
import com.lothus.sync.stats.platform.Platform;
import com.lothus.sync.stats.player.games.bedwars.BedPlayer;
import com.lothus.sync.stats.player.games.bedwars.league.BedWarsLeague;
import com.lothus.sync.stats.player.games.bedwars.stats.BedStats;
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
        BedStats bedRank = Platform.getBedPlatform().getBedPlayerController().get(DataType.BED_WARS_RANKED, player.getUniqueId());

        setRow(1, "");
        setRow(2, "§fSeu nível: " + bedPlayer.getLevelColor().getColor() + "[" + bedPlayer.getLevel() + "✧]");
        setRow(3, "§fXP: §7(" + bedPlayer.getXp() + "/500)");
        setRow(4, "");
        setRow(5, "§aSolo:");
        setRow(6, " §fVitórias: §a" + bedSolo.getWins());
        setRow(7, " §fWinstreak: §a" + bedSolo.getCurrentWinstreak());
        setRow(8, "§aRanqueado:");
        setRow(9, " §fLiga: " + BedWarsLeague.getTag(bedPlayer.getLeague()));
        setRow(10, " §fPontos: §7" + bedPlayer.getPoints() + "/" + BedWarsLeague.nextLevel(bedPlayer.getLeague()).getPoints());
        setRow(11, " §fWinstreak: §a" + bedRank.getCurrentWinstreak());
        setRow(12, "");
        setRow(13, "§fCoins: §a" + bedPlayer.getCoins());
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
        BedStats bedRank = Platform.getBedPlatform().getBedPlayerController().get(DataType.BED_WARS_RANKED, player.getUniqueId());

        setDisplayName(Core.getServerInfo().getConfiguration().getScoreboardTitle());
        setRow(1, "");
        setRow(2, "§fSeu nível: " + bedPlayer.getLevelColor().getColor() + "[" + bedPlayer.getLevel() + "✧]");
        setRow(3, "§fXP: §7(" + bedPlayer.getXp() + "/500)");
        setRow(4, "");
        setRow(5, "§aSolo:");
        setRow(6, " §fVitórias: §a" + bedSolo.getWins());
        setRow(7, " §fWinstreak: §a" + bedSolo.getCurrentWinstreak());
        setRow(8, "§aRanqueado:");
        setRow(9, " §fLiga: " + BedWarsLeague.getTag(bedPlayer.getLeague()));
        setRow(10, " §fPontos: §7" + bedPlayer.getPoints() + "/" + BedWarsLeague.nextLevel(bedPlayer.getLeague()).getPoints());
        setRow(11, " §fWinstreak: §a" + bedRank.getCurrentWinstreak());
        setRow(12, "");
        setRow(13, "§fCoins: §a" + bedPlayer.getCoins());
        setRow(14, "");
        setRow(15, Core.getServerInfo().getConfiguration().getScoreboardFooter());
    }
}
