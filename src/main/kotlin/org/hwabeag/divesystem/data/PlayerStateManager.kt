package org.hwabeag.divesystem.data

import org.bukkit.configuration.file.FileConfiguration
import org.hwabeag.divesystem.config.ConfigManager

object PlayerStateManager {
    private val playerConfig: FileConfiguration
        get() = ConfigManager.getConfig("player")

    fun initializeDefaults(name: String) {
        if (playerConfig.getString("$name.페이지") != null) {
            return
        }
        playerConfig.addDefault(name, "")
        playerConfig.set("$name.잠수정보", false)
        playerConfig.set("$name.페이지", 0)
        playerConfig.set("$name.구매상점", "없음")
        playerConfig.set("$name.구매슬롯", 0)
        playerConfig.set("$name.설정상점", "없음")
        playerConfig.set("$name.금액설정", false)
        playerConfig.set("$name.설정슬롯", 0)
        ConfigManager.saveConfigs()
    }
}
