package com.lothus.definitions;

import com.lothus.Lobby;
import com.lothus.core.Core;
import com.lothus.core.api.actionbar.ActionBar;
import com.lothus.core.api.hologram.Hologram;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.player.prefs.visibility.Visibility;
import com.lothus.core.player.rejoin.Rejoin;
import com.lothus.core.servers.ServerInfo;
import com.lothus.core.servers.type.ServerType;
import com.lothus.core.utils.bukkit.ItemCreator;
import com.lothus.core.utils.bukkit.locations.LocationInfo;
import com.lothus.core.utils.bukkit.locations.type.LocationType;
import com.lothus.instance.type.InstanceType;
import com.lothus.ranking.RankingHologram;
import com.lothus.sync.stats.data.type.DataType;
import com.lothus.utils.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.lothus.core.player.prefs.visibility.Visibility.*;

@Getter @Setter
public class Definitions {

    private List<String> messages;
    private List<Hologram> holograms;
    private List<LocationInfo> locations;

    public Definitions() {
        this.locations = locations();
        this.holograms = holograms();
        this.messages = Lobby.getPlugin().getConfig().getStringList("config.messages");
    }

    public void join(Player player) {
        player.teleport(locations.stream().filter(info -> info.getLocationType() == LocationType.SPAWN).findFirst().get().getLocation());

        reset(player);
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        player.getInventory().setItem(0, new ItemCreator(Material.COMPASS).setDisplayName("§aModos de Jogo").build());
        player.getInventory().setItem(1, new ItemCreator(Material.SKULL_ITEM).setDisplayName("§aPerfil").withSkullOwner(player.getName()).setId(3).build());
        player.getInventory().setItem(4, new ItemCreator(Material.CHEST).setDisplayName("§aCosméticos").build());
        if (Lobby.getInstanceType() == InstanceType.MAIN) {
            player.getInventory().setItem(5, new ItemCreator(Material.SNOW_BALL).setDisplayName("§aBola de Neve").setAmount(16).build());
        }

        player.getInventory().setItem(7, new ItemCreator(Material.INK_SACK).setDisplayName("§fPlayers: " + (lothPlayer.getPrefs().getVisibility().equals(ALL) ? "§aTodos" : lothPlayer.getPrefs().getVisibility().equals(FRIENDS) ? "§dAmigos" : "§cNinguém")).setId((lothPlayer.getPrefs().getVisibility().equals(ALL) ? 10 : lothPlayer.getPrefs().getVisibility().equals(NOBODY) ? 8 : 5)).build());
        player.getInventory().setItem(8, new ItemCreator(Material.NETHER_STAR).setDisplayName("§aLobbies").build());

        if (Lobby.getInstanceType() != InstanceType.MAIN) {
            player.getInventory().setItem(3, new ItemCreator(Material.EMERALD, "§aLoja").build());
        }

        if (Lobby.getInstanceType() == InstanceType.BED_WARS) {
            Rejoin rejoin = Core.getRejoinController().getRejoin(player.getUniqueId());
            if (rejoin == null)return;

            int sec = (int)TimeUnit.MILLISECONDS.toSeconds(rejoin.getExpires() - System.currentTimeMillis());

            if (sec < 1) {
                Core.getRejoinController().unload(player.getUniqueId());
                return;
            }

            TextComponent s1 = new TextComponent("§cClique ");
            TextComponent c = new TextComponent("§c§lAQUI");
            TextComponent s2 = new TextComponent("§c para reconectar-se a partida.");

            c.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rejoin"));
            c.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§cClique para reconectar-se a partida.")));

            s1.addExtra(c);
            s1.addExtra(s2);

            player.sendMessage("");
            player.sendMessage("§c§lBED WARS - RECONEXÃO");
            player.sendMessage("§cVocê tem §e" + sec + " segundo" + (sec == 0 ? "" : "s") + " §cpara reconectar-se a partida.");
            player.sendMessage(s1);
            player.sendMessage("");
        }


    }

    public void reset(Player player) {
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setFallDistance(0);
        player.setFireTicks(0);

        player.setGameMode(GameMode.ADVENTURE);

        if (lothPlayer.getPrefs().isFly()) {
            if (lothPlayer.getGroup().getTag() != Rank.MEMBRO) {
                player.setAllowFlight(true);
                player.setFlying(true);
            } else {
                player.setAllowFlight(false);
                player.setFlying(false);
            }
        } else {
            player.setAllowFlight(false);
            player.setFlying(false);
        }

        player.getActivePotionEffects().forEach(p -> player.removePotionEffect(p.getType()));

        player.getInventory().clear();

        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        player.setExp(0);
        player.setLevel(0);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (lothPlayer.getPrefs().getVisibility() == ALL) {
                online.showPlayer(player);
            }

            if (lothPlayer.getPrefs().getVisibility() == FRIENDS) {
                if (!(lothPlayer.getSocial().hasFriend(online.getUniqueId()))) {
                    online.hidePlayer(player);
                }
            }

            if (lothPlayer.getPrefs().getVisibility() == NOBODY) {
                online.hidePlayer(player);
            }
        }


