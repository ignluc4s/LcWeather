package com.lcweather.commands;

import com.lcweather.LCWeather;
import com.lcweather.managers.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LCWeatherCommand implements CommandExecutor {

    private final LCWeather plugin;
    private final ConfigManager configManager;

    public LCWeatherCommand(LCWeather plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission
        if (!sender.hasPermission("lcweather.admin")) {
            sender.sendMessage(configManager.getPrefix() + configManager.getNoPermissionMessage());
            return true;
        }

        // Check arguments
        if (args.length < 1) {
            sender.sendMessage(configManager.getPrefix() + configManager.getUsageLCWeatherMessage());
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
            case "rl":
                handleReload(sender);
                break;
            case "help":
            case "?":
                sendHelpMessage(sender);
                break;
            case "status":
            case "info":
                sendStatusMessage(sender);
                break;
            default:
                sender.sendMessage(configManager.getPrefix() + configManager.getUsageLCWeatherMessage());
                break;
        }

        return true;
    }

    /**
     * Handles the reload subcommand
     */
    private void handleReload(CommandSender sender) {
        plugin.reloadPlugin();
        sender.sendMessage(configManager.getPrefix() + configManager.getConfigReloadedMessage());
    }

    /**
     * Sends help message to the sender
     */
    private void sendHelpMessage(CommandSender sender) {
        String prefix = configManager.getPrefix();
        sender.sendMessage(prefix + "&7=== &bLCWeather Help &7===");
        sender.sendMessage(prefix + "&e/setweather <clear/rain/thunder> &7- Set the weather");
        sender.sendMessage(prefix + "&e/weatherlock <true/false> &7- Lock/unlock the weather");
        sender.sendMessage(prefix + "&e/lcweather reload &7- Reload the plugin configuration");
        sender.sendMessage(prefix + "&e/lcweather status &7- View plugin status");
        sender.sendMessage(prefix + "&7=======================");
    }

    /**
     * Sends plugin status to the sender
     */
    private void sendStatusMessage(CommandSender sender) {
        String prefix = configManager.getPrefix();
        sender.sendMessage(prefix + "&7=== &bLCWeather Status &7===");
        sender.sendMessage(prefix + "&7Version: &e1.0.0");
        sender.sendMessage(prefix + "&7Timed Weather: " + (configManager.isTimedWeatherEnabled() ? "&aEnabled" : "&cDisabled"));
        if (configManager.isTimedWeatherEnabled()) {
            sender.sendMessage(prefix + "&7Interval: &e" + configManager.getTimedWeatherInterval() + " minutes");
        }
        sender.sendMessage(prefix + "&7Broadcast: " + (configManager.isBroadcastEnabled() ? "&aEnabled" : "&cDisabled"));
        sender.sendMessage(prefix + "&7=======================");
    }
}
