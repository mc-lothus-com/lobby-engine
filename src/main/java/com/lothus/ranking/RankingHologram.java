package com.lothus.ranking;

import com.lothus.engines.sync.data.type.DataType;
import com.lothus.engines.sync.platform.Platform;
import com.lothus.engines.sync.player.games.bedwars.BedPlayer;
import com.lothus.engines.sync.player.games.bedwars.stats.BedStats;
import com.lothus.engines.sync.player.games.skywars.SkyPlayer;
import com.lothus.engines.sync.player.games.skywars.stats.SkyStats;
import com.mclothus.bukkit.api.hologram.Hologram;
import com.mclothus.core.Core;
import com.mclothus.core.player.LothPlayer;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
public class RankingHologram extends Hologram {

    private DataType dataType;
    private String fieldName;
    private String rankName;

    public RankingHologram(@NonNull Location location, DataType dataType, String fieldName, String rankName) {
        super(location);
        this.dataType = dataType;
        this.fieldName = fieldName;
        this.rankName = rankName;
    }

    public void update() throws NoSuchFieldException, IllegalAccessException {
        List<String> lines = new ArrayList<>();
        lines.add("§a§lTOP 10 " + rankName.toUpperCase() + "!");

        int position = 1;

        if (dataType == DataType.SKY_WARS_ACCOUNT) {
            for (SkyPlayer skyPlayer : Platform.getDataPlayer().getSkyRanking(dataType, fieldName)) {
                if (position > 10)
                    continue;

                Field field = skyPlayer.getClass().getDeclaredField(fieldName);
                LothPlayer lothPlayer = Core.getDataPlayer().get(skyPlayer.getUniqueId());

                if (lothPlayer == null) continue;

                field.setAccessible(true);
                lines.add("§2§l" + position + ". §r§7" + lothPlayer.getGroup().getRank().getColor() + lothPlayer.getName() + " §7- §a" + (rankName.equalsIgnoreCase("RANKS") ? skyPlayer.getLevelColor().getColor() + "§l" + skyPlayer.getLevelColor().name() : field.get(skyPlayer)));
                position++;
            }
        } else if (dataType.name().startsWith("SKY_WARS_")) {
            for (SkyStats skyPlayer : Platform.getDataStats().getSkyRanking(dataType, fieldName)) {
                if (position > 10)
                    continue;

                Field field = skyPlayer.getClass().getDeclaredField(fieldName);
                LothPlayer lothPlayer = Core.getDataPlayer().get(skyPlayer.getUniqueId());

                if (lothPlayer == null) continue;

                field.setAccessible(true);
                lines.add("§2§l" + position + ". §r§7" + lothPlayer.getGroup().getRank().getColor() + lothPlayer.getName() + " §7- §a" + field.get(skyPlayer));
                position++;
            }
        } else if (dataType == DataType.BED_WARS_ACCOUNT) {
            for (BedPlayer skyPlayer : Platform.getDataPlayer().getBedRanking(dataType, fieldName)) {
                if (position > 10)
                    continue;

                Field field = skyPlayer.getClass().getDeclaredField(fieldName);
                LothPlayer lothPlayer = Core.getDataPlayer().get(skyPlayer.getUniqueId());

                if (lothPlayer == null) continue;

                field.setAccessible(true);
                lines.add("§2§l" + position + ". §r§7" + lothPlayer.getGroup().getRank().getColor() + lothPlayer.getName() + " §7- §a" + (rankName.equalsIgnoreCase("RANKS") ? skyPlayer.getLevelColor().getColor() + "§l" + skyPlayer.getLevelColor().name() : field.get(skyPlayer)));
                position++;
            }
        } else if (dataType.name().startsWith("BED_WARS_")) {
            for (BedStats skyPlayer : Platform.getDataStats().getBedRanking(dataType, fieldName)) {
                if (position > 10)
                    continue;

                Field field = skyPlayer.getClass().getDeclaredField(fieldName);
                LothPlayer lothPlayer = Core.getDataPlayer().get(skyPlayer.getUniqueId());

                if (lothPlayer == null) continue;

                field.setAccessible(true);
                lines.add("§2§l" + position + ". §r§7" + lothPlayer.getGroup().getRank().getColor() + lothPlayer.getName() + " §7- §a" + field.get(skyPlayer));
                position++;
            }
        }
        setText(lines);
    }
}
