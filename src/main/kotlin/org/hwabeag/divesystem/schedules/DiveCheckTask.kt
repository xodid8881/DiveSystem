package org.hwabeag.divesystem.schedules

import org.bukkit.Bukkit
import org.hwabeag.divesystem.config.ConfigManager
import org.hwabeag.divesystem.data.DatabaseManager
import org.hwabeag.divesystem.util.MessageUtil

class DiveCheckTask : Runnable {
    override fun run() {
        val diveSystemConfig = ConfigManager.getConfig("dive-system")
        val playerConfig = ConfigManager.getConfig("player")
        val prefix = MessageUtil.prefix()

        for (player in Bukkit.getOnlinePlayers()) {
            val name = player.name
            val worldName = player.world.worldFolder.name
            if (!playerConfig.getBoolean("$name.잠수정보")) {
                playerConfig.set("$name.잠수정보", true)
                player.sendActionBar("${prefix} 잠수를 시작합니다.")
                diveSystemConfig.getConfigurationSection("dive-system.dive-no-world")?.getKeys(false)?.forEach { key ->
                    if (key == worldName) {
                        playerConfig.set("$name.잠수정보", false)
                        player.sendActionBar("${prefix} 해당 월드는 잠수가 진행되지 않습니다.")
                    }
                }
                ConfigManager.saveConfigs()
                continue
            }

            diveSystemConfig.getConfigurationSection("dive-system.dive-no-world")?.getKeys(false)?.forEach { key ->
                if (key == worldName) {
                    playerConfig.set("$name.잠수정보", false)
                    player.sendActionBar("${prefix} 해당 월드는 잠수가 진행되지 않습니다.")
                }
            }
            ConfigManager.saveConfigs()

            if (playerConfig.getBoolean("$name.잠수정보")) {
                val addPoint = diveSystemConfig.getInt("dive-system.one-minute-point")
                val updated = DatabaseManager.addPoints(name, addPoint)
                player.sendActionBar("${prefix} 잠수포인트 +${addPoint}.P (보유: ${updated}.P)")
            }
        }
    }
}
