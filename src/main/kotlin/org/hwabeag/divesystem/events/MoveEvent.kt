package org.hwabeag.divesystem.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.hwabeag.divesystem.config.ConfigManager
import org.hwabeag.divesystem.util.MessageUtil

class MoveEvent : Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val playerConfig = ConfigManager.getConfig("player")
        val name = player.name
        if (!playerConfig.getBoolean("$name.잠수정보")) {
            return
        }
        playerConfig.set("$name.잠수정보", false)
        ConfigManager.saveConfigs()
        player.sendActionBar("${MessageUtil.prefix()} 움직임이 있어 잠수가 취소되었습니다.")
    }
}
