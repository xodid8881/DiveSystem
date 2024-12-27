package org.hwabeag.divesystem.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.hwabeag.divesystem.config.ConfigManager;
import org.hwabeag.divesystem.inventorys.DiveSystemAmountSettingGUI;
import org.hwabeag.divesystem.inventorys.DiveSystemGUI;
import org.hwabeag.divesystem.inventorys.DiveSystemItemSettingGUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShopCommand implements TabCompleter, @Nullable CommandExecutor {

    FileConfiguration DiveSystemConfig = ConfigManager.getConfig("dive-system");
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");
    String Prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(DiveSystemConfig.getString("dive-system.prefix")));

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<String>();
            list.add("열기");
            list.add("생성");
            list.add("제거");
            list.add("물품설정");
            list.add("금액설정");
            return list;
        }
        if (args.length == 2) {
            List<String> list = new ArrayList<String>();
            list.addAll(Objects.requireNonNull(DiveSystemConfig.getConfigurationSection("잠수상점")).getKeys(false));
            return list;
        }
        return null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getConsoleSender().sendMessage(Prefix + " 인게임에서 사용할 수 있습니다.");
            return true;
        }
        String name = player.getName();
        if (args.length == 0) {
            if (!player.isOp()) {
                player.sendMessage(Prefix + " /잠수상점 열기 [제목] - 잠수상점을 오픈합니다.");
                return true;
            }
            player.sendMessage(Prefix + " /잠수상점 열기 [제목] - 잠수상점을 오픈합니다.");
            player.sendMessage(Prefix + " /잠수상점 생성 [제목] [라인] - 잠수상점을 생성합니다. (최대 5라인)");
            player.sendMessage(Prefix + " /잠수상점 제거 [제목] - 잠수상점을 제거합니다.");
            player.sendMessage(Prefix + " /잠수상점 물품설정 [제목] - 잠수상점 물품을 설정합니다.");
            player.sendMessage(Prefix + " /잠수상점 금액설정 [제목] - 잠수상점 물품 금액을 설정합니다.");
            return true;
        }
        if (args[0].equalsIgnoreCase("열기")) {
            if (args.length == 1) {
                player.sendMessage(Prefix + " 제목을 정확하게 적어주세요.");
                return true;
            }
            if (DiveSystemConfig.getString("잠수상점." + args[1]) != null) {
                PlayerConfig.set(name + ".페이지", 0);
                PlayerConfig.set(name + ".구매상점", args[1]);
                ConfigManager.saveConfigs();
                DiveSystemGUI inv = new DiveSystemGUI(player);
                inv.open(player);
            } else {
                player.sendMessage(Prefix + " " + args[1] + " 이름의 잠수상점이 존재하지 않습니다.");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("생성")) {
            if (args.length == 1) {
                player.sendMessage(Prefix + " 제목을 정확하게 적어주세요.");
                return true;
            }
            if (args.length == 2) {
                player.sendMessage(Prefix + " 라인을 정확하게 적어주세요.");
                return true;
            }
            if (Integer.parseInt(args[2]) > 5) {
                player.sendMessage(Prefix + " 라인은 최대 5 까지만 가능합니다.");
                return true;
            }
            if (DiveSystemConfig.getString("잠수상점." + args[1]) == null) {
                DiveSystemConfig.set("잠수상점." + args[1] + ".라인", Integer.parseInt(args[2]));
                ConfigManager.saveConfigs();
                player.sendMessage(Prefix + " " + args[1] + " 이름의 잠수상점을 생성했습니다.");
            } else {
                player.sendMessage(Prefix + " " + args[1] + " 이름의 잠수상점이 이미 존재합니다.");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("제거")) {
            if (args.length == 1) {
                player.sendMessage(Prefix + " 제목을 정확하게 적어주세요.");
                return true;
            }
            if (DiveSystemConfig.getString("잠수상점." + args[1]) != null) {
                DiveSystemConfig.set("잠수상점." + args[1], null);
                ConfigManager.saveConfigs();
                player.sendMessage(Prefix + " " + args[1] + " 이름의 잠수상점을 제거했습니다.");
            } else {
                player.sendMessage(Prefix + " " + args[1] + " 이름의 잠수상점이 존재하지 않습니다.");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("물품설정")) {
            if (args.length == 1) {
                player.sendMessage(Prefix + " 제목을 정확하게 적어주세요.");
                return true;
            }
            if (DiveSystemConfig.getString("잠수상점." + args[1]) != null) {
                PlayerConfig.set(name + ".페이지", 0);
                PlayerConfig.set(name + ".설정상점", args[1]);
                ConfigManager.saveConfigs();
                DiveSystemItemSettingGUI inv = new DiveSystemItemSettingGUI(player);
                inv.open(player);
            } else {
                player.sendMessage(Prefix + " " + args[1] + " 이름의 잠수상점이 존재하지 않습니다.");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("금액설정")) {
            if (args.length == 1) {
                player.sendMessage(Prefix + " 제목을 정확하게 적어주세요.");
                return true;
            }
            if (DiveSystemConfig.getString("잠수상점." + args[1]) != null) {
                PlayerConfig.set(name + ".페이지", 0);
                PlayerConfig.set(name + ".설정상점", args[1]);
                ConfigManager.saveConfigs();
                DiveSystemAmountSettingGUI inv = new DiveSystemAmountSettingGUI(player);
                inv.open(player);
            } else {
                player.sendMessage(Prefix + " " + args[1] + " 이름의 잠수상점이 존재하지 않습니다.");
            }
            return true;
        }
        if (!player.isOp()) {
            player.sendMessage(Prefix + " /잠수상점 열기 [제목] - 잠수상점을 오픈합니다.");
            return true;
        }
        player.sendMessage(Prefix + " /잠수상점 열기 [제목] - 잠수상점을 오픈합니다.");
        player.sendMessage(Prefix + " /잠수상점 생성 [제목] [라인] - 잠수상점을 생성합니다. (최대 5라인)");
        player.sendMessage(Prefix + " /잠수상점 제거 [제목] - 잠수상점을 제거합니다.");
        player.sendMessage(Prefix + " /잠수상점 물품설정 [제목] - 잠수상점 물품을 설정합니다.");
        player.sendMessage(Prefix + " /잠수상점 금액설정 [제목] - 잠수상점 물품 금액을 설정합니다.");
        return true;
    }
}