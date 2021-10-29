package de.skyslycer.hmclink.backend.database.tables

import org.jetbrains.exposed.sql.Table

class LinkTable {

    companion object : Table("hmc_link") {
        val playerUUID = uuid("player_uuid")
        val playerName = varchar("player_name", 18)
        val discordID = long("discord_id").nullable()
        val discordName = varchar("discord_name", 34).nullable()
        val code = varchar("code", 32).nullable()
        val linked = bool("linked")
        val everLinked = bool("ever_linked")
    }

}