package com.lothus.listeners;

import com.lothus.Lobby;
import com.lothus.bukkit.events.chat.CoreChatEvent;
import com.lothus.core.Core;
import com.lothus.core.api.actionbar.ActionBar;
import com.lothus.core.event.update.UpdateEvent;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.instance.type.InstanceType;
import com.lothus.player.LobbyPlayer;
import com.lothus.services.Services;
import com.lothus.sync.stats.data.type.DataType;
import com.lothus.sync.stats.platform.Platform;
import com.lothus.sync.stats.player.games.bedwars.BedPlayer;
import com.lothus.sync.stats.player.games.bedwars.league.BedWarsLeague;
import com.lothus.sync.stats.player.games.bedwars.league.rewards.BedWarsReward;
import com.lothus.sync.stats.player.games.bedwars.league.rewards.type.RewardType;
import com.lothus.sync.stats.player.games.skywars.SkyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LobbyPlayer lobbyPlayer = new LobbyPlayer(player.getUniqueId());

        lobbyPlayer.getScoreboard().create();;

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

        if (player.getPassenger() instanceof Player) {
            Player passanger = (Player) player.getPassenger();

            if (passanger == null) return;

            player.eject();

            Vector direction = player.getLocation()
                    .getDirection()
                    .clone()
                    .multiply(1.5);

            passanger.setVelocity(direction);
        }
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

        if (lobbyPlayer == null)return;

        lobbyPlayer.getScoreboard().update();

        for (Entity entity : player.getNearbyEntities(3,3,3)) {
            if (entity instanceof Player) {
                Player p = (Player) entity;
                LothPlayer lothPlayer = Core.getPlayerController().get(p.getUniqueId());
                if (lothPlayer.getPrefs().isVanish()) {
                    if (lp.getGroup().getRank().ordinal() <= Rank.TRIAL.ordinal()) {
                        ActionBar.sendActionBar(player, lothPlayer.getGroup().getTag().getColor() + lothPlayer.getName() + "§a está no modo §2§lADMIN§a.");
                    }
                }
            }
        }

        if (Lobby.getInstanceType() == InstanceType.BED_WARS) {
            BedPlayer bedPlayer = Platform.getBedPlatform().getBedPlayerController().getAccount(player.getUniqueId());

            if (bedPlayer == null)return;

            BedWarsLeague nextLeague = BedWarsLeague.nextLevel(bedPlayer.getLeague());

            if (bedPlayer.getPoints() < nextLeague.getPoints()) return;

            bedPlayer.setPoints(bedPlayer.getPoints() - nextLeague.getPoints());
            bedPlayer.setLeagueId(nextLeague.getId());

            for (BedWarsReward reward : nextLeague.getRewards()) {
                switch (reward.getType()){
                    case XP:
                        bedPlayer.setXp(bedPlayer.getXp() + reward.getAmount());
                        break;
                    case COINS:
                        bedPlayer.setCoins(bedPlayer.getCoins() + reward.getAmount());
                        break;
                    case CASH:
                        lp.setCash(lp.getCash() + reward.getAmount());
                        break;
                    case TAG:
                        lp.getGroup().addPermission("rank." + reward.getValue().toLowerCase(), System.currentTimeMillis() + TimeUnit.DAYS.toMillis(reward.getAmount()));
                }
            }

            player.sendMessage("");
            player.sendMessage("§2§lBED WARS §7- §a§lLIGAS");
            player.sendMessage("§aVocê subiu para a liga " + BedWarsLeague.getTag(nextLeague) + "§a!");
            player.sendMessage("");
            player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            lobbyPlayer.updateLeagueNPC();

            Core.getDataPlayer().update(lp);
            Platform.getDataPlayer().update(DataType.BED_WARS_ACCOUNT, bedPlayer);
        }

        if (player.getPassenger() instanceof Player) {
            Player passanger = (Player) player.getPassenger();
            if (passanger == null)return;
            if (!(player.getPassenger() instanceof Player))return;

            ActionBar.sendActionBar(player, "§aAperte §2§lSHIFT §apara jogar seu amigo.");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Services.getPlayerService().unload(event.getPlayer().getUniqueId());
    }
}
