package org.hwabeag.divesystem.data

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.bukkit.plugin.java.JavaPlugin
import org.hwabeag.divesystem.config.ConfigManager
import java.io.File
import java.sql.Connection
import java.sql.PreparedStatement

object DatabaseManager {
    private lateinit var plugin: JavaPlugin
    private lateinit var dataSource: HikariDataSource
    private var databaseType: DatabaseType = DatabaseType.SQLITE

    fun initialize(plugin: JavaPlugin) {
        this.plugin = plugin
        val config = ConfigManager.getConfig("dive-system")
        databaseType = DatabaseType.from(config.getString("database.type", "sqlite") ?: "sqlite")

        val hikariConfig = HikariConfig().apply {
            when (databaseType) {
                DatabaseType.SQLITE -> {
                    val sqliteFile = config.getString("database.sqlite.file", "divesystem.db") ?: "divesystem.db"
                    val dbFile = File(plugin.dataFolder, sqliteFile)
                    jdbcUrl = "jdbc:sqlite:${dbFile.absolutePath}"
                    driverClassName = "org.sqlite.JDBC"
                }

                DatabaseType.MYSQL -> {
                    val host = config.getString("database.mysql.host", "127.0.0.1")
                    val port = config.getInt("database.mysql.port", 3306)
                    val database = config.getString("database.mysql.database", "divesystem")
                    val username = config.getString("database.mysql.username", "root")
                    val password = config.getString("database.mysql.password", "password")
                    jdbcUrl = "jdbc:mysql://$host:$port/$database?useSSL=false&serverTimezone=UTC&characterEncoding=utf8"
                    driverClassName = "com.mysql.cj.jdbc.Driver"
                    this.username = username
                    this.password = password
                }
            }
            maximumPoolSize = config.getInt("database.pool.maximum-pool-size", 10)
            minimumIdle = config.getInt("database.pool.minimum-idle", 2)
            connectionTimeout = config.getLong("database.pool.connection-timeout-ms", 10000L)
            poolName = "DiveSystem-Hikari"
        }

        dataSource = HikariDataSource(hikariConfig)
        createTable()
        plugin.logger.info("데이터베이스 연결 완료: $databaseType")
    }

    fun shutdown() {
        if (::dataSource.isInitialized) {
            dataSource.close()
        }
    }

    fun ensurePlayer(name: String) {
        executeUpdate(
            """
            INSERT INTO dive_player_points(player_name, points)
            VALUES (?, 0)
            ON CONFLICT(player_name) DO NOTHING
            """.trimIndent()
        ) { stmt ->
            stmt.setString(1, name)
        }
    }

    fun hasPlayer(name: String): Boolean {
        return connection().use { conn ->
            conn.prepareStatement("SELECT 1 FROM dive_player_points WHERE player_name = ? LIMIT 1").use { stmt ->
                stmt.setString(1, name)
                stmt.executeQuery().use { rs -> rs.next() }
            }
        }
    }

    fun getPoints(name: String): Int {
        ensurePlayer(name)
        return connection().use { conn ->
            conn.prepareStatement("SELECT points FROM dive_player_points WHERE player_name = ?").use { stmt ->
                stmt.setString(1, name)
                stmt.executeQuery().use { rs ->
                    if (rs.next()) rs.getInt("points") else 0
                }
            }
        }
    }

    fun setPoints(name: String, points: Int) {
        ensurePlayer(name)
        executeUpdate("UPDATE dive_player_points SET points = ? WHERE player_name = ?") { stmt ->
            stmt.setInt(1, points)
            stmt.setString(2, name)
        }
    }

    fun addPoints(name: String, amount: Int): Int {
        val updated = getPoints(name) + amount
        setPoints(name, updated)
        return updated
    }

    private fun createTable() {
        val createSql = when (databaseType) {
            DatabaseType.SQLITE -> """
                CREATE TABLE IF NOT EXISTS dive_player_points (
                    player_name TEXT PRIMARY KEY,
                    points INTEGER NOT NULL DEFAULT 0
                )
            """.trimIndent()

            DatabaseType.MYSQL -> """
                CREATE TABLE IF NOT EXISTS dive_player_points (
                    player_name VARCHAR(32) PRIMARY KEY,
                    points INT NOT NULL DEFAULT 0
                )
            """.trimIndent()
        }
        connection().use { conn -> conn.createStatement().use { it.execute(createSql) } }
    }

    private fun executeUpdate(sql: String, binder: (PreparedStatement) -> Unit) {
        connection().use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                binder(stmt)
                stmt.executeUpdate()
            }
        }
    }

    private fun connection(): Connection = dataSource.connection
}
