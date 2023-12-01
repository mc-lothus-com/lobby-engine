package com.lothus.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

@Getter @Setter
public abstract class AbstractMenu implements Listener {

    private Inventory inventory;
    
    private String title;
    private int size;

    public AbstractMenu(String title, int size) {
        this.title = title;
        this.size = size;
        
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    public abstract void items(Player player);

    public void open(Player player) {
        items(player);
        player.openInventory(inventory);
    }

    public void update(Player player) {
        items(player);
    }
}
