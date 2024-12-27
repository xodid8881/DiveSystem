package org.hwabeag.divesystem.schedules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.hwabeag.divesystem.config.ConfigManager;

import java.util.Objects;

public class DiveCheckTask implements Runnable {
    FileConfiguration DiveSystemConfig = ConfigManager.getConfig("dive-system");
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");
    String Prefix = ChatColor.translateAlternateColorCodes('&', (String)Objects.requireNonNull(DiveSystemConfig.getString("dive-system.prefix")));

    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            String name = player.getName();
            if (!PlayerConfig.getBoolean(name + ".잠수정보")) {
                World world = player.getWorld();
                String worldname = world.getWorldFolder().getName();
                PlayerConfig.set(name + ".잠수정보", true);
                player.sendActionBar(Prefix + " 잠수를 시작합니다.");
                for (String key : Objects.requireNonNull(DiveSystemConfig.getConfigurationSection("dive-system.dive-no-world")).getKeys(false)) {
                    if (Objects.equals(key, worldname)) {
                        PlayerConfig.set(name + ".잠수정보", false);
                        player.sendActionBar(Prefix + " 해당 월드는 잠수가 진행되지 않습니다.");
                    }
                }
                ConfigManager.saveConfigs();
            } else {
                World world = player.getWorld();
                String worldname = world.getWorldFolder().getName();
                for (String key : Objects.requireNonNull(DiveSystemConfig.getConfigurationSection("dive-system.dive-no-world")).getKeys(false)) {
                    if (Objects.equals(key, worldname)) {
                        PlayerConfig.set(name + ".잠수정보", false);
                        player.sendActionBar(Prefix + " 해당 월드는 잠수가 진행되지 않습니다.");
                    }
                    ConfigManager.saveConfigs();
                }
                if (PlayerConfig.getBoolean(name + ".잠수정보")) {
                    int point = PlayerConfig.getInt(name + ".잠수");
                    int addpoint = DiveSystemConfig.getInt("dive-system.one-minute-point");
                    PlayerConfig.set(name + ".잠수", (point+addpoint));
                    ConfigManager.saveConfigs();
                    player.sendActionBar(Prefix + " 잠수포인트 +" + addpoint + ".P");
                }
            }
        }
    }
}

