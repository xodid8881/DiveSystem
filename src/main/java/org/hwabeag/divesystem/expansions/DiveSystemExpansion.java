package org.hwabeag.divesystem.expansions;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.hwabeag.divesystem.DiveSystem;
import org.hwabeag.divesystem.config.ConfigManager;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DiveSystemExpansion extends PlaceholderExpansion {


    FileConfiguration PlayerConfig = ConfigManager.getConfig("player");
    private final DiveSystem plugin; //

    public DiveSystemExpansion(DiveSystem plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "dive_system";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("get-index")) { // %dive_system_get-index%
            String name = Objects.requireNonNull(player).getName();
            if (PlayerConfig.getString(name + ".잠수") != null) {
                return PlayerConfig.getString(name + ".잠수");
            }
        }
        return null; //
    }
}