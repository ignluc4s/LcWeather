package com.lcweather.commands;

import com.lcweather.LCWeather;
import com.lcweather.managers.ConfigManager;
import com.lcweather.managers.WeatherManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetWeatherCommand implements CommandExecutor {

    private final LCWeather plugin;
    private final ConfigManager configManager;
    private final WeatherManager weatherManager;

    public SetWeatherCommand(LCWeather plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.weatherManager = plugin.getWeatherManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission
        if (!sender.hasPermission("lcweather.setweather")) {
            sender.sendMessage(configManager.getPrefix() + configManager.getNoPermissionMessage());
            return true;
        }

        // Check arguments
        if (args.length < 1) {
            sender.sendMessage(configManager.getPrefix() + configManager.getUsageSetWeatherMessage());
            return true;
        }

        String weatherType = args[0].toLowerCase();
        
        // Validate weather type
        if (!isValidWeatherType(weatherType)) {
            sender.sendMessage(configManager.getPrefix() + configManager.getInvalidWeatherMessage());
            return true;
        }

        // Get player name for broadcast
        String playerName = sender instanceof Player ? sender.getName() : "Console";
        
        // Set the weather
        weatherManager.setWeather(weatherType, playerName);
        
        // Send confirmation to sender
        String displayWeather = getDisplayName(weatherType);
        sender.sendMessage(configManager.getPrefix() + configManager.getWeatherChangedMessage(displayWeather));
        
        return true;
    }

    /**
     * Validates if the weather type is valid
     */
    private boolean isValidWeatherType(String weatherType) {
        return weatherType.equals("clear") || 
               weatherType.equals("rain") || 
               weatherType.equals("thunder");
    }

    /**
     * Gets a display name for the weather type
     */
    private String getDisplayName(String weatherType) {
        switch (weatherType.toLowerCase()) {
            case "clear":
                return "Clear";
            case "rain":
                return "Rain";
            case "thunder":
                return "Thunderstorm";
            default:
                return weatherType;
        }
    }
}
