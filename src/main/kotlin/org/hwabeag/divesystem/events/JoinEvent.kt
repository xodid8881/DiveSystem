package org.hwabeag.divesystem.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.hwabeag.divesystem.config.ConfigManager
import org.hwabeag.divesystem.data.DatabaseManager
import org.hwabeag.divesystem.data.PlayerStateManager

class JoinEvent : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val name = event.player.name
        val playerConfig = ConfigManager.getConfig("player")
        PlayerStateManager.initializeDefaults(name)
        DatabaseManager.ensurePlayer(name)
        val legacyPoints = playerConfig.getInt("$name.잠수", 0)
        if (legacyPoints > 0 && DatabaseManager.getPoints(name) == 0) {
            DatabaseManager.setPoints(name, legacyPoints)
        }
        playerConfig.set("$name.잠수", null)
        ConfigManager.saveConfigs()
    }
}
