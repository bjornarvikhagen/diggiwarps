package org.diggilounge.diggiwarps;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WarpsManager {
    private JavaPlugin plugin;
    private FileConfiguration config;
    private Map<UUID, Map<String, Location>> warps;

    public WarpsManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.warps = new HashMap<>();
        loadWarps();
    }

    public boolean hasWarp(UUID playerUUID, String warpName) {
        if (warps.containsKey(playerUUID)) {
            Map<String, Location> playerWarps = warps.get(playerUUID);
            return playerWarps.containsKey(warpName);
        } else {
            return false;
        }
    }

    public Location getWarp(UUID playerUUID, String warpName) {
        if (warps.containsKey(playerUUID)) {
            Map<String, Location> playerWarps = warps.get(playerUUID);
            return playerWarps.get(warpName);
        } else {
            return null;
        }
    }

    public String[] getWarpNames(UUID playerUUID) {
        if (warps.containsKey(playerUUID)) {
            Map<String, Location> playerWarps = warps.get(playerUUID);
            Set<String> warpNames = playerWarps.keySet();
            return warpNames.toArray(new String[0]);
        } else {
            return new String[0];
        }
    }

    public void setWarp(UUID playerUUID, String warpName, Location location) {
        Map<String, Location> playerWarps = warps.computeIfAbsent(playerUUID, k -> new HashMap<>());
        playerWarps.put(warpName, location);
        saveWarps();
    }

    public void deleteWarp(UUID playerUUID, String warpName) {
        if (warps.containsKey(playerUUID)) {
            Map<String, Location> playerWarps = warps.get(playerUUID);
            playerWarps.remove(warpName);
            String path = "warps." + playerUUID.toString() + "." + warpName;
            config.set(path, null);
            saveWarps();
        }
    }

    private void loadWarps() {
        ConfigurationSection warpsSection = plugin.getConfig().getConfigurationSection("warps");
        if (warpsSection == null) {
            return;
        }

        for (String playerUUIDString : warpsSection.getKeys(false)) {
            UUID playerUUID = UUID.fromString(playerUUIDString);
            ConfigurationSection playerWarpsSection = warpsSection.getConfigurationSection(playerUUIDString);
            if (playerWarpsSection == null) {
                continue;
            }

            Map<String, Location> playerWarps = new HashMap<>();
            for (String warpName : playerWarpsSection.getKeys(false)) {
                Location warpLocation = (Location) playerWarpsSection.get(warpName);
                playerWarps.put(warpName, warpLocation);
            }

            warps.put(playerUUID, playerWarps);
        }
    }


    private void saveWarps() {
        for (Map.Entry<UUID, Map<String, Location>> playerEntry : warps.entrySet()) {
            UUID playerUUID = playerEntry.getKey();
            Map<String, Location> playerWarps = playerEntry.getValue();
            for (Map.Entry<String, Location> warpEntry : playerWarps.entrySet()) {
                String warpName = warpEntry.getKey();
                Location warpLocation = warpEntry.getValue();
                String path = "warps." + playerUUID.toString() + "." + warpName;
                config.set(path, warpLocation);
            }
        }
        plugin.saveConfig();
    }

}
