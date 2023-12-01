package com.lothus;

import com.henryfabio.minecraft.inventoryapi.manager.InventoryManager;
import com.lothus.definitions.Definitions;
import com.lothus.instance.Instance;
import com.lothus.instance.type.InstanceType;
import com.lothus.services.npc.bedwars.BedWarsNPC;
import com.lothus.services.npc.lobby.LobbyNPC;
import com.lothus.services.npc.skywars.SkyWarsNPC;
import com.mclothus.bukkit.commands.loader.BukkitCommandLoader;
import com.mclothus.bukkit.listeners.loader.ListenerLoader;
import lombok.Getter;
import lombok.Setter;
import net.jitse.npclib.NPCLib;

public class Lobby extends Instance {

    @Getter @Setter
    private static Definitions definitions;

    @Getter @Setter
    private static NPCLib npcLib;

    @Override
    public void load() {
        saveDefaultConfig();
    }

    @Override
    public void enable() {
        npcLib = new NPCLib(getPlugin());
        InventoryManager.enable(this);
        BukkitCommandLoader.loadCommands(getPlugin(), "com.lothus.commands");
        ListenerLoader.loadListeners(getPlugin(), "com.lothus.menus");
        ListenerLoader.loadListeners(getPlugin(), "com.lothus.listeners");
        if (getInstanceType() == InstanceType.MAIN) {
            getServer().getPluginManager().registerEvents(new LobbyNPC(getPlugin()), getPlugin());
        } else if (getInstanceType() == InstanceType.SKY_WARS) {
            getServer().getPluginManager().registerEvents(new SkyWarsNPC(getPlugin()), getPlugin());
        } else if (getInstanceType() == InstanceType.BED_WARS) {
            getServer().getPluginManager().registerEvents(new BedWarsNPC(getPlugin()), getPlugin());
        }
    }

    @Override
    public void disable() {

    }
}
