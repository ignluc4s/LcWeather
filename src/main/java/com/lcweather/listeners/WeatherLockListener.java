package com.lcweather.listeners;

import com.lcweather.LCWeather;
import com.lcweather.managers.WeatherManager;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;

/**
 * Listener to enforce weather lock and prevent natural weather changes
 */
public class WeatherLockListener implements Listener {

    private final LCWeather plugin;
    private final WeatherManager weatherManager;

    public WeatherLockListener(LCWeather plugin) {
        this.plugin = plugin;
        this.weatherManager = plugin.getWeatherManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent event) {
        World world = event.getWorld();
        
        // Check if weather is locked for this world
        if (weatherManager.isWeatherLocked(world)) {
            // Cancel the event to prevent natural weather changes
            event.setCancelled(true);
            
            // Re-enforce the locked weather state
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                weatherManager.enforceLockedWeather(world);
            }, 1L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onThunderChange(ThunderChangeEvent event) {
        World world = event.getWorld();
        
        // Check if weather is locked for this world
        if (weatherManager.isWeatherLocked(world)) {
            // Cancel the event to prevent natural thunder changes
            event.setCancelled(true);
            
            // Re-enforce the locked weather state
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                weatherManager.enforceLockedWeather(world);
            }, 1L);
        }
    }
}
