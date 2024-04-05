package com.lothus.listeners;

import com.lothus.Lobby;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.prefs.visibility.Visibility;
import com.lothus.core.utils.bukkit.ItemCreator;
import com.lothus.instance.type.InstanceType;
import com.lothus.menus.interaction.InteractionMenu;
import com.lothus.menus.league.LeagueMenu;
import com.lothus.menus.lobby.LobbiesMenu;
import com.lothus.menus.profile.ProfileMenu;
import com.lothus.menus.server.ServerMenu;
import com.lothus.player.LobbyPlayer;
import com.lothus.services.Services;
import com.lothus.sync.stats.menus.games.bedwars.BedShopMenu;
import com.lothus.sync.stats.menus.games.bedwars.stats.StatsBedMenu;
import com.lothus.sync.stats.menus.games.skywars.SkyShopMenu;
import com.lothus.sync.stats.menus.games.skywars.stats.StatsSkyMenu;
import com.lothus.wadgets.sync.menus.CosmeticMenu;
import com.mojang.authlib.GameProfile;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.events.NPCInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InteractListener implements Listener {

    private HashMap<UUID, Long> c = new HashMap<>();
    private HashMap<UUID, Long> snowCooldown = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if (player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
        }

        if (itemStack == null || itemStack.getType() == Material.AIR)return;

        ItemMeta meta = itemStack.getItemMeta();

        if (!meta.hasDisplayName())return;
        String d = meta.getDisplayName();

        if (d == null)return;

        event.setCancelled(true);

        if (d.equalsIgnoreCase("§aBola de Neve")) {
            if (snowCooldown.containsKey(player.getUniqueId())) {
                if (snowCooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage("§cVocê deve aguardar para fazer isso novamente.");
                    return;
                }
            }

            Snowball snowball = player.launchProjectile(Snowball.class);
            snowball.setVelocity(player.getLocation().getDirection().multiply(1.3));
            player.playSound(player.getLocation(), Sound.CLICK, 2.0f, 2.0f);

            snowCooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(16));
            return;
        }

        if (d.equalsIgnoreCase("§aLoja")) {
            player.playSound(player.getLocation(), Sound.CLICK, 2.0f, 2.0f);
            if (Lobby.getInstanceType() == InstanceType.BED_WARS) {
                new BedShopMenu().open(player);
            } else if (Lobby.getInstanceType() == InstanceType.SKY_WARS) {
                new SkyShopMenu().open(player);
            }
            return;
        }

        if (d.equalsIgnoreCase("§aPerfil")) {
            player.playSound(player.getLocation(), Sound.CLICK, 2.0f, 2.0f);
            new ProfileMenu().init().openInventory(player);
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
        if (!(entity instanceof Player)) {
            return;
        }

        Player target = (Player) entity;
        GameProfile gameProfile = ((CraftPlayer) target).getProfile();

        if (gameProfile.getName().contains("[NPC]"))return;

        new InteractionMenu(target).init().openInventory(player);
    }

    @EventHandler
    public void onStatsNPC(NPCInteractEvent event) {
        NPC npc = event.getNPC();

        LobbyPlayer lobbyPlayer = null;

        for (LobbyPlayer lp : Services.getPlayerService().getAll()) {
            if (lp.getStatsNPC() == null)continue;
            if (lp.getStatsNPC().getId() == npc.getId()) {
                lobbyPlayer = lp;
                break;
            }
        }

        if (lobbyPlayer == null)return;

        if (Lobby.getInstanceType() == InstanceType.BED_WARS) {
            new StatsBedMenu().init().openInventory(event.getWhoClicked());
        } else if (Lobby.getInstanceType() == InstanceType.SKY_WARS) {
            new StatsSkyMenu().init().openInventory(event.getWhoClicked());
        }
    }

    @EventHandler
    public void onLeagueNPC(NPCInteractEvent event) {
        NPC npc = event.getNPC();

        LobbyPlayer lobbyPlayer = null;

        for (LobbyPlayer lp : Services.getPlayerService().getAll()) {
            if (lp.getLeagueNPC() == null)continue;
            if (lp.getLeagueNPC().getId() == npc.getId()) {
                lobbyPlayer = lp;
                break;
            }
        }

        if (lobbyPlayer == null)return;

        new LeagueMenu().init().openInventory(event.getWhoClicked());
    }
}
