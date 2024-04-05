package com.lothus.commands;

import com.lothus.Lobby;
import com.lothus.bukkit.commands.CommandBase;
import com.lothus.core.Core;
import com.lothus.core.player.LothPlayer;
import com.lothus.core.player.group.rank.Rank;
import com.lothus.core.utils.bukkit.locations.type.LocationType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand extends CommandBase {

    public SetupCommand() {
        super(
                "setup"
        );
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        LothPlayer lothPlayer = Core.getPlayerController().get(player.getUniqueId());

        if (lothPlayer.getGroup().getRank() != Rank.CEO) {
            if (!lothPlayer.getGroup().containsPermission("command.setup")) {
                player.sendMessage(NO_PERMISSION);
                return true;
            }
        }

        if (args.length == 0) {
            player.sendMessage("§cSintaxe incorreta, utilize '/setup [npcs/holograms/spawn]' para continuar.");
            return true;
        }

        if (args.length > 0) {
            String s = args[0];

            if (s.equalsIgnoreCase("npcs")) {
                if (args.length > 1) {
                    String s1 = args[1];
                    if (s1.equalsIgnoreCase("lobby")) {
                        if (args.length > 2) {
                            String s2 = args[2];
                            LocationType type = (s2.contains("skywars") ? LocationType.NPC_SKYWARS : s2.contains("bedwars") ? LocationType.NPC_BEDWARS : LocationType.NPC_TRAINING);
                            Lobby.getDefinitions().addLocation(player.getLocation(), type);
                            player.sendMessage("§2§lSETUP: §aVocê alterou a localização do §2§l" + type.name().replace("_", " ") + "§a.");
                            return true;
                        }
                    } else if (s1.equalsIgnoreCase("bedwars")) {
                        if (args.length > 2) {
                            String s2 = args[2];
                            LocationType type = (s2.contains("solo") ? LocationType.NPC_SOLO : s2.contains("team") ? LocationType.NPC_TEAM : s2.contains("trio") ? LocationType.NPC_TRIO : LocationType.NPC_QUARTETO);
                            Lobby.getDefinitions().addLocation(player.getLocation(), type);
                            player.sendMessage("§2§lSETUP: §aVocê alterou a localização do §2§l" + type.name().replace("_", " ") + "§a.");
                            return true;
                        }
                    } else if (s1.equalsIgnoreCase("skywars")) {
                        if (args.length > 2) {
                            String s2 = args[2];
                            LocationType type = (s2.contains("solo") ? LocationType.NPC_SOLO : LocationType.NPC_TEAM);
                            Lobby.getDefinitions().addLocation(player.getLocation(), type);
                            player.sendMessage("§2§lSETUP: §aVocê alterou a localização do §2§l" + type.name().replace("_", " ") + "§a.");
                            return true;
                        }
                    }
                }
                return false;
            }

            if (s.equalsIgnoreCase("hologram")) {
                if (args.length > 1) {
                    String s1 = args[1];
                    if (s1.equalsIgnoreCase("kills")) {
                        Lobby.getDefinitions().addLocation(player.getLocation(), LocationType.HOLOGRAM_TOP_KILLS);
                        player.sendMessage("§2§lSETUP: §aVocê alterou a localização do §2§lHOLOGRAMA DE KILLS§a.");
                        return true;
                    } else if (s1.equalsIgnoreCase("wins")) {
                        Lobby.getDefinitions().addLocation(player.getLocation(), LocationType.HOLOGRAM_TOP_WINS);
                        player.sendMessage("§2§lSETUP: §aVocê alterou a localização do §2§lHOLOGRAMA DE WINS§a.");
                        return true;
                    } else if (s1.equalsIgnoreCase("level")) {
                        Lobby.getDefinitions().addLocation(player.getLocation(), LocationType.HOLOGRAM_TOP_LEVEL);
                        player.sendMessage("§2§lSETUP: §aVocê alterou a localização do §2§lHOLOGRAMA DE LEVEL§a.");
                        return true;
                    } else if (s1.equalsIgnoreCase("rank")) {
                        Lobby.getDefinitions().addLocation(player.getLocation(), LocationType.HOLOGRAM_TOP_RANK);
                        player.sendMessage("§2§lSETUP: §aVocê alterou a localização do §2§lHOLOGRAMA DE RANK§a.");
                        return true;
                    }
                }
                return false;
            }

            if (s.equalsIgnoreCase("spawn")) {
                Lobby.getDefinitions().addLocation(player.getLocation(), LocationType.SPAWN);
                player.sendMessage("§2§lSETUP: §aVocê alterou a localização do §2§lSPAWN§a.");
                return true;
            }
        }
        return false;
    }
}

