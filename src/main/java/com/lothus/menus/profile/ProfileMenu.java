package com.lothus.menus.profile;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.lothus.api.AbstractMenu;
import com.lothus.core.Core;
import com.lothus.core.games.type.GameType;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.prefs.Prefs;
import com.lothus.core.utils.bukkit.ItemCreator;
import com.lothus.menus.profile.booster.BoosterMenu;
import com.lothus.menus.profile.prefs.PrefsMenu;
import com.lothus.menus.profile.stats.StatsMenu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.text.SimpleDateFormat;

public class ProfileMenu extends SimpleInventory {

    public ProfileMenu() {
        super(
                "profile-menu",
                "Perfil",
                (9*5)
        );


    }


    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {
        Player player = viewer.getPlayer();

        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        editor.setItem(13, InventoryItem.of(new ItemCreator(Material.SKULL_ITEM)
                .setDisplayName("§a" + player.getName())
                .setLore(
                        "§fTag: " + lothPlayer.getGroup().getTag().getColor() + lothPlayer.getGroup().getTag().getName(),
                        "§fRank: " + lothPlayer.getGroup().getRank().getColor() + lothPlayer.getGroup().getRank().getName(),
                        " §7- Adicionado em: " + new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(lothPlayer.getGroup().getCreated()),
                        " §7- Atualizado em: " + new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(lothPlayer.getGroup().getLastModified()),
                        "",
                        "§fPrimeiro login: §7" + new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(lothPlayer.getFirstLogin()),
                        "§7 - Há " + new SimpleDateFormat("dd").format((lothPlayer.getFirstLogin() - System.currentTimeMillis())) + " dias.",
                        "§fÚltimo login: §7" + new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss").format(lothPlayer.getLastLogin())
                ).withSkullOwner(player.getName()).setId(3).build()
        ).defaultCallback(event -> {
            event.setCancelled(true);
        }));

        for (int slot = 18; slot < 27; slot++) {
            editor.setItem(slot, InventoryItem.of(new ItemCreator(Material.STAINED_GLASS_PANE, " ").setId(7).build()).defaultCallback(
                    event -> event.setCancelled(true)
            ));
        }

        editor.setItem(29, InventoryItem.of(new ItemCreator(Material.REDSTONE_COMPARATOR)
                .setDisplayName("§aPreferências").setLore(
                        "§7Controle suas preferências dentro do servidor.",
                        "§eClique para expandir."
                )
                .build()
        ).defaultCallback(event -> {
            event.setCancelled(true);
            PrefsMenu.open(player, 1);
        }));


        editor.setItem(30, InventoryItem.of(new ItemCreator(Material.NAME_TAG)
                .setDisplayName("§aMedalhas").setLore(
                        "§7Veja e selecione medalhas."
                )
                .build()
        ).defaultCallback(event -> {
            event.setCancelled(true);
            player.closeInventory();
            player.chat("/medal");
        }));

        editor.setItem(32, InventoryItem.of(new ItemCreator(Material.EXP_BOTTLE)
                .setDisplayName("§aBoosters")
                .setLore(
                        "§7Veja os seus boosters.",
                        "§eClique para expandir."
                ).build())
                .defaultCallback(event -> {
                    event.setCancelled(true);

                    if (lothPlayer.getBoosters() == null) {
                        player.sendMessage("§cVocê não possui boosters.");
                        return;
                    }

                    if (lothPlayer.getBoosters().isEmpty()) {
                        player.sendMessage("§cVocê não possui boosters.");
                        return;
                    }

                    new BoosterMenu().init().openInventory(player);
                }));

        editor.setItem(33, InventoryItem.of(new ItemCreator(Material.PAPER).setDisplayName("§aEstatísticas").setLore(
                "§7Veja suas estatísticas dentro do servidor.",
                "§eClique para expandir."
        ).build()).defaultCallback(event -> {
            event.setCancelled(true);

            new StatsMenu().open(player);
        }));
    }
}
