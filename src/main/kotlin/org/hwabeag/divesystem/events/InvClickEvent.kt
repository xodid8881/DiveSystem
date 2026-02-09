package org.hwabeag.divesystem.events

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.hwabeag.divesystem.DiveSystem
import org.hwabeag.divesystem.config.ConfigManager
import org.hwabeag.divesystem.data.DatabaseManager
import org.hwabeag.divesystem.inventorys.DiveSystemAmountSettingGUI
import org.hwabeag.divesystem.inventorys.DiveSystemGUI
import org.hwabeag.divesystem.inventorys.DiveSystemItemSettingGUI
import org.hwabeag.divesystem.inventorys.PurchaseGUI
import org.hwabeag.divesystem.util.MessageUtil

class InvClickEvent : Listener {
    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (event.clickedInventory == null) {
            return
        }
        val current = event.currentItem ?: return
        val player = event.whoClicked as? Player ?: return
        val diveSystemConfig = ConfigManager.getConfig("dive-system")
        val playerConfig = ConfigManager.getConfig("player")
        val name = player.name
        var playerShop = playerConfig.getString("$name.설정상점") ?: "없음"
        val clickItemName = current.itemMeta?.displayName ?: return
        val title = event.view.title

        if (title == "${playerShop}물품설정") {
            if (clickItemName == color("&a이전 페이지")) {
                event.inventory.clear()
                player.closeInventory()
                playerConfig.set("$name.페이지", playerConfig.getInt("$name.페이지") - 1)
                ConfigManager.saveConfigs()
                Bukkit.getScheduler().runTaskLater(DiveSystem.instance, Runnable {
                    DiveSystemItemSettingGUI(player).open(player)
                }, 20L)
            }
            if (clickItemName == color("&a다음 페이지")) {
                event.inventory.clear()
                player.closeInventory()
                playerConfig.set("$name.페이지", playerConfig.getInt("$name.페이지") + 1)
                ConfigManager.saveConfigs()
                Bukkit.getScheduler().runTaskLater(DiveSystem.instance, Runnable {
                    DiveSystemItemSettingGUI(player).open(player)
                }, 20L)
            }
            return
        }

        if (title == "${playerShop}금액설정") {
            event.isCancelled = true
            if (clickItemName == color("&a이전 페이지")) {
                event.inventory.clear()
                player.closeInventory()
                playerConfig.set("$name.페이지", playerConfig.getInt("$name.페이지") - 1)
                ConfigManager.saveConfigs()
                Bukkit.getScheduler().runTaskLater(DiveSystem.instance, Runnable {
                    DiveSystemAmountSettingGUI(player).open(player)
                }, 20L)
            }
            if (clickItemName == color("&a다음 페이지")) {
                event.inventory.clear()
                player.closeInventory()
                playerConfig.set("$name.페이지", playerConfig.getInt("$name.페이지") + 1)
                ConfigManager.saveConfigs()
                Bukkit.getScheduler().runTaskLater(DiveSystem.instance, Runnable {
                    DiveSystemAmountSettingGUI(player).open(player)
                }, 20L)
            }
            playerConfig.set("$name.금액설정", true)
            playerConfig.set("$name.설정슬롯", event.slot)
            ConfigManager.saveConfigs()
            player.closeInventory()
            player.sendMessage("${MessageUtil.prefix()} 금액을 /잠수금액 명령어를 이용해서 설정해주세요.")
            return
        }

        playerShop = playerConfig.getString("$name.구매상점") ?: "없음"
        if (title == "${playerShop}잠수상점") {
            event.isCancelled = true
            if (clickItemName == color("&a이전 페이지")) {
                event.inventory.clear()
                player.closeInventory()
                playerConfig.set("$name.페이지", playerConfig.getInt("$name.페이지") - 1)
                ConfigManager.saveConfigs()
                Bukkit.getScheduler().runTaskLater(DiveSystem.instance, Runnable {
                    DiveSystemGUI(player).open(player)
                }, 20L)
                return
            }
            if (clickItemName == color("&a다음 페이지")) {
                event.inventory.clear()
                player.closeInventory()
                playerConfig.set("$name.페이지", playerConfig.getInt("$name.페이지") + 1)
                ConfigManager.saveConfigs()
                Bukkit.getScheduler().runTaskLater(DiveSystem.instance, Runnable {
                    DiveSystemGUI(player).open(player)
                }, 20L)
                return
            }
            playerConfig.set("$name.구매슬롯", event.slot)
            ConfigManager.saveConfigs()
            event.inventory.clear()
            player.closeInventory()
            Bukkit.getScheduler().runTaskLater(DiveSystem.instance, Runnable {
                PurchaseGUI(player).open(player)
            }, 20L)
            return
        }

        if (title == "${playerShop}구매도우미") {
            event.isCancelled = true
            if (clickItemName == color("&a구매하기")) {
                val page = playerConfig.getInt("$name.페이지")
                val shopName = playerConfig.getString("$name.구매상점") ?: return
                val slot = playerConfig.getInt("$name.구매슬롯")
                val item = diveSystemConfig.getItemStack("잠수상점.$shopName.물품.$page.$slot")
                val amount = diveSystemConfig.getInt("잠수상점.$shopName.금액.$page.$slot")
                val point = DatabaseManager.getPoints(name)
                if (amount == 0) {
                    event.inventory.clear()
                    player.closeInventory()
                    player.sendMessage("${MessageUtil.prefix()} 해당 물품은 구매할 수 없습니다.")
                    return
                }
                if (point < amount) {
                    event.inventory.clear()
                    player.closeInventory()
                    player.sendMessage("${MessageUtil.prefix()} 구매하기에 잠수가 부족합니다.")
                    player.sendMessage("${MessageUtil.prefix()} 보유 잠수:${point}.P")
                    return
                }
                event.inventory.clear()
                player.closeInventory()
                DatabaseManager.setPoints(name, point - amount)
                player.sendMessage("${MessageUtil.prefix()} 정상적으로 물품을 구매했습니다.")
                player.sendMessage("${MessageUtil.prefix()} 보유 잠수:${point - amount}.P")
                if (item != null) {
                    player.inventory.addItem(item)
                }
            }
            if (clickItemName == color("&c취소하기")) {
                event.inventory.clear()
                player.closeInventory()
                player.sendMessage("${MessageUtil.prefix()} 작업을 취소했습니다.")
            }
        }
    }

    private fun color(text: String): String = ChatColor.translateAlternateColorCodes('&', text)
}
