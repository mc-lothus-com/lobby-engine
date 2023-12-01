package com.lothus.services.npc;

import com.lothus.Lobby;
import com.lothus.utils.LocationUtil;
import com.mclothus.bukkit.BukkitCore;
import com.mclothus.bukkit.events.UpdateEvent;
import com.mclothus.bukkit.utils.player.PlayerUtil;
import com.mclothus.core.Core;
import com.mclothus.core.games.GameInfo;
import com.mclothus.core.games.room.RoomType;
import com.mclothus.core.games.type.GameType;
import com.mclothus.core.servers.ServerInfo;
import com.mclothus.core.servers.type.ServerType;
import lombok.Getter;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.events.NPCInteractEvent;
import net.jitse.npclib.api.skin.Skin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.mclothus.core.servers.type.ServerType.*;

public class NPCService implements Listener {

    public NPC create(Location location, Skin skin, String... args) {
        NPC npc = Lobby.getNpcLib().createNPC(Arrays.asList(args));
        npc.setLocation(location);
        npc.setSkin(skin);
        npc.create();
        return npc;
    }

    public NPC create(Location location, Skin skin, List<String> args) {
        NPC npc = Lobby.getNpcLib().createNPC(args);
        npc.setLocation(location);
        npc.setSkin(skin);
        npc.create();
        return npc;
    }

    public List<String> updateText(NPC npc, int i) {
        List<String> list = new ArrayList<>();

        for (String text : npc.getText()) {
            if (text.endsWith("jogando agora!")) {
                list.add("§a" + i + " jogando agora!");
                continue;
            }
            list.add(text);
        }

        npc.setText(list);
        return list;
    }
    public List<String> updateText(NPC npc, GameType gameType, RoomType roomType) {
        List<String> list = new ArrayList<>();
        Integer onlineCount = getOnlineCount(gameType, roomType);

        for (String text : npc.getText()) {
            if (text.endsWith("jogando agora!")) {
                list.add("§a" + onlineCount + " jogando agora!");
                continue;
            }
            list.add(text);
        }

        npc.setText(list);
        return list;
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
    public int getOnlineCount(ServerType type) {
        return BukkitCore.getInstance().getOnlineCount(type);
    }
}
