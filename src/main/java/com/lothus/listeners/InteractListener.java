package com.lothus.listeners;

import com.lothus.engines.sync.menus.games.skywars.SkyShopMenu;
import com.lothus.menus.interaction.InteractionMenu;
import com.lothus.menus.lobby.LobbiesMenu;
import com.lothus.menus.profile.ProfileMenu;
import com.lothus.menus.server.ServerMenu;
import com.lothus.wadgets.sync.menus.CosmeticMenu;
import com.mclothus.bukkit.utils.items.ItemCreator;
import com.mclothus.core.Core;
import com.mclothus.core.player.LothPlayer;
import com.mclothus.core.player.prefs.visibility.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InteractListener implements Listener {

    private HashMap<UUID, Long> c = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if (event.getAction().name().contains("PHYSICAL")) {
            if (event.getClickedBlock().getType() == Material.CROPS ||
                    event.getClickedBlock().getType() == Material.CARROT ||
                    event.getClickedBlock().getType() == Material.POTATO) {
                event.setCancelled(true);
                return;
            }
        }

        if (itemStack == null || itemStack.getType() == Material.AIR)return;

        ItemMeta meta = itemStack.getItemMeta();

        if (!meta.hasDisplayName())return;
        String d = meta.getDisplayName();

        if (d == null)return;

        if (d.equalsIgnoreCase("§aLoja")) {
            player.playSound(player.getLocation(), Sound.CLICK, 2.0f, 2.0f);
            new SkyShopMenu().open(player);
            return;
        }

        if (d.equalsIgnoreCase("§aPerfil")) {
            player.playSound(player.getLocation(), Sound.CLICK, 2.0f, 2.0f);
            new ProfileMenu().open(player);
            return;
        }

        if (d.equalsIgnoreCase("§aModos de Jogo")) {
            player.playSound(player.getLocation(), Sound.CLICK, 2.0f, 2.0f);
            new ServerMenu().open(player);
            return;
        }

        if (d.equalsIgnoreCase("§aLobbies")) {
            player.playSound(player.getLocation(), Sound.CLICK, 2.0f, 2.0f);
            new LobbiesMenu().open(player);
            return;
        }

        if (d.equalsIgnoreCase("§aCosméticos")) {
            player.playSound(player.getLocation(), Sound.CLICK, 2.0f,2.0f);
            CosmeticMenu.open(player);
            return;
        }

        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        if (d.startsWith("§fPlayers:")) {
            if (c.get(player.getUniqueId()) != null && c.get(player.getUniqueId()) > System.currentTimeMillis()) {
                player.sendMessage("§cVocê deve aguardar para fazer isso novamente.");
                return;
            }

            c.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1));

            if (lothPlayer.getPrefs().getVisibility().equals(Visibility.ALL)) {
                lothPlayer.getPrefs().setVisibility(Visibility.FRIENDS);
            } else if (lothPlayer.getPrefs().getVisibility().equals(Visibility.FRIENDS)) {
                lothPlayer.getPrefs().setVisibility(Visibility.NOBODY);
            } else if (lothPlayer.getPrefs().getVisibility().equals(Visibility.NOBODY)) {
                lothPlayer.getPrefs().setVisibility(Visibility.ALL);
            }

            if (lothPlayer.getPrefs().getVisibility().equals(Visibility.ALL)) {
                Bukkit.getOnlinePlayers().forEach(r -> {
                    LothPlayer rp = Core.getPlayerController().get(r.getUniqueId());
                    if (!rp.getPrefs().isVanish()) {
                        player.showPlayer(r);
                    }
                });
            } else if (lothPlayer.getPrefs().getVisibility().equals(Visibility.FRIENDS)) {
                for (Player o : Bukkit.getOnlinePlayers()) {
                    LothPlayer on = Core.getPlayerController().get(o.getUniqueId());

                    if (!on.getPrefs().isVanish()) {
                        player.showPlayer(o);
                    }

                    if (!lothPlayer.getSocial().getFriends().contains(o.getUniqueId())) {
                        player.hidePlayer(o);
                    } else {
                        if (!on.getPrefs().isVanish()) {
                            player.showPlayer(o);
                        }
                    }
                }
            } else {
                Bukkit.getOnlinePlayers().forEach(player::hidePlayer);
            }

            player.getInventory().setItem(7, new ItemCreator(Material.INK_SACK).setDisplayName("§fPlayers: " + (lothPlayer.getPrefs().getVisibility().equals(Visibility.ALL) ? "§aTodos" : lothPlayer.getPrefs().getVisibility().equals(Visibility.FRIENDS) ? "§dAmigos" : "§cNinguém")).setId((lothPlayer.getPrefs().getVisibility().equals(Visibility.ALL) ? 10 : lothPlayer.getPrefs().getVisibility().equals(Visibility.NOBODY) ? 8 : 5)).build());
            player.playSound(player.getLocation(), Sound.CLICK, 2.0f, 2.0f);
            Core.getDataPlayer().update(lothPlayer);
        }
    }


    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        if (entity == null)return;
        if (!(entity instanceof Player))return;

        Player target = (Player) entity;

        new InteractionMenu(target).init().openInventory(player);

    }
}
