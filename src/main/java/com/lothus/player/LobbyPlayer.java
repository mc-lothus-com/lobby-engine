package com.lothus.player;

import com.lothus.Lobby;
import com.lothus.definitions.scoreboards.BedWarsScoreboard;
import com.lothus.definitions.scoreboards.LobbyScoreboard;
import com.lothus.definitions.scoreboards.SkyWarsScoreboard;
import com.lothus.instance.type.InstanceType;
import com.mclothus.bukkit.api.scoreboard.TScoreboard;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class LobbyPlayer {

    private UUID uniqueId;

    private TScoreboard scoreboard;

    public LobbyPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;

        this.scoreboard = (
                Lobby.getInstanceType() == InstanceType.MAIN ? new LobbyScoreboard(uniqueId) :
                Lobby.getInstanceType() == InstanceType.SKY_WARS ? new SkyWarsScoreboard(uniqueId) :
                new BedWarsScoreboard(uniqueId)
        );
    }
}
