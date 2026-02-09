package org.hwabeag.divesystem

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.hwabeag.divesystem.commands.AmountCommand
import org.hwabeag.divesystem.commands.DiveCommand
import org.hwabeag.divesystem.commands.ShopCommand
import org.hwabeag.divesystem.config.ConfigManager
import org.hwabeag.divesystem.data.DatabaseManager
import org.hwabeag.divesystem.events.InvClickEvent
import org.hwabeag.divesystem.events.InvCloseEvent
import org.hwabeag.divesystem.events.JoinEvent
import org.hwabeag.divesystem.events.MoveEvent
import org.hwabeag.divesystem.expansions.DiveSystemExpansion
import org.hwabeag.divesystem.schedules.DiveCheckTask

class DiveSystem : JavaPlugin() {
    companion object {
        lateinit var instance: DiveSystem
            private set
    }

    override fun onEnable() {
        instance = this
        logger.info("[DiveSystem] Enable")

        ConfigManager.initialize(this)
        DatabaseManager.initialize(this)

        registerCommands()
        registerEvents()

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, DiveCheckTask(), 20L * 20, 20L * 20)

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            DiveSystemExpansion(this).register()
        }
    }

    override fun onDisable() {
        logger.info("[DiveSystem] Disable")
        ConfigManager.saveConfigs()
        DatabaseManager.shutdown()
    }

    private fun registerEvents() {
        val pm = server.pluginManager
        pm.registerEvents(InvClickEvent(), this)
        pm.registerEvents(InvCloseEvent(), this)
        pm.registerEvents(JoinEvent(), this)
        pm.registerEvents(MoveEvent(), this)
    }

    private fun registerCommands() {
        val diveCommand = DiveCommand()
        val shopCommand = ShopCommand()
        val amountCommand = AmountCommand()

        getCommand("잠수포인트")?.apply {
            setExecutor(diveCommand)
            tabCompleter = diveCommand
        }
        getCommand("잠수상점")?.apply {
            setExecutor(shopCommand)
            tabCompleter = shopCommand
        }
        getCommand("잠수금액")?.setExecutor(amountCommand)
    }
}
