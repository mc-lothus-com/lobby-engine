package com.lothus.definitions.scoreboards;

import com.lothus.services.Services;
import com.mclothus.bukkit.api.scoreboard.TScoreboard;
import com.mclothus.core.Core;
import com.mclothus.core.player.LothPlayer;
import com.lothus.sync.clans.utils.LevelColor;
import com.mclothus.core.servers.type.ServerType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LobbyScoreboard extends TScoreboard {

    public LobbyScoreboard(Player player) {
        super(
                player,
                "dummy",
                Core.getServerInfo().getConfiguration().getScoreboardTitle()
        );
    }

    public LobbyScoreboard(UUID uniqueId) {
        super(
                Bukkit.getPlayer(uniqueId),
                "dummy",
                Core.getServerInfo().getConfiguration().getScoreboardTitle()
        );
    }

    @Override
    public void create() {
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        int players = Services.getNpcService().getOnlineCount(
                ServerType.PROXY
        );

        LevelColor color = LevelColor.getLevelColor(lothPlayer.getLevel());

        setDisplayName(Core.getServerInfo().getConfiguration().getScoreboardTitle());

        setRow(1, "");
        setRow(2, "§fRank: " + lothPlayer.getGroup().getRank().getColor() + lothPlayer.getGroup().getRank().getName());
        setRow(3, "§fLiga: " + color.getColor() + "(" + color.getSymbol() + ")");
        setRow(4, "");
        setRow(5, "§fPlayers: §b" + players);
        setRow(6, "§fLobby: §a#" + Core.getServerInfo().getId());
        setRow(7, "");
        setRow(8, Core.getServerInfo().getConfiguration().getScoreboardFooter());
    }

    @Override
    public void update() {
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        int players = Services.getNpcService().getOnlineCount(
                ServerType.PROXY
        );

        LevelColor color = LevelColor.getLevelColor(lothPlayer.getLevel());

        setDisplayName(Core.getServerInfo().getConfiguration().getScoreboardTitle());

        setRow(1, "");
        setRow(2, "§fRank: " + lothPlayer.getGroup().getRank().getColor() + lothPlayer.getGroup().getRank().getName());
        setRow(3, "§fLiga: " + color.getColor() + "(" + color.getSymbol() + ")");
        setRow(4, "");
        setRow(5, "§fPlayers: §b" + players);
        setRow(6, "§fLobby: §a#" + Core.getServerInfo().getId());
        setRow(7, "");
        setRow(8, Core.getServerInfo().getConfiguration().getScoreboardFooter());
    }
}
