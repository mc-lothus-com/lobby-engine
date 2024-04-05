package com.lothus.menus.profile.booster;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.paged.PagedInventory;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.item.supplier.InventoryItemSupplier;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.henryfabio.minecraft.inventoryapi.viewer.configuration.ViewerConfiguration;
import com.henryfabio.minecraft.inventoryapi.viewer.impl.paged.PagedViewer;
import com.lothus.core.Core;
import com.lothus.core.games.type.GameType;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.booster.GameBooster;
import com.lothus.core.player.booster.duration.type.BoosterDurationType;
import com.lothus.core.player.booster.status.BoosterStatus;
import com.lothus.core.utils.bukkit.ItemCreator;
import com.lothus.menus.league.LeagueMenu;
import com.lothus.menus.profile.ProfileMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BoosterMenu extends PagedInventory {

    public BoosterMenu() {
        super(
                "booster-inventory",
                "Perfil - Booster",
                9*3
        );
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {
        Player player = viewer.getPlayer();
        LothPlayer lp = Core.getPlayerController().get(player.getUniqueId());

        List<String> lore = new ArrayList<>();
        ItemCreator netherStar = new ItemCreator(Material.NETHER_STAR, "§aBoosters ativos");

        lore.add("§7Veja seus boosters ativos.");
        lore.add("");
        for (GameBooster booster : lp.getBoosters().stream().filter(gameBooster -> gameBooster.getGameType() == GameType.BED_WARS).filter(gameBooster -> gameBooster.getStatus() == BoosterStatus.ACTIVE).collect(Collectors.toList())) {
            lore.add(" §8§l● §fBooster de " + booster.getType().getName() + " " + booster.getGameType().getName() + " §7(" + (booster.getDuration().getType() == BoosterDurationType.HOURS ? "§a" + booster.getDuration().getDuration() + " horas§7" : "§a" + booster.getDuration().getDuration() + " minutos§7") + ")");
        }
        lore.add("");

        editor.setItem(21, InventoryItem.of(
                netherStar.setLore(lore).build()
        ).defaultCallback(event -> {
            event.setCancelled(true);
        }));

        editor.setItem(22, InventoryItem.of(
                new ItemCreator(Material.ARROW, "§aVoltar")
                        .setLore("§eClique para voltar ao menu principal")
                        .build()
        ).defaultCallback(event -> {
            event.setCancelled(true);
            new ProfileMenu().init().openInventory(player);
        }));
    }

    @Override
    protected void configureViewer(PagedViewer viewer) {

        viewer.changePage(23);
        viewer.getConfiguration().itemPageLimit(7);

    }

    @Override
    protected List<InventoryItemSupplier> createPageItems(PagedViewer pagedViewer) {
        Player player = pagedViewer.getPlayer();

        List<InventoryItemSupplier> items = new LinkedList<>();

        LothPlayer lp = Core.getPlayerController().get(player.getUniqueId());

        boolean alreadyActive = lp.getBoosters().stream().anyMatch(booster -> booster.getStatus() == BoosterStatus.ACTIVE);

        for (GameBooster booster : lp.getBoosters()) {
            items.add(() -> {
                return InventoryItem.of(
                        new ItemCreator(Material.EXP_BOTTLE, "§aBooster de " + booster.getType().getName() + " " + booster.getGameType().getName())
                                .setLore(
                                        "§fDuração: §7" + (booster.getDuration().getType() == BoosterDurationType.HOURS ? "§a" + booster.getDuration().getDuration() + " horas" : "§a" + booster.getDuration().getDuration() + " dias"),
                                        "§fMultiplicador: §a" + booster.getMultiplier() + "x",
                                        "",
                                        (alreadyActive ? "§cVocê já possui um booster ativo." : "§aClique para ativar o booster.")
                                )
                                .build()
                ).defaultCallback(event -> {
                    event.setCancelled(true);

                    if (alreadyActive) {
                        player.sendMessage("§cVocê já possui um booster ativo.");
                        return;
                    }

                    booster.setStatus(BoosterStatus.ACTIVE);
                    booster.getDuration().setExpires(System.currentTimeMillis() + (booster.getDuration().getType() == BoosterDurationType.HOURS ? TimeUnit.HOURS.toMillis(booster.getDuration().getDuration()) : TimeUnit.DAYS.toMillis(booster.getDuration().getDuration())));
                    player.sendMessage("§aBooster ativado com sucesso.");
                    new BoosterMenu().init().openInventory(player);
                    Core.getDataPlayer().update(lp);
                });
            });
        }
        return items;
    }
}
