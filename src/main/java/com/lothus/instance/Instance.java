package com.lothus.instance;

import com.lothus.Lobby;
import com.lothus.core.Core;
import com.lothus.core.servers.type.ServerType;
import com.lothus.definitions.Definitions;
import com.lothus.instance.type.InstanceType;
import com.lothus.sync.stats.Sync;
import com.lothus.wadgets.sync.SyncWadget;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public abstract class Instance extends JavaPlugin {

    @Getter
    private static Instance plugin;

    @Getter @Setter
    private static InstanceType instanceType;

    public abstract void load();
    public abstract void enable();
    public abstract void disable();

    @Override
    public void onLoad() {
        plugin = this;

        if (new File(getDataFolder().getPath() + "/world").exists()) {
            paste();
        }

        load();
    }

    @Override
    public void onEnable() {
        Sync.loadLobby(this);
        new SyncWadget(this);
        setInstanceType((Core.getServerInfo().getType() == ServerType.LOBBY ? InstanceType.MAIN : Core.getServerInfo().getType() == ServerType.LOBBY_SKYWARS ? InstanceType.SKY_WARS : InstanceType.BED_WARS));
        Lobby.setDefinitions(new Definitions());
        config();
        enable();
        run();
    }

    @Override
    public void onDisable() {
        disable();
    }


    private void paste() {
        try {
            File dir = new File(getDataFolder().getPath() + "/world/");
            File to = new File("/home/container/world");

            FileUtils.deleteDirectory(to);

            if (!to.exists()) {
                to.mkdirs();
            }

            FileUtils.copyDirectory(dir, to);
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    private void config() {
        World world = Bukkit.getWorld("world");

        world.setGameRuleValue("announceAdvancements", "false");
        world.setGameRuleValue("blockExplosionDropDecay", "false");
        world.setGameRuleValue("commandModificationBlockLimit", "0");
        world.setGameRuleValue("doFireTick", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("randomTickSpeed", "0");
        world.setGameRuleValue("showDeathMessages", "false");

        world.setTime(0);
        world.getEntities().forEach(Entity::remove);
        world.setSpawnLocation(Lobby.getDefinitions().getSpawn().getBlockX(),Lobby.getDefinitions().getSpawn().getBlockY(),Lobby.getDefinitions().getSpawn().getBlockZ());

        world.setAutoSave(false);
        world.setPVP(false);
        world.setAnimalSpawnLimit(0);
        world.setMonsterSpawnLimit(0);
        world.setWeatherDuration(0);
    }
    private void run() {
        new BukkitRunnable() {
            public void run() {
                int random = new Random().nextInt(Lobby.getDefinitions().getMessages().size());
                String message = Lobby.getDefinitions().getMessages().get(random);
                Bukkit.broadcastMessage(message.replace("&", "§"));
            }
        }.runTaskTimer(this, 0L, 20*TimeUnit.MINUTES.toSeconds(5));
    }
}
