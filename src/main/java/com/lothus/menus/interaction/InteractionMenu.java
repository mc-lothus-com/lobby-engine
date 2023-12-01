package com.lothus.menus.interaction;

import com.henryfabio.minecraft.inventoryapi.editor.InventoryEditor;
import com.henryfabio.minecraft.inventoryapi.inventory.impl.simple.SimpleInventory;
import com.henryfabio.minecraft.inventoryapi.item.InventoryItem;
import com.henryfabio.minecraft.inventoryapi.viewer.Viewer;
import com.lothus.engines.sync.data.type.DataType;
import com.lothus.engines.sync.platform.Platform;
import com.lothus.engines.sync.player.games.bedwars.BedPlayer;
import com.lothus.engines.sync.player.games.bedwars.stats.BedStats;
import com.lothus.engines.sync.player.games.skywars.SkyPlayer;
import com.lothus.engines.sync.player.games.skywars.stats.SkyStats;
import com.mclothus.bukkit.utils.items.ItemCreator;
import com.mclothus.core.Core;
import com.mclothus.core.player.LothPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class InteractionMenu extends SimpleInventory {

    private Player target;
    public InteractionMenu(Player target) {
        super(
                "interaction-menu",
                "Informações de " + target.getName(),
                9*3
        );

        this.target = target;
    }

    @Override
    protected void configureInventory(Viewer v, InventoryEditor editor) {
        Player viewer = v.getPlayer();

        LothPlayer lp = Core.getPlayerController().get(viewer.getUniqueId());

        SkyPlayer skyPlayer = Platform.getSkyPlatform().getSkyPlayerController().getAccount(target.getUniqueId());
        SkyStats skySoloStats = Platform.getDataStats().getSkyStats(DataType.SKY_WARS_SOLO, target.getUniqueId());
        SkyStats skyTeamStats = Platform.getDataStats().getSkyStats(DataType.SKY_WARS_TEAM, target.getUniqueId());

        BedPlayer bedPlayer = Platform.getBedPlatform().getBedPlayerController().getAccount(target.getUniqueId());
        BedStats bedSoloStats = Platform.getDataStats().getBedStats(DataType.BED_WARS_SOLO, target.getUniqueId());
        BedStats bedTeamStats = Platform.getDataStats().getBedStats(DataType.BED_WARS_TEAM, target.getUniqueId());
        BedStats bedTrioStats = Platform.getDataStats().getBedStats(DataType.BED_WARS_TRIO, target.getUniqueId());
        BedStats bedQuarStats = Platform.getDataStats().getBedStats(DataType.BED_WARS_QUARTETO, target.getUniqueId());

        editor.setItem(12, InventoryItem.of(new ItemCreator(Material.GRASS, "§aSky Wars")
                .setLore(
                        "",
                        "§fNível: " + skyPlayer.getLevelColor().getColor() + "[" + skyPlayer.getLevel() + "✧]",
                        "§fXP: §7(" + skyPlayer.getXp() + "/500)",
                        "",
                        "§fVitórias Totais: §a" + skyPlayer.getTotalWins(),
                        "§fVítimas Totais: §a" + skyPlayer.getTotalKills(),
                        "",
                        "§fWinstreak: §a" + (Math.max(skySoloStats.getCurrentWinstreak(), skyTeamStats.getCurrentWinstreak())),
                        "§fMaior Winstreak: §a" + (Math.max(skySoloStats.getBestWinstreak(), skyTeamStats.getBestWinstreak())),
                        "",
                        "§fDerrotas: §c" + (skySoloStats.getLoses() + skyTeamStats.getLoses()),
                        "",
                        "§fCoins: §6" + skyPlayer.getCoins()).build()).defaultCallback(e -> e.setCancelled(true)));

        editor.setItem(14, InventoryItem.of(new ItemCreator(Material.BED, "§aBed Wars")
                .setLore(
                        "",
                        "§fNível: " + bedPlayer.getLevelColor().getColor() + "[" + bedPlayer.getLevel() + "✧]",
                        "§fXP: §7(" + bedPlayer.getXp() + "/500)",
                        "",
                        "§fVitórias Totais: §a" + bedPlayer.getTotalWins(),
                        "§fVítimas Totais: §a" + bedPlayer.getTotalKills(),
                        "",
                        "§fWinstreak: §a" + (Math.max(Math.max(bedSoloStats.getCurrentWinstreak(), bedTeamStats.getCurrentWinstreak()), Math.max(bedTrioStats.getCurrentWinstreak(), bedQuarStats.getCurrentWinstreak()))),
                        "§fMaior Winstreak: §a" + (Math.max(Math.max(bedSoloStats.getBestWinstreak(), bedTeamStats.getBestWinstreak()), Math.max(bedTrioStats.getBestWinstreak(), bedQuarStats.getBestWinstreak()))),
                        "",
                        "§fDerrotas: §c" + (bedSoloStats.getLoses() + bedTeamStats.getLoses() + bedTrioStats.getLoses() + bedQuarStats.getLoses()),
                        "",
                        "§fCoins: §6" + bedPlayer.getCoins()).build()).defaultCallback(e -> e.setCancelled(true)));


        boolean holding = (viewer.getPassenger() != null && viewer.getPassenger() instanceof Player);
        boolean friend = lp.getSocial().hasFriend(target.getUniqueId());

        editor.setItem(18, InventoryItem.of(new ItemCreator(Material.ARMOR_STAND, !holding ? "§aSegurar" : "§cSoltar")
                .setLore("§7Segure seu amigo e leve-o para qualquer lugar.",
                        (friend ? "§eClique para " + (holding ? "soltar." : "segurar.") : "§cVocê precisa ser amigo deste jogador.")).build())
                .defaultCallback(e -> {
                    e.setCancelled(true);
                    viewer.closeInventory();

                    if (!friend) {
                        viewer.sendMessage("§cVocê precisa ser amigo desde jogador para exeecutar essa ação.");
                        return;
                    }

                    viewer.setPassenger(target);
                }));
    }
}