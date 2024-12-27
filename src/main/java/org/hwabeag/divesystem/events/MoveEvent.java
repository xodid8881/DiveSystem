package org.hwabeag.divesystem.events;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.hwabeag.divesystem.config.ConfigManager;

import java.util.Objects;

public class MoveEvent implements Listener {
    FileConfiguration DiveSystemConfig = ConfigManager.getConfig("dive-system");
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");
    String Prefix = ChatColor.translateAlternateColorCodes('&', (String) Objects.requireNonNull(DiveSystemConfig.getString("dive-system.prefix")));

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String name = player.getName();
        if (!PlayerConfig.getBoolean(name + ".잠수정보")) return;
        PlayerConfig.set(name + ".잠수정보", false);
        ConfigManager.saveConfigs();
        player.sendActionBar(Prefix + " 움직임이 있어 잠수가 취소되었습니다.");
    }
}
