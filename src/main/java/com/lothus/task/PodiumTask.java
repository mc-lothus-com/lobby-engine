package com.lothus.task;

import com.lothus.Lobby;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.sync.stats.data.type.DataType;
import com.lothus.sync.stats.platform.Platform;
import com.lothus.sync.stats.player.games.bedwars.BedPlayer;
import com.lothus.sync.stats.player.games.bedwars.stats.BedStats;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class PodiumTask extends BukkitRunnable {

    @Getter
    private static HashMap<UUID, BedPlayer> bedPlayers;

    @Getter
    private static HashMap<UUID, LothPlayer> lothPlayers;

    @Getter
    private static HashMap<UUID, BedStats> bedRanked;

    @Getter
    private static long updateIn;

    public PodiumTask() {
        bedPlayers = new HashMap<>();
        lothPlayers = new HashMap<>();
        bedRanked = new HashMap<>();
        runTaskTimerAsynchronously(Lobby.getPlugin(), 20L, (20L*60L) * 10L);
    }

    @Override
    public void run() {
        for (BedPlayer player : Platform.getDataPlayer().getLeagueRanking()) {
            LothPlayer lothPlayer = Core.getDataPlayer().get(player.getUniqueId());
            BedStats stats = Platform.getDataStats().getBedStats(DataType.BED_WARS_RANKED, player.getUniqueId());

            lothPlayers.put(player.getUniqueId(), lothPlayer);
            bedRanked.put(player.getUniqueId(), stats);

            bedPlayers.put(player.getUniqueId(), player);
        }
        updateIn = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10);
    }
}