        if (lothPlayer.getPrefs().isVanish()) {
            player.sendMessage("§2§lADMIN: §aVocê está com o modo §2§lADMIN §aativado!");
        }

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (lothPlayer.getPrefs().isVanish()) {
                online.hidePlayer(player);
            }
        }

        player.playSound(player.getLocation(), Sound.LEVEL_UP, 4.0f,4.0f);
        player.sendTitle(new Title("§2§lLOTHUS", "§aSeja bem-vindo!", 10, 20, 10));
        ActionBar.sendActionBar(player, "§aSeja bem-vindo §2" + player.getName() + "§a!");
        holograms.forEach(h -> h.spawnTo(player));

        if (!lothPlayer.hasPermission(Rank.VIP))return;
        if (!(lothPlayer.getGroup().getTag().ordinal() <= Rank.VIP.ordinal()))return;

        player.setAllowFlight(true);
        player.setFlying(true);

        for (Player o : Bukkit.getOnlinePlayers()) {
            if (lothPlayer.getPrefs().isVanish()) return;
            LothPlayer online = Core.getPlayerController().get(o.getUniqueId());

            if (online == null) continue;
            if (lothPlayer.isFake()) {
                if (lothPlayer.getSocial().getFake().getRank() == Rank.MEMBRO) continue;
                o.sendMessage(
                        lothPlayer.getSocial().getFake().getRank().getColor() + "§l" + lothPlayer.getSocial().getFake().getRank().getName().toUpperCase() + " " + lothPlayer.getSocial().getFake().getRank().getColor() +
                                lothPlayer.getSocial().getFake().getName() + "§e entrou neste lobby! " + (online.hasPermission(Rank.GER) ? "§7§lDISFARÇADO" : "")
                );
                continue;
            }
            o.sendMessage(lothPlayer.getGroup().getTag().getColor() + "§l" + lothPlayer.getGroup().getTag().getName().toUpperCase() + " " + lothPlayer.getGroup().getTag().getColor() +
                    lothPlayer.getName() + "§e entrou neste lobby!");
        }
    }

    public Location getSpawn() {
        return getLocation(LocationType.SPAWN);
    }

    public Location getLocation(LocationType locationType) {
        for (LocationInfo locationInfo : locations) {
            if (locationInfo.getLocationType() == locationType) {
                return locationInfo.getLocation();
            }
        }
        return null;
    }

    public void addLocation(Location location, LocationType locationType) {
        LocationInfo info = new LocationInfo(location, locationType);
        if (getLocation(locationType) != null) {
            locations.removeIf(i -> i.getLocationType() == locationType);
        }
        locations.add(info);

        Lobby.getPlugin().getConfig().set("config.locations", getData());
        Lobby.getPlugin().saveConfig();
    }

    private List<String> getData() {
        List<String> datas = new ArrayList<>();
        locations.forEach(locationInfo -> datas.add(locationInfo.getLocationType().name() + " : " + LocationUtil.getData(locationInfo.getLocation())));
        return datas;
    }


    public Integer getOnlineCount(ServerType serverType) {
        int players = 0;
        for (ServerInfo serverInfo : Core.getServerController().getAll()) {
            if (serverInfo == null) {
                continue;
            }
            if (serverInfo.getType() == serverType) {
                players += serverInfo.getPlayers();
            }
        }
        return players;
    }


    private List<Hologram> holograms() {
        List<Hologram> h = new ArrayList<>();
        String game = (Lobby.getInstanceType() == InstanceType.SKY_WARS ? "skywars" : Lobby.getInstanceType() == InstanceType.BED_WARS ? "bedwars" : "none");

        if (game.equals("none")) {
            return h;
        }

        for (LocationInfo locationInfo : locations) {
            if (!locationInfo.getLocationType().name().startsWith("HOLOGRAM")) continue;

            RankingHologram hologram = null;

            if (locationInfo.getLocationType() == LocationType.HOLOGRAM_TOP_LEVEL) {
                hologram = new RankingHologram(locationInfo.getLocation(), game == "skywars" ? DataType.SKY_WARS_ACCOUNT : DataType.BED_WARS_ACCOUNT, "level", "NÍVEIS");
            } else if (locationInfo.getLocationType() == LocationType.HOLOGRAM_TOP_WINS) {
                hologram = new RankingHologram(locationInfo.getLocation(), game == "skywars" ? DataType.SKY_WARS_ACCOUNT : DataType.BED_WARS_ACCOUNT, "totalWins", "VITÒRIAS TOTAIS");
            } else if (locationInfo.getLocationType() == LocationType.HOLOGRAM_TOP_KILLS) {
                hologram = new RankingHologram(locationInfo.getLocation(), game == "skywars" ? DataType.SKY_WARS_ACCOUNT : DataType.BED_WARS_ACCOUNT, "totalKills", "VÍTIMAS TOTAIS");
            } else if (locationInfo.getLocationType() == LocationType.HOLOGRAM_TOP_RANK) {
                hologram = new RankingHologram(locationInfo.getLocation(),  game == "skywars" ? DataType.SKY_WARS_ACCOUNT : DataType.BED_WARS_ACCOUNT, "level", "RANKS");
            }

            RankingHologram finalHologram = hologram;
            new BukkitRunnable() {
                public void run() {
                    try {
                        assert finalHologram != null;
                        finalHologram.update();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskTimer(Lobby.getPlugin(), 0, 20 * 60 * 10);
            h.add(hologram);
        }

        return h;
    }
    private List<LocationInfo> locations() {
        List<LocationInfo> infos = new ArrayList<>();
        for (String s : Lobby.getPlugin().getConfig().getStringList("config.locations")) {
            LocationType type = LocationType.getByName(s.split(" : ")[0]);
            if (type == null) {
                Core.getLogger().info("Não foi possível carregar a linha §e" + s + ". (TIPO INVÀLIDO)");
                continue;
            }

            Core.getLogger().info(type.name() + " -> " + s.split(" : ")[1]);

            Location location = LocationUtil.getLocation(s.split(" : ")[1]);
            LocationInfo info = new LocationInfo(location, type);
            infos.add(info);
        }
        return infos;
    }
}
