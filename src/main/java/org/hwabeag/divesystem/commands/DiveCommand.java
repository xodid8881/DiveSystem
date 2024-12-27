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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiveCommand implements TabCompleter, @Nullable CommandExecutor {

    FileConfiguration DiveSystemConfig = ConfigManager.getConfig("dive-system");
    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");
    String Prefix = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(DiveSystemConfig.getString("dive-system.prefix")));

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<String>();
            list.add("지급");
            list.add("회수");
            list.add("설정");
            list.add("확인");
            return list;
        }
        if (args.length == 2) {
            List<String> list = new ArrayList<String>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                list.add(p.getName());
            }
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
        if (args.length == 0) {
            if (player.isOp()) {
                player.sendMessage(Prefix + " /잠수포인트 지급 [닉네임] [정도] - 잠수포인트를 지급합니다.");
                player.sendMessage(Prefix + " /잠수포인트 회수 [닉네임] [정도] - 잠수포인트를 회수합니다.");
                player.sendMessage(Prefix + " /잠수 설정 [닉네임] [정도] - 잠수포인트를 설정합니다.");
            }
            player.sendMessage(Prefix + " /잠수포인트 확인 [닉네임] - 잠수포인트를 확인합니다.");
            return true;
        }
        if (args[0].equalsIgnoreCase("지급")) {
            if (args.length == 1) {
                player.sendMessage(Prefix + " 닉네임을 적어주세요.");
                return true;
            }
            if (args.length == 2) {
                player.sendMessage(Prefix + " 정도를 정확하게 적어주세요.");
                return true;
            }
            if (PlayerConfig.getString(args[1] + ".잠수") != null) {
                int point = PlayerConfig.getInt(args[1] + ".잠수");
                int index = point+Integer.parseInt(args[2]);
                PlayerConfig.set(args[1] + ".잠수", index);
                ConfigManager.saveConfigs();
                player.sendMessage(Prefix + " " + args[1] + " 님에게 " + args[2] + " .P 를 지급했습니다.");
                player.sendMessage(Prefix + " 해당 유저 보유 잠수포인트 : " + index + "원");
                for (Player ignored : Bukkit.getOnlinePlayers()) {
                    if (ignored.getName().equals(args[2])){
                        ignored.sendMessage(Prefix + " 운영진으로부터 " + args[2] + " .P 를 지급받았습니다.");
                        ignored.sendMessage(Prefix + " 보유 잠수포인트 : " + index + ".P");
                    }
                }
            } else {
                player.sendMessage(Prefix + " " + args[1] + " 닉네임의 유저는 존재하지 않습니다.");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("회수")) {
            if (args.length == 1) {
                player.sendMessage(Prefix + " 닉네임을 적어주세요.");
                return true;
            }
            if (args.length == 2) {
                player.sendMessage(Prefix + " 정도를 정확하게 적어주세요.");
                return true;
            }
            if (PlayerConfig.getString(args[1] + ".잠수") != null) {
                int point = PlayerConfig.getInt(args[1] + ".잠수");
                if (point < Integer.parseInt(args[2])){
                    player.sendMessage(Prefix + " " + args[1] + " 유저의 잠수포인트 보유량보다 회수금액이 큽니다.");
                    return true;
                }
                int index = point-Integer.parseInt(args[2]);
                PlayerConfig.set(args[1] + ".잠수", index);
                ConfigManager.saveConfigs();
                player.sendMessage(Prefix + " " + args[1] + " 님에게 " + args[2] + " .P 를 회수했습니다.");
                player.sendMessage(Prefix + " 해당 유저 보유 잠수포인트 : " + index + ".P");
                for (Player ignored : Bukkit.getOnlinePlayers()) {
                    if (ignored.getName().equals(args[2])){
                        ignored.sendMessage(Prefix + " 운영진으로부터 " + args[2] + " .P 를 회수당했습니다.");
                        ignored.sendMessage(Prefix + " 보유 잠수포인트 : " + index + ".P");
                    }
                }
            } else {
                player.sendMessage(Prefix + " " + args[1] + " 닉네임의 유저는 존재하지 않습니다.");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("설정")) {
            if (args.length == 1) {
                player.sendMessage(Prefix + " 닉네임을 적어주세요.");
                return true;
            }
            if (args.length == 2) {
                player.sendMessage(Prefix + " 정도를 정확하게 적어주세요.");
                return true;
            }
            if (PlayerConfig.getString(args[1] + ".잠수") != null) {
                PlayerConfig.set(args[1] + ".잠수", Integer.parseInt(args[2]));
                ConfigManager.saveConfigs();
                player.sendMessage(Prefix + " " + args[1] + " 님의 잠수포인트 보유량을 " + args[2] + " .P 로 설정했습니다.");
                player.sendMessage(Prefix + " 해당 유저 보유 잠수포인트 : " + args[2] + ".P");
                for (Player ignored : Bukkit.getOnlinePlayers()) {
                    if (ignored.getName().equals(args[2])){
                        ignored.sendMessage(Prefix + " 운영진이 당신의 잠수포인트 보유량을 " + args[2] + " .P 로 변경했습니다.");
                        ignored.sendMessage(Prefix + " 보유 잠수포인트 : " + args[2] + ".P");
                    }
                }
            } else {
                player.sendMessage(Prefix + " " + args[1] + " 닉네임의 유저는 존재하지 않습니다.");
            }
            return true;
        }
        if (args[0].equalsIgnoreCase("확인")) {
            String name = player.getName();
            if (PlayerConfig.getString(name + ".잠수") != null) {
                int point = PlayerConfig.getInt(name + ".잠수");
                player.sendMessage(Prefix + " 당신의 보유 잠수포인트 : " + point + ".P");
                return true;
            }
        }
        if (player.isOp()) {
            player.sendMessage(Prefix + " /잠수포인트 지급 [닉네임] [정도] - 잠수포인트를 지급합니다.");
            player.sendMessage(Prefix + " /잠수포인트 회수 [닉네임] [정도] - 잠수포인트를 회수합니다.");
            player.sendMessage(Prefix + " /잠수포인트 설정 [닉네임] [정도] - 잠수포인트를 설정합니다.");
        }
        player.sendMessage(Prefix + " /잠수포인트 확인 [닉네임] - 잠수포인트를 확인합니다.");
        return true;
    }
}