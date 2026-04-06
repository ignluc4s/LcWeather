package com.lcweather;

import com.lcweather.commands.LCWeatherCommand;
import com.lcweather.commands.SetWeatherCommand;
import com.lcweather.commands.WeatherLockCommand;
import com.lcweather.managers.ConfigManager;
import com.lcweather.managers.WeatherManager;
import com.lcweather.tasks.WeatherCycleTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class LCWeather extends JavaPlugin {

    private static LCWeather instance;
    private ConfigManager configManager;
    private WeatherManager weatherManager;
    private WeatherCycleTask weatherCycleTask;

    @Override
    public void onEnable() {
        instance = this;
        
        // Save default config
        saveDefaultConfig();
        
        // Initialize managers
        this.configManager = new ConfigManager(this);
        this.weatherManager = new WeatherManager(this);
        
        // Register commands
        registerCommands();
        
        // Start timed weather cycle if enabled
        startWeatherCycleTask();
        
        getLogger().info("LCWeather has been enabled!");
    }

    @Override
    public void onDisable() {
        // Stop weather cycle task
        stopWeatherCycleTask();
        
        getLogger().info("LCWeather has been disabled!");
    }

    /**
     * Registers all plugin commands
     */
    private void registerCommands() {
        getCommand("setweather").setExecutor(new SetWeatherCommand(this));
        getCommand("weatherlock").setExecutor(new WeatherLockCommand(this));
        getCommand("lcweather").setExecutor(new LCWeatherCommand(this));
    }

    /**
     * Starts the weather cycle task if enabled in config
     */
    public void startWeatherCycleTask() {
        stopWeatherCycleTask();
        
        if (configManager.isTimedWeatherEnabled()) {
            weatherCycleTask = new WeatherCycleTask(this);
            int interval = configManager.getTimedWeatherInterval();
            // Convert minutes to ticks (20 ticks = 1 second, 1200 ticks = 1 minute)
            long ticks = interval * 60L * 20L;
            weatherCycleTask.runTaskTimer(this, ticks, ticks);
            getLogger().info("Weather cycle task started with interval: " + interval + " minutes");
        }
    }

    /**
     * Stops the weather cycle task
     */
    public void stopWeatherCycleTask() {
        if (weatherCycleTask != null) {
            weatherCycleTask.cancel();
            weatherCycleTask = null;
            getLogger().info("Weather cycle task stopped");
        }
    }

    /**
     * Reloads the plugin configuration
     */
    public void reloadPlugin() {
        reloadConfig();
        configManager.reload();
        stopWeatherCycleTask();
        startWeatherCycleTask();
    }

    /**
     * Gets the plugin instance
     */
    public static LCWeather getInstance() {
        return instance;
    }

    /**
     * Gets the config manager
     */
    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Gets the weather manager
     */
    public WeatherManager getWeatherManager() {
        return weatherManager;
    }
}
