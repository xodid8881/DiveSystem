package org.hwabeag.divesystem.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.hwabeag.divesystem.DiveSystem;

import java.util.HashMap;

public class ConfigManager {
    private static final DiveSystem plugin = DiveSystem.getPlugin();

    private static final HashMap<String, ConfigMaker> configSet = new HashMap<>();

    public ConfigManager() {
        String path = plugin.getDataFolder().getAbsolutePath();
        configSet.put("dive-system", new ConfigMaker(path, "DiveSystem.yml"));
        configSet.put("player", new ConfigMaker(path, "Player.yml"));
        loadSettings();
        saveConfigs();
    }

    public static void reloadConfigs() {
        for (String key : configSet.keySet()){
            plugin.getLogger().info(key);
            configSet.get(key).reloadConfig();
        }
    }

    public static void saveConfigs(){
        for (String key : configSet.keySet())
            configSet.get(key).saveConfig();
    }

    public static FileConfiguration getConfig(String fileName) {
        return configSet.get(fileName).getConfig();
    }

    public static void loadSettings(){
        FileConfiguration StyleSystemConfig = getConfig("dive-system");
        StyleSystemConfig.options().copyDefaults(true);
        StyleSystemConfig.addDefault("dive-system.prefix", "&a&l[DiveSystem]&7");
        StyleSystemConfig.addDefault("dive-system.one-minute-point", 1);
        StyleSystemConfig.addDefault("dive-system.dive-no-world.test", "test");
    }
}