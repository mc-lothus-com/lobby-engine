package com.lothus.listeners;

import com.lothus.Lobby;
import com.lothus.engines.sync.platform.Platform;
import com.lothus.engines.sync.player.games.bedwars.BedPlayer;
import com.lothus.engines.sync.player.games.skywars.SkyPlayer;
import com.lothus.instance.type.InstanceType;
import com.lothus.player.LobbyPlayer;
import com.lothus.services.Services;
import com.mclothus.bukkit.api.actionbar.ActionBar;
import com.mclothus.bukkit.events.chat.CoreChatEvent;
import com.mclothus.bukkit.events.update.UpdateEvent;
import com.mclothus.core.Core;
import com.mclothus.core.player.LothPlayer;
import com.mclothus.core.player.group.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LobbyPlayer lobbyPlayer = new LobbyPlayer(player.getUniqueId());

        Lobby.getDefinitions().join(player);
        Services.getPlayerService().load(lobbyPlayer);

        event.setJoinMessage(null);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(CoreChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        String level = "";

        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        if (event.isCancelled()) {
            return;
        }

        if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.GER.ordinal())) {
            if (!Core.getServerInfo().getConfiguration().isChat()) {
                player.sendMessage("§cO chat está desativado no momento.");
                event.setCancelled(true);
                return;
            }
        }

        if (lothPlayer.getGroup().getRank().ordinal() <= Rank.MASTER.ordinal() || lothPlayer.getGroup().containsPermission("chat.color")) {
            message = message.replace("&", "§");
        }

        if (Lobby.getInstanceType() == InstanceType.SKY_WARS) {
            SkyPlayer skyPlayer = Platform.getSkyPlatform().getSkyPlayerController().getAccount(player.getUniqueId());
            level = skyPlayer.getLevelColor().getColor() + "[" + skyPlayer.getLevel() + "✧] ";
        } else if (Lobby.getInstanceType() == InstanceType.BED_WARS) {
            BedPlayer bedPlayer = Platform.getBedPlatform().getBedPlayerController().getAccount(player.getUniqueId());
            level = bedPlayer.getLevelColor().getColor() + "[" + bedPlayer.getLevel() + "✧] ";
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            LothPlayer on = Core.getPlayerController().get(p.getUniqueId());
            if (!on.getPrefs().isChat())continue;

            if (lothPlayer.getSocial().getFake().getName().equals(lothPlayer.getName())) {
                p.sendMessage(level + lothPlayer.getMedal().getColor() + lothPlayer.getMedal().getSymbol() + " " + (lothPlayer.getGroup().getTag() == Rank.MEMBRO ? "§7" : lothPlayer.getGroup().getTag().getColor() + "§l" + lothPlayer.getGroup().getTag().getName().toUpperCase() + " " + lothPlayer.getGroup().getTag().getColor()) + lothPlayer.getName() + ": §7" + message);
            } else {
                p.sendMessage(level + lothPlayer.getMedal().getColor() + lothPlayer.getMedal().getSymbol() + " " + (lothPlayer.getSocial().getFake().getRank() == Rank.MEMBRO ? "§7" : lothPlayer.getSocial().getFake().getRank().getColor() + "§l" + lothPlayer.getSocial().getFake().getRank().getName().toUpperCase() + " " + lothPlayer.getSocial().getFake().getRank().getColor()) + lothPlayer.getSocial().getFake().getName() + ": §7" + message);
            }
        }
    }

    @EventHandler
    public void onPlayerToogleSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        Player passanger = (Player) player.getPassenger();

        if (passanger == null)return;

        player.eject();

        Vector direction = player.getLocation()
                .getDirection()
                .clone()
                .multiply(1.5);

        passanger.setVelocity(direction);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (event.getTo().getY() <= -10) {
            player.teleport(Lobby.getDefinitions().getSpawn());
            return;
        }
    }

    @EventHandler
    public void onPlayerUpdate(UpdateEvent event) {
        Player player = event.getPlayer();
        LothPlayer lp = Core.getPlayerController().get(player.getUniqueId());
        LobbyPlayer lobbyPlayer = Services.getPlayerService().get(player.getUniqueId());

        lobbyPlayer.getScoreboard().update();

        for (Entity entity : player.getNearbyEntities(3,3,3)) {
            if (entity instanceof Player) {
                Player p = (Player) entity;
                LothPlayer lothPlayer = Core.getPlayerController().get(p.getUniqueId());
                if (lothPlayer.getPrefs().isVanish()) {
                    if (lp.getGroup().getRank().ordinal() <= Rank.AJD.ordinal()) {
                        ActionBar.sendActionBar(player, lothPlayer.getGroup().getTag().getColor() + lothPlayer.getName() + "§a está no modo §2§lADMIN§a.");
                    }
                }
            }
        }

        Player passanger = (Player) player.getPassenger();
        if (passanger == null)return;
        if (!(player.getPassenger() instanceof Player))return;

        ActionBar.sendActionBar(player, "§aAperte §2§lSHIFT §apara jogar seu amigo.");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Services.getPlayerService().unload(event.getPlayer().getUniqueId());
    }
}
