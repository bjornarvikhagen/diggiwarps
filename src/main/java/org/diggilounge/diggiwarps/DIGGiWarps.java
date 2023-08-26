package org.diggilounge.diggiwarps;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class DIGGiWarps extends JavaPlugin implements Listener, CommandExecutor {
    private WarpsManager warpsManager;

    private static final String PERMISSION_WARP = "diggiwarps.warp";
    private static final String PERMISSION_WARP_SET = "diggiwarps.warp.set";
    private static final String PERMISSION_WARP_DELETE = "diggiwarps.warp.delete";
    private static final String PERMISSION_WARPS = "diggiwarps.warps";

    @Override
    public void onEnable() {
        getLogger().info("DIGGiWarps activated");
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("warp").setExecutor(this);
        getCommand("warps").setExecutor(this);

        saveDefaultConfig();
        warpsManager = new WarpsManager(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("DIGGiWarps deactivated");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            UUID playerUUID = player.getUniqueId();
            if (command.getName().equalsIgnoreCase("warp")) {
                if (args.length == 0) {
                    if (player.hasPermission(PERMISSION_WARPS)) {
                        String[] warpNames = warpsManager.getWarpNames(playerUUID);
                        if (warpNames.length == 0) {
                            player.sendMessage(ChatColor.RED + "Du har ingen warps");
                        } else {
                            player.sendMessage(ChatColor.GREEN + "Tilgjengelige warps:");
                            for (String warpName : warpNames) {
                                player.sendMessage(ChatColor.YELLOW + "- " + warpName);
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Du har ikke tillatelse til å bruke denne kommandoen.");
                    }
                } else if (args.length == 1) {
                    if (player.hasPermission(PERMISSION_WARP)) {
                        String warpName = args[0];
                        Location warpLocation = warpsManager.getWarp(playerUUID, warpName);
                        if (warpLocation == null) {
                            player.sendMessage(ChatColor.RED + "Du har ingen warp '" + warpName + "'!");
                        } else {
                            player.teleport(warpLocation);
                            player.sendMessage(ChatColor.GREEN + "Teleporterte til '" + warpName + "'!");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Du har ikke tillatelse til å bruke denne kommandoen.");
                    }
                } else if (args.length == 2) {
                    if (player.hasPermission(PERMISSION_WARP_SET) || player.hasPermission(PERMISSION_WARP_DELETE)) {
                        String action = args[0];
                        String warpName = args[1];

                        if (action.equalsIgnoreCase("set")) {
                            if (player.hasPermission(PERMISSION_WARP_SET)) {
                                Location warpLocation = player.getLocation();
                                if (warpsManager.hasWarp(playerUUID, warpName)) {
                                    player.sendMessage(ChatColor.RED + "Denne warpen har du allerede laget!");
                                } else {
                                    warpsManager.setWarp(playerUUID, warpName, warpLocation);
                                    player.sendMessage(ChatColor.GREEN + "Warp '" + warpName + "' satt!");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Du har ikke tillatelse til å bruke denne kommandoen.");
                            }
                        } else if (action.equalsIgnoreCase("delete")) {
                            if (player.hasPermission(PERMISSION_WARP_DELETE)) {
                                if (warpsManager.hasWarp(playerUUID, warpName)) {
                                    warpsManager.deleteWarp(playerUUID, warpName);
                                    player.sendMessage(ChatColor.GREEN + "Warp '" + warpName + "' fjernet!");
                                } else {
                                    player.sendMessage(ChatColor.RED + "Du har ingen warps med dette navnet");
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "Du har ikke tillatelse til å bruke denne kommandoen.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "Bruk: /warp [set/delete] <navn>");
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Du har ikke tillatelse til å bruke denne kommandoen.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Bruk: /warp [set/delete] <navn>");
                }
                return true;
            } else if (command.getName().equalsIgnoreCase("warps")) {
                if (player.hasPermission(PERMISSION_WARPS)) {
                    String[] warpNames = warpsManager.getWarpNames(playerUUID);
                    if (warpNames.length == 0) {
                        player.sendMessage(ChatColor.RED + "Du har ingen warps enda, slik kommer du i gang:");
                        player.sendMessage(ChatColor.YELLOW + "/warps" + ChatColor.WHITE + " - Liste av dine warps.");
                        player.sendMessage(ChatColor.YELLOW + "/warp set <navn>" + ChatColor.WHITE + " - Lag en ny warp");
                        player.sendMessage(ChatColor.YELLOW + "/warp delete <navn>" + ChatColor.WHITE + " - Slett en warp.");
                    } else {
                        player.sendMessage(ChatColor.GREEN + "Dine warps:");
                        for (String warpName : warpNames) {
                            player.sendMessage(ChatColor.YELLOW + "- " + warpName);
                        }
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Du har ikke tillatelse til å bruke denne kommandoen.");
                }
                return true;
            }
        }
        return false;
    }
}

