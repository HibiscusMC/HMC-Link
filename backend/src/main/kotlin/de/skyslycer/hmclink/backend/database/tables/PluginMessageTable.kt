package de.skyslycer.hmclink.backend.database.tables

import org.jetbrains.exposed.sql.Table

class PluginMessageTable {

    companion object : Table("hmc_link_minecraft") {
        val code = varchar("code", 64)
        val message = binary("message")
        override val primaryKey = PrimaryKey(code)
    }

}