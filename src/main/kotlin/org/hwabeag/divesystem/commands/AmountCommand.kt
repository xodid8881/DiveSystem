package org.hwabeag.divesystem.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.hwabeag.divesystem.DiveSystem
import org.hwabeag.divesystem.config.ConfigManager
import org.hwabeag.divesystem.inventorys.DiveSystemAmountSettingGUI
import org.hwabeag.divesystem.util.MessageUtil

class AmountCommand : CommandExecutor {
    private val diveSystemConfig: FileConfiguration
        get() = ConfigManager.getConfig("dive-system")
    private val playerConfig: FileConfiguration
        get() = ConfigManager.getConfig("player")

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val prefix = MessageUtil.prefix()
        if (sender !is Player) {
            Bukkit.getConsoleSender().sendMessage("$prefix 게임안에서만 사용 가능합니다.")
            return true
        }
        if (!sender.isOp) {
            sender.sendMessage("$prefix 당신은 권한이 없습니다.")
            return true
        }
        if (args.isEmpty() || args[0].toIntOrNull() == null) {
            sender.sendMessage("$prefix /잠수금액 [정도] - 금액을 설정합니다.")
            return true
        }
        val name = sender.name
        if (!playerConfig.getBoolean("$name.금액설정")) {
            return false
        }
        val page = playerConfig.getInt("$name.페이지")
        val shopName = playerConfig.getString("$name.설정상점") ?: return true
        val slot = playerConfig.getInt("$name.설정슬롯")
        diveSystemConfig.set("잠수상점.$shopName.금액.$page.$slot", args[0].toInt())
        playerConfig.set("$name.금액설정", false)
        playerConfig.set("$name.설정슬롯", 0)
        ConfigManager.saveConfigs()
        sender.sendMessage("$prefix 해당 구매 금액을 ${args[0]}원으로 설정했습니다.")
        Bukkit.getScheduler().runTaskLater(DiveSystem.instance, Runnable {
            DiveSystemAmountSettingGUI(sender).open(sender)
        }, 20L * 2)
        return true
    }
}
