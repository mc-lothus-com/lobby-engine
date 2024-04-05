package com.lothus.services;

import com.lothus.services.npc.NPCService;
import com.lothus.services.player.PlayerService;
import lombok.Getter;

public class Services {

    @Getter
    private static NPCService npcService = new NPCService();

    @Getter
    private static PlayerService playerService = new PlayerService();

}
