package org.hwabeag.divesystem.expansions

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.OfflinePlayer
import org.hwabeag.divesystem.DiveSystem
import org.hwabeag.divesystem.data.DatabaseManager

class DiveSystemExpansion(private val plugin: DiveSystem) : PlaceholderExpansion() {
    override fun persist(): Boolean = true

    override fun canRegister(): Boolean = true

    override fun getAuthor(): String = plugin.description.authors.toString()

    override fun getIdentifier(): String = "dive_system"

    override fun getVersion(): String = plugin.description.version

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if (!params.equals("get-index", ignoreCase = true)) {
            return null
        }
        val name = player?.name ?: return null
        return DatabaseManager.getPoints(name).toString()
    }
}
