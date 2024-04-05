package com.lothus.services.player;

import com.lothus.player.LobbyPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerService {

    private HashMap<UUID, LobbyPlayer> players = new HashMap<>();

    public void load(LobbyPlayer lobbyPlayer) {
        players.put(lobbyPlayer.getUniqueId(), lobbyPlayer);
    }

    public void unload(UUID uniqueId) {
        players.remove(uniqueId);
    }

    public LobbyPlayer get(UUID uniqueId) {
        return players.get(uniqueId);
    }

    public List<LobbyPlayer> getAll() {
        List<LobbyPlayer> c = new ArrayList<>();
        for (LobbyPlayer lobbyPlayer : players.values()) {
            c.add(lobbyPlayer);
        }
        return c;
    }
}
