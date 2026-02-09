package org.hwabeag.divesystem.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.concurrent.ConcurrentHashMap

object ConfigManager {
    private lateinit var plugin: JavaPlugin
    private val configSet = ConcurrentHashMap<String, ConfigFile>()

    fun initialize(plugin: JavaPlugin) {
        this.plugin = plugin
        val path = plugin.dataFolder.absolutePath
        configSet["dive-system"] = ConfigFile(path, "DiveSystem.yml")
        configSet["player"] = ConfigFile(path, "Player.yml")
        loadSettings()
        saveConfigs()
    }

    fun reloadConfigs() {
        configSet.forEach { (key, configFile) ->
            plugin.logger.info(key)
            configFile.reloadConfig()
        }
    }

    fun saveConfigs() {
        configSet.values.forEach { it.saveConfig() }
    }

    fun getConfig(fileName: String): FileConfiguration {
        return requireNotNull(configSet[fileName]) { "설정 파일을 찾을 수 없습니다: $fileName" }.config
    }

    private fun loadSettings() {
        val diveSystemConfig = getConfig("dive-system")
        diveSystemConfig.options().copyDefaults(true)
        diveSystemConfig.addDefault("dive-system.prefix", "&a&l[DiveSystem]&7")
        diveSystemConfig.addDefault("dive-system.one-minute-point", 1)
        diveSystemConfig.addDefault("dive-system.dive-no-world.test", "test")

        diveSystemConfig.addDefault("database.type", "sqlite")
        diveSystemConfig.addDefault("database.sqlite.file", "divesystem.db")
        diveSystemConfig.addDefault("database.mysql.host", "127.0.0.1")
        diveSystemConfig.addDefault("database.mysql.port", 3306)
        diveSystemConfig.addDefault("database.mysql.database", "divesystem")
        diveSystemConfig.addDefault("database.mysql.username", "root")
        diveSystemConfig.addDefault("database.mysql.password", "password")
        diveSystemConfig.addDefault("database.pool.maximum-pool-size", 10)
        diveSystemConfig.addDefault("database.pool.minimum-idle", 2)
        diveSystemConfig.addDefault("database.pool.connection-timeout-ms", 10000)
    }

    private class ConfigFile(path: String, fileName: String) {
        private val file = File(path, fileName)
        var config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
            private set

        fun saveConfig() {
            runCatching { config.save(file) }.onFailure { it.printStackTrace() }
        }

        fun reloadConfig() {
            if (!file.exists()) {
                return
            }
            config = YamlConfiguration.loadConfiguration(file)
        }
    }
}
