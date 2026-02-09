package org.hwabeag.divesystem.data

enum class DatabaseType {
    SQLITE,
    MYSQL;

    companion object {
        fun from(raw: String): DatabaseType {
            return entries.firstOrNull { it.name.equals(raw, ignoreCase = true) } ?: SQLITE
        }
    }
}
