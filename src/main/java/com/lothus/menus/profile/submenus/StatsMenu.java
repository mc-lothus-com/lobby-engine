package com.lothus.menus.profile.submenus;

import com.lothus.api.AbstractMenu;
import com.lothus.engines.sync.menus.games.bedwars.stats.StatsBedMenu;
import com.lothus.engines.sync.menus.games.skywars.stats.StatsSkyMenu;
import com.lothus.menus.profile.ProfileMenu;
import com.mclothus.bukkit.utils.items.ItemCreator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class StatsMenu extends AbstractMenu {

    public StatsMenu() {
        super(
                "Estatísticas",
                (9*4)
        );
    }

    @Override
    public void items(Player player) {
        Inventory inventory = getInventory();
        inventory.setItem(12, new ItemCreator(Material.GRASS, "§aSky Wars").setLore(
                "§eClique para expandir."
        ).build());
        inventory.setItem(14, new ItemCreator(Material.BED, "§aBed Wars").setLore(
                "§eClique para expandir."
        ).build());

        inventory.setItem(31, new ItemCreator(Material.ARROW, "§cVoltar").build());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack itemStack = event.getCurrentItem();

        if (inventory == null)return;
        if (itemStack == null || itemStack.getType() == Material.AIR)return;

        if (!inventory.getName().equalsIgnoreCase(getTitle()))return;

        event.setCancelled(true);

        player.playSound(player.getLocation(), Sound.CLICK, 1, 1);

        if (event.getRawSlot() == 31) {
            new ProfileMenu().open(player);
        } else if (event.getRawSlot() == 14) {
            new StatsBedMenu().open(player);
        } else if (event.getRawSlot() == 12) {
            new StatsSkyMenu().open(player);
        }
    }
}
