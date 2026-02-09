package org.hwabeag.divesystem.commands

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.hwabeag.divesystem.data.DatabaseManager
import org.hwabeag.divesystem.util.MessageUtil

class DiveCommand : CommandExecutor, TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<out String>
    ): List<String>? {
        return when (args.size) {
            1 -> listOf("지급", "회수", "설정", "확인")
            2 -> Bukkit.getOnlinePlayers().map { it.name }
            else -> null
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val prefix = MessageUtil.prefix()
        if (sender !is Player) {
            Bukkit.getConsoleSender().sendMessage("$prefix 게임안에서만 사용 가능합니다.")
            return true
        }
        if (args.isEmpty()) {
            if (sender.isOp) {
                sender.sendMessage("$prefix /잠수포인트 지급 [닉네임] [정도] - 잠수포인트를 지급합니다.")
                sender.sendMessage("$prefix /잠수포인트 회수 [닉네임] [정도] - 잠수포인트를 회수합니다.")
                sender.sendMessage("$prefix /잠수포인트 설정 [닉네임] [정도] - 잠수포인트를 설정합니다.")
            }
            sender.sendMessage("$prefix /잠수포인트 확인 [닉네임] - 잠수포인트를 확인합니다.")
            return true
        }

        if (!sender.isOp && !args[0].equals("확인", ignoreCase = true)) {
            sender.sendMessage("$prefix /잠수포인트 확인 [닉네임] - 잠수포인트를 확인합니다.")
            return true
        }

        when {
            args[0].equals("지급", ignoreCase = true) -> {
                if (args.size < 2) {
                    sender.sendMessage("$prefix 닉네임을 적어주세요.")
                    return true
                }
                if (args.size < 3 || args[2].toIntOrNull() == null) {
                    sender.sendMessage("$prefix 정도를 정확하게 적어주세요.")
                    return true
                }
                val targetName = args[1]
                if (!DatabaseManager.hasPlayer(targetName)) {
                    sender.sendMessage("$prefix $targetName 닉네임의 유저는 존재하지 않습니다.")
                    return true
                }
                val delta = args[2].toInt()
                val updated = DatabaseManager.addPoints(targetName, delta)
                sender.sendMessage("$prefix $targetName 님에게 ${args[2]} .P 를 지급했습니다.")
                sender.sendMessage("$prefix 해당 유저 보유 잠수포인트 : ${updated}.P")
                Bukkit.getPlayerExact(targetName)?.apply {
                    sendMessage("$prefix 운영진으로부터 ${args[2]} .P 를 지급받았습니다.")
                    sendMessage("$prefix 보유 잠수포인트 : ${updated}.P")
                }
                return true
            }

            args[0].equals("회수", ignoreCase = true) -> {
                if (args.size < 2) {
                    sender.sendMessage("$prefix 닉네임을 적어주세요.")
                    return true
                }
                if (args.size < 3 || args[2].toIntOrNull() == null) {
                    sender.sendMessage("$prefix 정도를 정확하게 적어주세요.")
                    return true
                }
                val targetName = args[1]
                if (!DatabaseManager.hasPlayer(targetName)) {
                    sender.sendMessage("$prefix $targetName 닉네임의 유저는 존재하지 않습니다.")
                    return true
                }
                val delta = args[2].toInt()
                val current = DatabaseManager.getPoints(targetName)
                if (current < delta) {
                    sender.sendMessage("$prefix $targetName 유저의 잠수포인트 보유량보다 회수금액이 큽니다.")
                    return true
                }
                val updated = current - delta
                DatabaseManager.setPoints(targetName, updated)
                sender.sendMessage("$prefix $targetName 님에게 ${args[2]} .P 를 회수했습니다.")
                sender.sendMessage("$prefix 해당 유저 보유 잠수포인트 : ${updated}.P")
                Bukkit.getPlayerExact(targetName)?.apply {
                    sendMessage("$prefix 운영진으로부터 ${args[2]} .P 를 회수당했습니다.")
                    sendMessage("$prefix 보유 잠수포인트 : ${updated}.P")
                }
                return true
            }

            args[0].equals("설정", ignoreCase = true) -> {
                if (args.size < 2) {
                    sender.sendMessage("$prefix 닉네임을 적어주세요.")
                    return true
                }
                if (args.size < 3 || args[2].toIntOrNull() == null) {
                    sender.sendMessage("$prefix 정도를 정확하게 적어주세요.")
                    return true
                }
                val targetName = args[1]
                if (!DatabaseManager.hasPlayer(targetName)) {
                    sender.sendMessage("$prefix $targetName 닉네임의 유저는 존재하지 않습니다.")
                    return true
                }
                val amount = args[2].toInt()
                DatabaseManager.setPoints(targetName, amount)
                sender.sendMessage("$prefix $targetName 님의 잠수포인트 보유량을 ${args[2]} .P 로 설정했습니다.")
                sender.sendMessage("$prefix 해당 유저 보유 잠수포인트 : ${args[2]}.P")
                Bukkit.getPlayerExact(targetName)?.apply {
                    sendMessage("$prefix 운영진이 당신의 잠수포인트 보유량을 ${args[2]} .P 로 변경했습니다.")
                    sendMessage("$prefix 보유 잠수포인트 : ${args[2]}.P")
                }
                return true
            }

            args[0].equals("확인", ignoreCase = true) -> {
                val targetName = if (args.size >= 2 && sender.isOp) args[1] else sender.name
                if (!DatabaseManager.hasPlayer(targetName)) {
                    sender.sendMessage("$prefix $targetName 닉네임의 유저는 존재하지 않습니다.")
                    return true
                }
                val point = DatabaseManager.getPoints(targetName)
                if (targetName == sender.name) {
                    sender.sendMessage("$prefix 당신의 보유 잠수포인트 : ${point}.P")
                } else {
                    sender.sendMessage("$prefix $targetName 님의 보유 잠수포인트 : ${point}.P")
                }
                return true
            }
        }

        sender.sendMessage("$prefix /잠수포인트 지급 [닉네임] [정도] - 잠수포인트를 지급합니다.")
        sender.sendMessage("$prefix /잠수포인트 회수 [닉네임] [정도] - 잠수포인트를 회수합니다.")
        sender.sendMessage("$prefix /잠수포인트 설정 [닉네임] [정도] - 잠수포인트를 설정합니다.")
        sender.sendMessage("$prefix /잠수포인트 확인 [닉네임] - 잠수포인트를 확인합니다.")
        return true
    }
}
