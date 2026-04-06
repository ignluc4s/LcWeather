package com.lcweather.managers;

import com.lcweather.LCWeather;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WeatherManager {

    private final LCWeather plugin;
    private final ConfigManager configManager;
    
    // Track locked worlds and their weather state
    private final Map<UUID, Boolean> lockedWorlds;
    private final Map<UUID, String> lockedWeatherType;

    public WeatherManager(LCWeather plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.lockedWorlds = new HashMap<>();
        this.lockedWeatherType = new HashMap<>();
    }

    /**
     * Sets the weather for all worlds
     */
    public void setWeather(String weatherType, String playerName) {
        setWeather(weatherType, playerName, true);
    }

    /**
     * Sets the weather for all worlds with option to broadcast
     */
    public void setWeather(String weatherType, String playerName, boolean broadcast) {
        String upperWeather = weatherType.toUpperCase();
        
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL) {
                applyWeatherToWorld(world, upperWeather);
            }
        }
        
        // Broadcast message if enabled
        if (broadcast && configManager.isBroadcastEnabled()) {
            String displayWeather = getDisplayName(upperWeather);
            String message = configManager.getBroadcastMessage(displayWeather, playerName);
            Bukkit.broadcastMessage(message);
        }
    }

    /**
     * Applies weather to a specific world
     */
    private void applyWeatherToWorld(World world, String weatherType) {
        int duration = getDurationTicks(weatherType);
        
        switch (weatherType) {
            case "CLEAR":
                world.setStorm(false);
                world.setThundering(false);
                break;
            case "RAIN":
                world.setStorm(true);
                world.setThundering(false);
                break;
            case "THUNDER":
                world.setStorm(true);
                world.setThundering(true);
                break;
        }
        
        // Store the locked weather type for this world
        lockedWeatherType.put(world.getUID(), weatherType);
    }

    /**
     * Locks or unlocks the weather for all worlds
     */
    public void setWeatherLock(boolean locked, Player player) {
        for (World world : Bukkit.getWorlds()) {
            if (world.getEnvironment() == World.Environment.NORMAL) {
                lockedWorlds.put(world.getUID(), locked);
                
                if (locked) {
                    // Store current weather state
                    String currentWeather = getCurrentWeather(world);
                    lockedWeatherType.put(world.getUID(), currentWeather);
                }
            }
        }
        
        // Send message to player
        if (player != null) {
            String message = locked ? configManager.getWeatherLockedMessage() 
                                    : configManager.getWeatherUnlockedMessage();
            player.sendMessage(configManager.getPrefix() + message);
        }
    }

    /**
     * Checks if weather is locked for a world
     */
    public boolean isWeatherLocked(World world) {
        return lockedWorlds.getOrDefault(world.getUID(), false);
    }

    /**
     * Gets the locked weather type for a world
     */
    public String getLockedWeatherType(World world) {
        return lockedWeatherType.getOrDefault(world.getUID(), "CLEAR");
    }

    /**
     * Enforces locked weather on a world
     */
    public void enforceLockedWeather(World world) {
        if (isWeatherLocked(world)) {
            String lockedWeather = getLockedWeatherType(world);
            String currentWeather = getCurrentWeather(world);
            
            if (!lockedWeather.equals(currentWeather)) {
                applyWeatherToWorld(world, lockedWeather);
            }
        }
    }

    /**
     * Gets the current weather type of a world
     */
    public String getCurrentWeather(World world) {
        if (world.isThundering()) {
            return "THUNDER";
        } else if (world.hasStorm()) {
            return "RAIN";
        } else {
            return "CLEAR";
        }
    }

    /**
     * Gets the duration in ticks based on weather type from config
     */
    private int getDurationTicks(String weatherType) {
        int minutes;
        switch (weatherType) {
            case "CLEAR":
                minutes = configManager.getClearDuration();
                break;
            case "RAIN":
                minutes = configManager.getRainDuration();
                break;
            case "THUNDER":
                minutes = configManager.getThunderDuration();
                break;
            default:
                minutes = -1;
        }
        
        // Convert minutes to ticks, default to 10 minutes if -1
        return minutes > 0 ? minutes * 60 * 20 : 12000;
    }

    /**
     * Gets a display name for the weather type
     */
    private String getDisplayName(String weatherType) {
        switch (weatherType) {
            case "CLEAR":
                return "Clear";
            case "RAIN":
                return "Rain";
            case "THUNDER":
                return "Thunderstorm";
            default:
                return weatherType;
        }
    }

    /**
     * Gets the next weather type in the cycle
     */
    public String getNextWeatherInCycle(String currentWeather) {
        java.util.List<String> cycle = configManager.getWeatherCycle();
        if (cycle == null || cycle.isEmpty()) {
            return "CLEAR";
        }
        
        int currentIndex = -1;
        for (int i = 0; i < cycle.size(); i++) {
            if (cycle.get(i).equalsIgnoreCase(currentWeather)) {
                currentIndex = i;
                break;
            }
        }
        
        if (currentIndex == -1 || currentIndex >= cycle.size() - 1) {
            return cycle.get(0).toUpperCase();
        }
        
        return cycle.get(currentIndex + 1).toUpperCase();
    }
}
