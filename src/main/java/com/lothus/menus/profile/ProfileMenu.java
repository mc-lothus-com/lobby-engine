package com.lothus.menus.profile;

import com.lothus.api.AbstractMenu;
import com.lothus.menus.profile.submenus.PrefsMenu;
import com.lothus.menus.profile.submenus.StatsMenu;
import com.mclothus.bukkit.utils.items.ItemCreator;
import com.mclothus.core.Core;
import com.mclothus.core.player.LothPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;

public class ProfileMenu extends AbstractMenu {

    private Inventory inventory = getInventory();

    public ProfileMenu() {
        super(
                "Perfil",
                (9*5)
        );
    }

    @Override
    public void items(Player player) {
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());
        inventory.setItem(13, new ItemCreator(Material.SKULL_ITEM)
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
                ).withSkullOwner(player.getName()).setId(3).build());

        for (int slot = 18; slot < 27; slot++) {
            inventory.setItem(slot, new ItemCreator(Material.STAINED_GLASS_PANE, " ").setId(7).build());
        }

        inventory.setItem(30, new ItemCreator(Material.REDSTONE_COMPARATOR)
                .setDisplayName("§aPreferências").setLore(
                        "§7Controle suas preferências dentro do servidor.",
                        "§eClique para expandir."
                )
                .build());

        inventory.setItem(31, new ItemCreator(Material.NAME_TAG)
                .setDisplayName("§aMedalhas").setLore(
                        "§7Veja e selecione medalhas."
                )
                .build());

        inventory.setItem(32, new ItemCreator(Material.PAPER).setDisplayName("§aEstatísticas").setLore(
                "§7Veja suas estatísticas dentro do servidor.",
                "§eClique para expandir."
        ).build());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory clickedInventory = event.getClickedInventory();

        if (clickedInventory == null) return;
        if (!clickedInventory.getTitle().equalsIgnoreCase(getTitle())) return;

        event.setCancelled(true);

        if (event.getRawSlot() == 30) {
            player.playSound(player.getLocation(), Sound.CLICK, 2.0f, 2.0f);
            PrefsMenu.open(player, 1);
        } else if (event.getRawSlot() == 31) {
            player.playSound(player.getLocation(), Sound.CLICK, 2.0f, 2.0f);
            player.chat("/medal");
        } else if (event.getRawSlot() == 32) {
            player.playSound(player.getLocation(), Sound.CLICK, 2.0f,2.0f);
            new StatsMenu().open(player);
        }
    }
}
