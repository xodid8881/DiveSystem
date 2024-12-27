package org.hwabeag.divesystem;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.hwabeag.divesystem.commands.AmountCommand;
import org.hwabeag.divesystem.commands.DiveCommand;
import org.hwabeag.divesystem.commands.ShopCommand;
import org.hwabeag.divesystem.config.ConfigManager;
import org.hwabeag.divesystem.events.InvClickEvent;
import org.hwabeag.divesystem.events.InvCloseEvent;
import org.hwabeag.divesystem.events.JoinEvent;
import org.hwabeag.divesystem.events.MoveEvent;
import org.hwabeag.divesystem.expansions.DiveSystemExpansion;
import org.hwabeag.divesystem.schedules.DiveCheckTask;

import java.util.Objects;

public final class DiveSystem extends JavaPlugin {

    private static ConfigManager configManager;

    private FileConfiguration config;

    public static DiveSystem getPlugin() {
        return JavaPlugin.getPlugin(DiveSystem.class);
    }

    public static void getConfigManager() {
        if (configManager == null)
            configManager = new ConfigManager();
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new InvClickEvent(), this);
        getServer().getPluginManager().registerEvents(new InvCloseEvent(), this);
        getServer().getPluginManager().registerEvents(new JoinEvent(), this);
        getServer().getPluginManager().registerEvents(new MoveEvent(), this);
    }

    private void registerCommands() {
        Objects.requireNonNull(getServer().getPluginCommand("잠수금액")).setExecutor(new AmountCommand());
        Objects.requireNonNull(getServer().getPluginCommand("잠수포인트")).setExecutor(new DiveCommand());
        Objects.requireNonNull(getServer().getPluginCommand("잠수상점")).setExecutor(new ShopCommand());
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("[DiveSystem] Enable");
        getConfigManager();
        registerCommands();
        registerEvents();
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new DiveCheckTask(), 20*20, 20*20);
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new DiveSystemExpansion(this).register();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("[DiveSystem] Disable");
        ConfigManager.saveConfigs();
    }
}
