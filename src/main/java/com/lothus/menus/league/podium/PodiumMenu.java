package com.lothus.menus.league.podium;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.utils.bukkit.ItemCreator;
import com.lothus.menus.league.LeagueMenu;
import com.lothus.sync.stats.player.games.bedwars.BedPlayer;
import com.lothus.sync.stats.player.games.bedwars.league.BedWarsLeague;
import com.lothus.sync.stats.player.games.bedwars.stats.BedStats;
import com.lothus.task.PodiumTask;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

public class PodiumMenu extends SimpleInventory {

    public PodiumMenu() {
        super(
                "bedwars-podium",
                "Liga - Pódio",
                9*3
        );

        configuration(config -> {
            config.secondUpdate(1);
        });
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {
        Player player = viewer.getPlayer();

        int slot = 10;
        int pos = 1;
        for (BedPlayer bp : PodiumTask.getBedPlayers().values().stream().sorted((o1, o2) -> Integer.compare(o2.getPoints(), o1.getPoints())).toArray(BedPlayer[]::new)) {
            if (pos > 7)break;

            LothPlayer lothPlayer = PodiumTask.getLothPlayers().get(bp.getUniqueId());
            BedStats stats = PodiumTask.getBedRanked().get(bp.getUniqueId());

            editor.setItem(slot, InventoryItem.of(
                    new ItemCreator(Material.SKULL_ITEM, "§a" + pos +"º. " + lothPlayer.getGroup().getTag().getColor() + lothPlayer.getName())
                            .setLore(
                                    "",
                                    "§fLiga: " + BedWarsLeague.getTag(bp.getLeague()),
                                    "§fPontos: §7" + bp.getPoints(),
                                    "§fProgresso atual: " + progressBar(bp.getPoints(), BedWarsLeague.nextLevel(bp.getLeague()).getPoints()),
                                    "",
                                    "§fVitórias: §a" + stats.getWins(),
                                    "§fDerrotas: §c" + stats.getLoses(),
                                    ""
                            )
                            .setAmount(1)
                            .setId(3)
                            .withSkullOwner(lothPlayer.getName())
                            .build()
            )
                    .defaultCallback(event -> {
                        event.setCancelled(true);
                    }));

            slot++;
            pos++;
        }

        editor.setItem(21, InventoryItem.of(
                        new ItemCreator(Material.WATCH, "§aAtualiza em: " + new SimpleDateFormat("mm:ss").format(PodiumTask.getUpdateIn() - System.currentTimeMillis()))
                                .setAmount(1)
                                .build())
                .defaultCallback(event -> {
                    event.setCancelled(true);
                }));

        editor.setItem(22, InventoryItem.of(
                new ItemCreator(Material.ARROW, "§aVoltar")
                        .setLore(
                                "§fClique para voltar ao menu principal"
                        )
                        .setAmount(1)
                        .build())
                .defaultCallback(event -> {
                    event.setCancelled(true);
                    new LeagueMenu().init().openInventory(player);
        }));
    }

    @Override
    protected void update(Viewer viewer, InventoryEditor editor) {
        editor.updateItemStack(21);
        editor.updateAllItemStacks();

        editor.setItem(21, InventoryItem.of(
                        new ItemCreator(Material.WATCH, "§aAtualiza em: " + new SimpleDateFormat("mm:ss").format(PodiumTask.getUpdateIn() - System.currentTimeMillis()))
                                .setAmount(1)
                                .build())
                .defaultCallback(event -> {
                    event.setCancelled(true);
                }));

    }

    private static String progressBar(int valorAtual, int valorMaximo) {
        int progresso = (int) (((double) valorAtual / valorMaximo) * 10);

        StringBuilder barra = new StringBuilder("§f[");
        for (int i = 0; i < 10; i++) {
            if (i < progresso) {
                barra.append("§a▓");
            } else {
                barra.append("§7▓");
            }
        }
        double i = ((double) valorAtual / valorMaximo) * 100;
        barra.append("§f] §c(" + (Math.round(i)) + "%)");

        return barra.toString();
    }
}
