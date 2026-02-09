package org.hwabeag.divesystem.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.hwabeag.divesystem.config.ConfigManager
import org.hwabeag.divesystem.inventorys.DiveSystemAmountSettingGUI
import org.hwabeag.divesystem.inventorys.DiveSystemGUI
import org.hwabeag.divesystem.inventorys.DiveSystemItemSettingGUI
import org.hwabeag.divesystem.util.MessageUtil

class ShopCommand : CommandExecutor, TabCompleter {
    private val diveSystemConfig: FileConfiguration
        get() = ConfigManager.getConfig("dive-system")
    private val playerConfig: FileConfiguration
        get() = ConfigManager.getConfig("player")

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {
        return when (args.size) {
            1 -> listOf("열기", "생성", "제거", "물품설정", "금액설정")
            2 -> diveSystemConfig.getConfigurationSection("잠수상점")?.getKeys(false)?.toList() ?: emptyList()
            else -> null
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val prefix = MessageUtil.prefix()
        if (sender !is Player) {
            Bukkit.getConsoleSender().sendMessage("$prefix 게임안에서만 사용 가능합니다.")
            return true
        }
        val name = sender.name
        if (args.isEmpty()) {
            sender.sendMessage("$prefix /잠수상점 열기 [제목] - 잠수상점을 오픈합니다.")
            if (sender.isOp) {
                sender.sendMessage("$prefix /잠수상점 생성 [제목] [라인] - 잠수상점을 생성합니다. (최대 5라인)")
                sender.sendMessage("$prefix /잠수상점 제거 [제목] - 잠수상점을 제거합니다.")
                sender.sendMessage("$prefix /잠수상점 물품설정 [제목] - 잠수상점 물품을 설정합니다.")
                sender.sendMessage("$prefix /잠수상점 금액설정 [제목] - 잠수상점 물품 금액을 설정합니다.")
            }
            return true
        }

        when {
            args[0].equals("열기", ignoreCase = true) -> {
                if (args.size < 2) {
                    sender.sendMessage("$prefix 제목을 정확하게 적어주세요.")
                    return true
                }
                val title = args[1]
                if (diveSystemConfig.getString("잠수상점.$title") == null) {
                    sender.sendMessage("$prefix $title 이름의 잠수상점이 존재하지 않습니다.")
                    return true
                }
                playerConfig.set("$name.페이지", 0)
                playerConfig.set("$name.구매상점", title)
                ConfigManager.saveConfigs()
                DiveSystemGUI(sender).open(sender)
                return true
            }

            args[0].equals("생성", ignoreCase = true) -> {
                if (!sender.isOp) {
                    sender.sendMessage("$prefix 당신은 권한이 없습니다.")
                    return true
                }
                if (args.size < 2) {
                    sender.sendMessage("$prefix 제목을 정확하게 적어주세요.")
                    return true
                }
                if (args.size < 3 || args[2].toIntOrNull() == null) {
                    sender.sendMessage("$prefix 라인을 정확하게 적어주세요.")
                    return true
                }
                val line = args[2].toInt()
                if (line > 5) {
                    sender.sendMessage("$prefix 라인은 최대 5 까지만 가능합니다.")
                    return true
                }
                val title = args[1]
                if (diveSystemConfig.getString("잠수상점.$title") != null) {
                    sender.sendMessage("$prefix $title 이름의 잠수상점이 이미 존재합니다.")
                    return true
                }
                diveSystemConfig.set("잠수상점.$title.라인", line)
                ConfigManager.saveConfigs()
                sender.sendMessage("$prefix $title 이름의 잠수상점을 생성했습니다.")
                return true
            }

            args[0].equals("제거", ignoreCase = true) -> {
                if (!sender.isOp) {
                    sender.sendMessage("$prefix 당신은 권한이 없습니다.")
                    return true
                }
                if (args.size < 2) {
                    sender.sendMessage("$prefix 제목을 정확하게 적어주세요.")
                    return true
                }
                val title = args[1]
                if (diveSystemConfig.getString("잠수상점.$title") == null) {
                    sender.sendMessage("$prefix $title 이름의 잠수상점이 존재하지 않습니다.")
                    return true
                }
                diveSystemConfig.set("잠수상점.$title", null)
                ConfigManager.saveConfigs()
                sender.sendMessage("$prefix $title 이름의 잠수상점을 제거했습니다.")
                return true
            }

            args[0].equals("물품설정", ignoreCase = true) -> {
                if (!sender.isOp) {
                    sender.sendMessage("$prefix 당신은 권한이 없습니다.")
                    return true
                }
                if (args.size < 2) {
                    sender.sendMessage("$prefix 제목을 정확하게 적어주세요.")
                    return true
                }
                val title = args[1]
                if (diveSystemConfig.getString("잠수상점.$title") == null) {
                    sender.sendMessage("$prefix $title 이름의 잠수상점이 존재하지 않습니다.")
                    return true
                }
                playerConfig.set("$name.페이지", 0)
                playerConfig.set("$name.설정상점", title)
                ConfigManager.saveConfigs()
                DiveSystemItemSettingGUI(sender).open(sender)
                return true
            }

            args[0].equals("금액설정", ignoreCase = true) -> {
                if (!sender.isOp) {
                    sender.sendMessage("$prefix 당신은 권한이 없습니다.")
                    return true
                }
                if (args.size < 2) {
                    sender.sendMessage("$prefix 제목을 정확하게 적어주세요.")
                    return true
                }
                val title = args[1]
                if (diveSystemConfig.getString("잠수상점.$title") == null) {
                    sender.sendMessage("$prefix $title 이름의 잠수상점이 존재하지 않습니다.")
                    return true
                }
                playerConfig.set("$name.페이지", 0)
                playerConfig.set("$name.설정상점", title)
                ConfigManager.saveConfigs()
                DiveSystemAmountSettingGUI(sender).open(sender)
                return true
            }
        }

        sender.sendMessage("$prefix /잠수상점 열기 [제목] - 잠수상점을 오픈합니다.")
        return true
    }
}
