package org.hwabeag.divesystem.inventorys

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.hwabeag.divesystem.config.ConfigManager

class DiveSystemItemSettingGUI(player: Player) {
    private val diveSystemConfig: FileConfiguration = ConfigManager.getConfig("dive-system")
    private val playerConfig: FileConfiguration = ConfigManager.getConfig("player")
    private val inv: Inventory

    init {
        val name = player.name
        val playerShop = playerConfig.getString("$name.설정상점") ?: "없음"
        val line = diveSystemConfig.getInt("잠수상점.$playerShop.라인")
        inv = Bukkit.createInventory(null, line * 9 + 9, "물품설정")
        initItemSetting(player)
    }

    private fun initItemSetting(player: Player) {
        val name = player.name
        val page = playerConfig.getInt("$name.페이지")
        val playerShop = playerConfig.getString("$name.설정상점") ?: return
        val line = diveSystemConfig.getInt("잠수상점.$playerShop.라인")
        val section = diveSystemConfig.getConfigurationSection("잠수상점.$playerShop.물품.$page")
        section?.getKeys(false)?.forEach { key ->
            val item = diveSystemConfig.getItemStack("잠수상점.$playerShop.물품.$page.$key")
            inv.setItem(key.toInt(), item)
        }

        val prev = ItemStack(Material.PAPER, 1)
        prev.itemMeta = prev.itemMeta?.apply {
            setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a이전 페이지"))
            lore = listOf(ChatColor.translateAlternateColorCodes('&', "&a- &f클릭 시 이전 페이지로 이동합니다."))
        }
        inv.setItem(line * 9 + 6, prev)

        val next = ItemStack(Material.PAPER, 1)
        next.itemMeta = next.itemMeta?.apply {
            setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a다음 페이지"))
            lore = listOf(ChatColor.translateAlternateColorCodes('&', "&a- &f클릭 시 다음 페이지로 이동합니다."))
        }
        inv.setItem(line * 9 + 8, next)
    }

    fun open(player: Player) {
        player.openInventory(inv)
    }
}
