package com.lothus.services.npc;

import com.lothus.Lobby;
import com.lothus.core.Core;
import com.lothus.core.games.GameInfo;
import com.lothus.core.games.room.RoomType;
import com.lothus.core.games.type.GameType;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.servers.type.ServerType;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.skin.Skin;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;


public class NPCService {

    public void show(Player player, NPC npc) {
        npc.show(player);
    }

    public void hide(Player player, NPC npc) {
        npc.show(player);
    }

    public NPC create(String id, Location location, Skin skin, String... args) {
        NPC npc = Lobby.getNpcLib().createNPC(Arrays.asList(args));
        npc.setLocation(location);
        npc.setSkin(skin);
        npc.create();
        return npc;
    }

    public void updateText(NPC npc, int i) {
        int line = 0;
        for (String text : npc.getText()) {
            if (text.endsWith("jogando!")) {
                line = npc.getText().indexOf(text);
            }
        }

        npc.getText().set(line, "§a" + i + " jogando!");
    }
    public void updateText(NPC npc, GameType gameType, RoomType roomType) {
        Integer onlineCount = getOnlineCount(gameType, roomType);
        updateText(npc, onlineCount);
    }

    public int getOnlineCount(GameType gameType, RoomType roomType) {
        int i = 0;

        for (GameInfo gameInfo : Core.getGameController().getAll()) {
            if (gameInfo.getType() != gameType) continue;
            if (gameInfo.getRoomType() != roomType) continue;
            i = i + gameInfo.getPlayers();
        }
        return i;
    }


    public Integer getOnlineCount(ServerType serverType) {
        int players = 0;
        for (ServerInfo serverInfo : Core.getServerController().getAll()) {
            if (serverInfo == null) {
                continue;
            }
            if (serverInfo.getType() == serverType) {
                players += serverInfo.getPlayers();
            }
        }
        return players;
    }
}
