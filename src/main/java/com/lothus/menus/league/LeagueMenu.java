package com.lothus.menus.league;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.lothus.core.Core;
import com.lothus.core.games.type.GameType;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.utils.bukkit.ItemCreator;
import com.lothus.menus.profile.booster.BoosterMenu;
import com.lothus.menus.league.podium.PodiumMenu;
import com.lothus.sync.stats.platform.Platform;
import com.lothus.sync.stats.player.games.bedwars.BedPlayer;
import com.lothus.sync.stats.player.games.bedwars.league.BedWarsLeague;
import com.lothus.sync.stats.player.games.bedwars.league.rewards.BedWarsReward;
import com.lothus.sync.stats.player.games.bedwars.league.rewards.type.RewardType;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LeagueMenu extends SimpleInventory {

    public LeagueMenu() {
        super(
                "bedwars-missions",
                "Bed Wars - Liga",
                9*3
        );
    }

    @Override
    protected void configureInventory(Viewer viewer, InventoryEditor editor) {
        Player player = viewer.getPlayer();

        BedPlayer bedPlayer = Platform.getBedPlatform().getBedPlayerController().getAccount(player.getUniqueId());

        editor.setItem(10, InventoryItem.of(
                new ItemCreator(Material.SKULL_ITEM, "§eInformações")
                        .setLore(
                                "",
                                "§fLiga: " + BedWarsLeague.getTag(bedPlayer.getLeague()),
                                "§fPróx. Liga: " + BedWarsLeague.getTag(BedWarsLeague.nextLevel(bedPlayer.getLeague())),
                                "",
                                "§fProgresso: " + progressBar(bedPlayer.getPoints(), BedWarsLeague.nextLevel(bedPlayer.getLeague()).getPoints()),
                                "§fPontos: §7" + bedPlayer.getPoints() + "/" + BedWarsLeague.nextLevel(bedPlayer.getLeague()).getPoints(),
                                ""
                        )
                        .withSkullOwner(player.getName())
                        .setId(3)
                        .setAmount(1)
                        .build()
        ).defaultCallback(event -> {
            event.setCancelled(true);
        }));

        editor.setItem(15, InventoryItem.of(
                new ItemCreator(Material.SIGN, "§ePódio")
                        .setLore("§7Veja o pódio de jogadores.",  "§eClique para expandir.")
                        .build()
        ).defaultCallback(event -> {
            event.setCancelled(true);
            new PodiumMenu().init().openInventory(player);
        }));

        List<String> lore = new ArrayList<>();
        List<BedWarsReward> rewards = BedWarsLeague.nextLevel(bedPlayer.getLeague()).getRewards();
        Comparator<BedWarsReward> comparator = Comparator.comparing(BedWarsReward::getType);
        comparator = comparator.thenComparing(BedWarsReward::getAmount);
        rewards.sort(comparator);

        lore.add("§7Essas são as recompensas que você");
        lore.add("§7receberá ao subir de liga.");
        lore.add("");
        for (BedWarsReward reward : rewards) {
            String message = " §8§l● §f";

            if (reward.getType() == RewardType.RANK || reward.getType() == RewardType.TAG) {
                Rank rank = Rank.getRankByName(reward.getValue());
                message += "§71x "+ reward.getType().getName() + " " + rank.getColor() + rank.getName() + " §7por " + reward.getAmount() + " dias.";
            } else {
                message += "§7" + reward.getAmount() + "x Bed Wars " + reward.getType().getName().replace("{s}" , reward.getAmount() > 0 ? "s" : "");
            }
            lore.add(message);
        }
        lore.add("");

        editor.setItem(16, InventoryItem.of(
                new ItemCreator(Material.BOOK, "§eRecompensas")
                        .setLore(
                                lore
                        ).build()
        ).defaultCallback(event -> {
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
