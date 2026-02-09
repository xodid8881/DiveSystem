package org.hwabeag.divesystem.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import org.hwabeag.divesystem.config.ConfigManager

class InvCloseEvent : Listener {
    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        val player = event.player
        val name = player.name
        val diveSystemConfig = ConfigManager.getConfig("dive-system")
        val playerConfig = ConfigManager.getConfig("player")

        if (event.view.title != "물품설정") {
            return
        }
        val shopName = playerConfig.getString("$name.설정상점") ?: return
        if (diveSystemConfig.getString("잠수상점.$shopName") == null) {
            return
        }
        val line = diveSystemConfig.getInt("잠수상점.$shopName.라인")
        val page = playerConfig.getInt("$name.페이지")
        var slot = 0
        while (slot <= line * 9 - 1) {
            val item = event.inventory.getItem(slot)
            if (item != null) {
                diveSystemConfig.set("잠수상점.$shopName.물품.$page.$slot", item)
                if (diveSystemConfig.get("잠수상점.$shopName.금액.$page.$slot") == null) {
                    diveSystemConfig.set("잠수상점.$shopName.금액.$page.$slot", 0)
                }
            } else if (diveSystemConfig.getItemStack("잠수상점.$shopName.물품.$page.$slot") != null) {
                diveSystemConfig.set("잠수상점.$shopName.물품.$page.$slot", null)
                diveSystemConfig.set("잠수상점.$shopName.금액.$page.$slot", null)
            }
            slot += 1
        }
        playerConfig.set("$name.설정상점", "없음")
        ConfigManager.saveConfigs()
    }
}
