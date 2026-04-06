package com.lcweather.tasks;

import com.lcweather.LCWeather;
import com.lcweather.managers.WeatherManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task that automatically cycles through weather types based on configuration
 */
public class WeatherCycleTask extends BukkitRunnable {

    private final LCWeather plugin;
    private final WeatherManager weatherManager;

    public WeatherCycleTask(LCWeather plugin) {
        this.plugin = plugin;
        this.weatherManager = plugin.getWeatherManager();
    }

    @Override
    public void run() {
        // Get the first normal world to determine current weather
        World world = null;
        for (World w : Bukkit.getWorlds()) {
            if (w.getEnvironment() == World.Environment.NORMAL) {
                world = w;
                break;
            }
        }

        if (world == null) {
            return;
        }

        // Get current weather and determine next in cycle
        String currentWeather = weatherManager.getCurrentWeather(world);
        String nextWeather = weatherManager.getNextWeatherInCycle(currentWeather);

        // Apply the new weather (System is the changer, no broadcast to avoid spam)
        weatherManager.setWeather(nextWeather, "System", plugin.getConfigManager().isBroadcastEnabled());

        plugin.getLogger().info("Automatic weather cycle changed weather to: " + nextWeather);
    }
}
