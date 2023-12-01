package com.lothus.menus.profile.submenus;

import com.lothus.menus.profile.ProfileMenu;
import com.mclothus.bukkit.utils.items.ItemCreator;
import com.mclothus.core.Core;
import com.mclothus.core.player.LothPlayer;
import com.mclothus.core.player.group.rank.Rank;
import com.mclothus.core.player.prefs.visibility.Visibility;
import com.mclothus.core.storage.redis.channels.RedisChannel;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PrefsMenu implements Listener {

    private HashMap<UUID, Long> cooldown = new HashMap<>();

    public static void open(Player player, int page) {
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());
        Inventory inventory = Bukkit.createInventory(null, 9*6, "Preferências - Página " + page + "/2");

        if (page == 1) {
            inventory.setItem(20, new ItemCreator(Material.SIGN, "§aMensagens Privadas").build());
            inventory.setItem(29, new ItemCreator(Material.INK_SACK, "§eClique para alternar.").setId((lothPlayer.getPrefs().isTell() ? 10 : 8)).build());

            inventory.setItem(21, new ItemCreator(Material.POTION, "§aVisibilidade").build());
            inventory.setItem(30, new ItemCreator(Material.INK_SACK, "§eClique para alternar.").setId((lothPlayer.getPrefs().getVisibility().equals(Visibility.ALL) ? 10 : lothPlayer.getPrefs().getVisibility().equals(Visibility.NOBODY) ? 8 : 5)).build());

            inventory.setItem(22, new ItemCreator(Material.FEATHER, "§aFly").build());
            inventory.setItem(31, new ItemCreator(Material.INK_SACK, "§eClique para alternar.").setId((lothPlayer.getPrefs().isFly() ? 10 : 8)).build());

            inventory.setItem(23, new ItemCreator(Material.HOPPER, "§aEstatísticas").build());
            inventory.setItem(32, new ItemCreator(Material.INK_SACK, "§eClique para alternar.").setId((lothPlayer.getPrefs().isStats() ? 10 : 8)).build());

            inventory.setItem(24, new ItemCreator(Material.PAPER, "§aChat").build());
            inventory.setItem(33, new ItemCreator(Material.INK_SACK, "§eClique para alternar.").setId((lothPlayer.getPrefs().isChat() ? 10 : 8)).build());

            inventory.setItem(50, new ItemCreator(Material.ARROW, "§aPróx. Página").build());
        } else if (page == 2) {
            inventory.setItem(20, new ItemCreator(Material.EMPTY_MAP, "§aConfirmar para voltar ao Lobby").build());
            inventory.setItem(29, new ItemCreator(Material.INK_SACK, "§eClique para alternar.").setId((lothPlayer.getPrefs().isLobby() ? 10 : 8)).build());

            inventory.setItem(21, new ItemCreator(Material.BOOK_AND_QUILL, "§aCensurar chat").build());
            inventory.setItem(30, new ItemCreator(Material.INK_SACK, "§eClique para alternar.").setId((lothPlayer.getPrefs().isCensorShip() ? 10 : 8)).build());

            if (lothPlayer.getGroup().getRank().ordinal() <= Rank.AJD.ordinal() || lothPlayer.getGroup().containsPermission("lobby.prefs.admin")) {
                inventory.setItem(22, new ItemCreator(Material.BARRIER, "§aVanish").build());
                inventory.setItem(31, new ItemCreator(Material.INK_SACK, "§eClique para alternar.").setId((lothPlayer.getPrefs().isVanish() ? 10 : 8)).build());

                inventory.setItem(23, new ItemCreator(Material.PAPER, "§aChat Staff").build());
                inventory.setItem(32, new ItemCreator(Material.INK_SACK, "§eClique para alternar.").setId((lothPlayer.getPrefs().isStaffChat() ? 10 : 8)).build());

                inventory.setItem(24, new ItemCreator(Material.PAPER, "§aReportes").build());
                inventory.setItem(33, new ItemCreator(Material.INK_SACK, "§eClique para alternar.").setId((lothPlayer.getPrefs().isReport() ? 10 : 8)).build());
            }

            inventory.setItem(48, new ItemCreator(Material.ARROW, "§aPág. Anterior").build());
        }

        inventory.setItem(49, new ItemCreator(Material.BARRIER, "§cVoltar").build());
        player.openInventory(inventory);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        ItemStack stack = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        if (stack == null)return;
        if (inventory == null)return;

        if (!inventory.getName().startsWith("Preferências - Página"))return;
        event.setCancelled(true);

        if (inventory.getName().endsWith("1/2")) {
            if (event.getRawSlot() == 29) {
                if (cooldown.get(player.getUniqueId()) != null && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage("§eVocê deve aguardar para alternar novamente.");
                    return;
                }
                cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2));
                lothPlayer.getPrefs().setTell(!lothPlayer.getPrefs().isTell());
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                open(player, 1);
            } else if (event.getRawSlot() == 30) {
                if (cooldown.get(player.getUniqueId()) != null && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage("§eVocê deve aguardar para alternar novamente.");
                    return;
                }
                cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2));
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
                    Bukkit.getOnlinePlayers().forEach(r -> {
                        player.showPlayer(r);
                        if (!lothPlayer.getSocial().hasFriend(r.getUniqueId())) {
                            player.hidePlayer(r);
                        }
                    });
                } else {
                    Bukkit.getOnlinePlayers().forEach(player::hidePlayer);
                }
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                player.getInventory().setItem(7, new ItemCreator(Material.INK_SACK).setDisplayName("§fPlayers: " + (lothPlayer.getPrefs().getVisibility().equals(Visibility.ALL) ? "§aTodos" : lothPlayer.getPrefs().getVisibility().equals(Visibility.FRIENDS) ? "§dAmigos" : "§cNinguém")).setId((lothPlayer.getPrefs().getVisibility().equals(Visibility.ALL) ? 10 : lothPlayer.getPrefs().getVisibility().equals(Visibility.NOBODY) ? 8 : 5)).build());
                open(player, 1);
            } else if (event.getRawSlot() == 31) {
                if (cooldown.get(player.getUniqueId()) != null && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage("§eVocê deve aguardar para alternar novamente.");
                    return;
                }

                if (!(lothPlayer.getGroup().getRank().ordinal() <= Rank.VIP.ordinal())) {
                    player.sendMessage("");
                    player.sendMessage("§cA função selecionada é destinada apenas para VIP ou superior.");
                    player.sendMessage("§eAdquira um VIP em nossa loja: §emc-lothus.com/loja");
                    player.sendMessage("");
                    return;
                }

                cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2));
                lothPlayer.getPrefs().setFly(!lothPlayer.getPrefs().isFly());
                player.setAllowFlight(lothPlayer.getPrefs().isFly());
                player.setFlying(lothPlayer.getPrefs().isFly());
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                open(player, 1);
            } else if (event.getRawSlot() == 32) {
                if (cooldown.get(player.getUniqueId()) != null && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage("§eVocê deve aguardar para alternar novamente.");
                    return;
                }
                cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2));
                lothPlayer.getPrefs().setStats(!lothPlayer.getPrefs().isStats());
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                open(player, 1);
            } else if (event.getRawSlot() == 33) {
                if (cooldown.get(player.getUniqueId()) != null && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage("§eVocê deve aguardar para alternar novamente.");
                    return;
                }
                cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2));
                lothPlayer.getPrefs().setChat(!lothPlayer.getPrefs().isChat());
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                open(player, 1);
            } else if (event.getRawSlot() == 50) {
                open(player, 2);
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
            } else if (event.getRawSlot() == 49) {
                new ProfileMenu().open(player);
            }
        } else if (inventory.getName().endsWith("2/2")) {
            if (event.getRawSlot() == 30) {
                if (cooldown.get(player.getUniqueId()) != null && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage("§eVocê deve aguardar para alternar novamente.");
                    return;
                }
                cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2));
                lothPlayer.getPrefs().setCensorShip(!lothPlayer.getPrefs().isCensorShip());
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                open(player, 2);
            } else if (event.getRawSlot() == 29) {
                if (cooldown.get(player.getUniqueId()) != null && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage("§eVocê deve aguardar para alternar novamente.");
                    return;
                }
                cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2));
                lothPlayer.getPrefs().setLobby(!lothPlayer.getPrefs().isLobby());
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                open(player, 2);
            } else if (event.getRawSlot() == 31) {
                if (cooldown.get(player.getUniqueId()) != null && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage("§eVocê deve aguardar para alternar novamente.");
                    return;
                }
                cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2));
                player.chat("/admin");
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                open(player, 2);
            } else if (event.getRawSlot() == 32) {
                if (cooldown.get(player.getUniqueId()) != null && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage("§eVocê deve aguardar para alternar novamente.");
                    return;
                }
                cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2));
                lothPlayer.getPrefs().setStaffChat(!lothPlayer.getPrefs().isStaffChat());
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                open(player, 2);
            } else if (event.getRawSlot() == 33) {
                if (cooldown.get(player.getUniqueId()) != null && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
                    player.sendMessage("§eVocê deve aguardar para alternar novamente.");
                    return;
                }
                cooldown.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2));
                lothPlayer.getPrefs().setReport(!lothPlayer.getPrefs().isReport());
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                open(player, 2);
            } else if (event.getRawSlot() == 48) {
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                open(player, 1);
            } else if (event.getRawSlot() == 49) {
                new ProfileMenu().open(player);
            }
        }
        Core.getDataPlayer().update(lothPlayer);
    }
}
