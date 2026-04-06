package com.lcweather.commands;

import com.lcweather.LCWeather;
import com.lcweather.managers.ConfigManager;
import com.lcweather.managers.WeatherManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WeatherLockCommand implements CommandExecutor {

    private final LCWeather plugin;
    private final ConfigManager configManager;
    private final WeatherManager weatherManager;

    public WeatherLockCommand(LCWeather plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.weatherManager = plugin.getWeatherManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission
        if (!sender.hasPermission("lcweather.weatherlock")) {
            sender.sendMessage(configManager.getPrefix() + configManager.getNoPermissionMessage());
            return true;
        }

        // Check arguments
        if (args.length < 1) {
            sender.sendMessage(configManager.getPrefix() + configManager.getUsageWeatherLockMessage());
            return true;
        }

        String lockValue = args[0].toLowerCase();
        
        // Parse boolean value
        Boolean locked;
        if (lockValue.equals("true") || lockValue.equals("on") || lockValue.equals("yes") || lockValue.equals("1")) {
            locked = true;
        } else if (lockValue.equals("false") || lockValue.equals("off") || lockValue.equals("no") || lockValue.equals("0")) {
            locked = false;
        } else {
            sender.sendMessage(configManager.getPrefix() + configManager.getInvalidBooleanMessage());
            return true;
        }

        // Get player (null if console)
        Player player = sender instanceof Player ? (Player) sender : null;
        
        // Set the weather lock
        weatherManager.setWeatherLock(locked, player);
        
        return true;
    }
}
