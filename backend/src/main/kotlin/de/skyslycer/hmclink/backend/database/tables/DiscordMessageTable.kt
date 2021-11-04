package de.skyslycer.hmclink.backend.database.tables

import org.jetbrains.exposed.sql.Table

class DiscordMessageTable {

    companion object : Table("hmc_link_discord") {
        val message = binary("message")
    }

}