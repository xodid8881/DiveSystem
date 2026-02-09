package org.hwabeag.divesystem.inventorys

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.hwabeag.divesystem.config.ConfigManager

class PurchaseGUI(player: Player) {
    private val diveSystemConfig: FileConfiguration = ConfigManager.getConfig("dive-system")
    private val playerConfig: FileConfiguration = ConfigManager.getConfig("player")
    private val inv: Inventory

    init {
        val name = player.name
        val playerShop = playerConfig.getString("$name.구매상점") ?: "없음"
        inv = Bukkit.createInventory(null, 9, "${playerShop}구매도우미")
        initItemSetting(player)
    }

    private fun getDiveItemData(player: Player): ItemStack? {
        val name = player.name
        val page = playerConfig.getInt("$name.페이지")
        val shopName = playerConfig.getString("$name.구매상점") ?: return null
        val slot = playerConfig.getInt("$name.구매슬롯")
        return diveSystemConfig.getItemStack("잠수상점.$shopName.물품.$page.$slot")
    }

    private fun initItemSetting(player: Player) {
        inv.setItem(4, getDiveItemData(player))

        val buy = ItemStack(Material.PAPER, 1)
        buy.itemMeta = buy.itemMeta?.apply {
            setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a구매하기"))
            lore = listOf(ChatColor.translateAlternateColorCodes('&', "&a- &f클릭 시 물품을 구매합니다."))
        }
        inv.setItem(2, buy)

        val cancel = ItemStack(Material.PAPER, 1)
        cancel.itemMeta = cancel.itemMeta?.apply {
            setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c취소하기"))
            lore = listOf(ChatColor.translateAlternateColorCodes('&', "&a- &f클릭 시 물품 구매를 취소합니다."))
        }
        inv.setItem(6, cancel)
    }

    fun open(player: Player) {
        player.openInventory(inv)
    }
}
