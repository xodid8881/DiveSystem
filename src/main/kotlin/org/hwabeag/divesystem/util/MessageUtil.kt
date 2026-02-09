package org.hwabeag.divesystem.util

import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.hwabeag.divesystem.config.ConfigManager

object MessageUtil {
    private val diveSystemConfig: FileConfiguration
        get() = ConfigManager.getConfig("dive-system")

    fun prefix(): String {
        val raw = diveSystemConfig.getString("dive-system.prefix") ?: "&a&l[잠수]&7"
        return ChatColor.translateAlternateColorCodes('&', raw)
    }
}
