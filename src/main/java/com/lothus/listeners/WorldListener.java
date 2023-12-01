package com.lothus.listeners;

import com.mclothus.bukkit.events.commands.AdminChangeEvent;
import com.mclothus.core.Core;
import com.mclothus.core.player.LothPlayer;
import com.mclothus.core.player.group.rank.Rank;
import com.mclothus.core.storage.redis.channels.RedisChannel;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class WorldListener implements Listener {

    @EventHandler
    public void onAdminChage(AdminChangeEvent event) {
        Player player = event.getPlayer();
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        for (Player o : Bukkit.getOnlinePlayers()) {
            LothPlayer on = Core.getPlayerController().get(o.getUniqueId());
            if (event.isStatus()) {
                if (on.getGroup().getRank().ordinal() >= Rank.GER.ordinal()) {
                    o.hidePlayer(player);
                }
            } else {
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 4.0f, 4.0f);
                o.showPlayer(player);
            }
        }

        lothPlayer.getPrefs().setVanish(event.isStatus());
        player.sendMessage("§2§lADMIN: §aVocê " + (event.isStatus() ? "§2§lENTROU" : "§c§lSAIU") + " §ado modo admin.");
        Core.getDataPlayer().update(lothPlayer);
    }

    @EventHandler
    public void onFoodLevel(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void explode(EntityExplodeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlock(BlockBreakEvent event) {
        LothPlayer player = Core.getPlayerController().get(event.getPlayer().getUniqueId());
        if (player.getGroup().getRank() != Rank.CEO && (event.getPlayer().getGameMode() != GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlock(BlockPlaceEvent event) {
        LothPlayer player = Core.getPlayerController().get(event.getPlayer().getUniqueId());
        if (player.getGroup().getRank() != Rank.CEO && (event.getPlayer().getGameMode() != GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventory(InventoryClickEvent event) {
        LothPlayer player = Core.getPlayerController().get(event.getWhoClicked().getUniqueId());
        if (player.getGroup().getRank() != Rank.CEO && (event.getWhoClicked().getGameMode() != GameMode.CREATIVE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onJump(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Block slime = e.getTo().getBlock().getRelative(BlockFace.DOWN);
        if (slime.getType() == Material.SLIME_BLOCK) {
            p.setVelocity(p.getLocation().getDirection().multiply(2));
            p.setVelocity(new Vector(p.getVelocity().getX(), 1.0D, p.getVelocity().getZ()));
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 66, 4));
            p.playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 2.0f, 2.0f);
        }
    }
}
