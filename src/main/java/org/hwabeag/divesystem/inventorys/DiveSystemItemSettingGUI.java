package org.hwabeag.divesystem.inventorys;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hwabeag.divesystem.config.ConfigManager;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public class DiveSystemItemSettingGUI implements Listener {
    private final Inventory inv;

    FileConfiguration DiveSystemConfig = ConfigManager.getConfig("dive-system");
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");

    private void initItemSetting(Player p) {
        String name = p.getName();
        int PlayerPage = PlayerConfig.getInt(name + ".페이지");
        String PlayerShop = PlayerConfig.getString(name + ".설정상점");
        int index = DiveSystemConfig.getInt("잠수상점." + PlayerShop + ".라인");
        int N = 0;
        int Page = 0;
        if (DiveSystemConfig.getConfigurationSection("잠수상점." + PlayerShop + ".물품." + PlayerPage) != null) {
            for (String key : Objects.requireNonNull(DiveSystemConfig.getConfigurationSection("잠수상점." + PlayerShop + ".물품." + PlayerPage)).getKeys(false)) {
                if (Page == PlayerPage) {
                    @Nullable ItemStack item = DiveSystemConfig.getItemStack("잠수상점." + PlayerShop + ".물품." + PlayerPage + "." + key);
                    inv.setItem(Integer.parseInt(key), item);
                }
                N = N + 1;
                if (N >= index * 9) {
                    Page = Page + 1;
                    N = 0;
                }
            }
        }
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a이전 페이지"));
        itemMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&a- &f클릭 시 이전 페이지로 이동합니다.")));
        item.setItemMeta(itemMeta);
        inv.setItem(index*9+9-3,item);

        item = new ItemStack(Material.PAPER, 1);
        itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a다음 페이지"));
        itemMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&a- &f클릭 시 다음 페이지로 이동합니다.")));
        item.setItemMeta(itemMeta);
        inv.setItem(index*9+9-1,item);
    }

    public DiveSystemItemSettingGUI(Player p) {
        String name = p.getName();
        String PlayerShop = PlayerConfig.getString(name + ".설정상점");
        int index = DiveSystemConfig.getInt("잠수상점." + PlayerShop + ".라인");
        inv = Bukkit.createInventory(null,index*9+9,"물품설정");
        this.initItemSetting(p);
    }

    public void open(Player player){
        player.openInventory(inv);
    }

}
