package com.lcweather.managers;

import com.lcweather.LCWeather;
import org.bukkit.ChatColor;
import org.bukkit.WeatherType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ConfigManager {

    private final LCWeather plugin;
    private FileConfiguration config;

    // Cached values for performance
    private boolean timedWeatherEnabled;
    private int timedWeatherInterval;
    private List<String> weatherCycle;
    private int clearDuration;
    private int rainDuration;
    private int thunderDuration;
    private boolean broadcastEnabled;

    public ConfigManager(LCWeather plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        loadValues();
    }

    /**
     * Loads all configuration values into cache
     */
    public void loadValues() {
        this.timedWeatherEnabled = config.getBoolean("timed-weather-change.enabled", false);
        this.timedWeatherInterval = config.getInt("timed-weather-change.interval", 15);
        this.weatherCycle = config.getStringList("timed-weather-change.cycle");
        this.clearDuration = config.getInt("weather-durations.clear", 30);
        this.rainDuration = config.getInt("weather-durations.rain", 20);
        this.thunderDuration = config.getInt("weather-durations.thunder", 10);
        this.broadcastEnabled = config.getBoolean("broadcast.enabled", true);
    }

    /**
     * Reloads configuration from file
     */
    public void reload() {
        this.config = plugin.getConfig();
        loadValues();
    }

    /**
     * Colorizes a message string
     */
    public String colorize(String message) {
        if (message == null) return "";
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    // ==================== Getters ====================

    public boolean isTimedWeatherEnabled() {
        return timedWeatherEnabled;
    }

    public int getTimedWeatherInterval() {
        return timedWeatherInterval;
    }

    public List<String> getWeatherCycle() {
        return weatherCycle;
    }

    public int getClearDuration() {
        return clearDuration;
    }

    public int getRainDuration() {
        return rainDuration;
    }

    public int getThunderDuration() {
        return thunderDuration;
    }

    public boolean isBroadcastEnabled() {
        return broadcastEnabled;
    }

    // ==================== Message Getters ====================

    public String getPrefix() {
        return colorize(config.getString("messages.prefix", "&8[&bLCWeather&8] &r"));
    }

    public String getWeatherChangedMessage(String weather) {
        String msg = config.getString("messages.weather-changed", "&7The weather has been changed to &e{weather}&7.");
        return colorize(msg.replace("{weather}", weather));
    }

    public String getWeatherLockedMessage() {
        return colorize(config.getString("messages.weather-locked", "&7The weather has been &clocked&7. Natural weather changes are now disabled."));
    }

    public String getWeatherUnlockedMessage() {
        return colorize(config.getString("messages.weather-unlocked", "&7The weather has been &aunlocked&7. Natural weather changes are now enabled."));
    }

    public String getNoPermissionMessage() {
        return colorize(config.getString("messages.no-permission", "&cYou do not have permission to use this command."));
    }

    public String getInvalidWeatherMessage() {
        return colorize(config.getString("messages.invalid-weather", "&cInvalid weather type. Use: clear, rain, or thunder."));
    }

    public String getInvalidBooleanMessage() {
        return colorize(config.getString("messages.invalid-boolean", "&cInvalid value. Use: true or false."));
    }

    public String getPlayerOnlyMessage() {
        return colorize(config.getString("messages.player-only", "&cThis command can only be used by players."));
    }

    public String getConfigReloadedMessage() {
        return colorize(config.getString("messages.config-reloaded", "&aConfiguration has been reloaded successfully."));
    }

    public String getUsageSetWeatherMessage() {
        return colorize(config.getString("messages.usage-setweather", "&cUsage: /setweather <clear/rain/thunder>"));
    }

    public String getUsageWeatherLockMessage() {
        return colorize(config.getString("messages.usage-weatherlock", "&cUsage: /weatherlock <true/false>"));
    }

    public String getUsageLCWeatherMessage() {
        return colorize(config.getString("messages.usage-lcweather", "&cUsage: /lcweather <reload>"));
    }

    public String getBroadcastMessage(String weather, String player) {
        String msg = config.getString("broadcast.message", "&7[Server] The weather is now &e{weather}&7!");
        return colorize(msg.replace("{weather}", weather).replace("{player}", player));
    }

    /**
     * Gets the duration in ticks for a specific weather type
     */
    public int getDurationInTicks(WeatherType weatherType) {
        int minutes;
        switch (weatherType) {
            case CLEAR:
                minutes = clearDuration;
                break;
            case DOWNFALL:
                minutes = rainDuration;
                break;
            default:
                minutes = -1;
        }
        
        // Convert minutes to ticks (-1 means infinite)
        return minutes > 0 ? minutes * 60 * 20 : 6000; // Default to 5 minutes if invalid
    }
}
