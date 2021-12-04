package de.skyslycer.hmclink.backend.database

import de.skyslycer.hmclink.backend.EnvironmentVariables
import de.skyslycer.hmclink.backend.database.tables.DiscordMessageTable
import de.skyslycer.hmclink.backend.database.tables.LinkTable
import de.skyslycer.hmclink.backend.database.tables.PluginMessageTable
import de.skyslycer.hmclink.common.data.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.ds.PGSimpleDataSource
import java.util.*

class DatabaseHandler {

    companion object {
        /**
         * Try to connect to the database.
         *
         * @return The error when applicable
         */
        fun connect(): Optional<Throwable> {
            return try {
                val dataSource = PGSimpleDataSource()

                dataSource.serverNames = arrayOf(System.getenv(EnvironmentVariables.POSTGRES_HOST))
                dataSource.portNumbers = intArrayOf(System.getenv(EnvironmentVariables.POSTGRES_PORT).toInt())
                dataSource.databaseName = System.getenv(EnvironmentVariables.POSTGRES_NAME)

                Database.connect(dataSource)

                transaction {
                    SchemaUtils.createMissingTablesAndColumns(LinkTable, PluginMessageTable, DiscordMessageTable)
                }

                Optional.empty()
            } catch (exception: Throwable) {
                Optional.of(exception)
            }
        }

        /**
         * Get a user by Minecraft UUID.
         *
         * @param uuid The UUID to search for
         * @return The DatabaseUser if applicable
         */
        suspend fun get(uuid: UUID): Optional<DatabaseUser> {
            val result = newSuspendedTransaction {
                LinkTable.select {
                    LinkTable.playerUUID eq uuid
                }.firstOrNull()
            } ?: return Optional.empty()

            return with(LinkTable) {
                Optional.of(
                    DatabaseUser(
                        uuid,
                        result[playerName],
                        Optional.ofNullable(result[discordID]),
                        Optional.ofNullable(result[discordName]),
                        Optional.ofNullable(result[code]),
                        result[linked],
                        result[everLinked]
                    )
                )
            }
        }

        /**
         * Get a user by verification code.
         *
         * @param code The code to search for
         * @return The DatabaseUser if applicable
         */
        suspend fun get(code: String): Optional<DatabaseUser> {
            val result = newSuspendedTransaction {
                LinkTable.select {
                    LinkTable.code eq code
                }.firstOrNull()
            } ?: return Optional.empty()

            return with(LinkTable) {
                Optional.of(
                    DatabaseUser(
                        result[playerUUID],
                        result[playerName],
                        Optional.ofNullable(result[discordID]),
                        Optional.ofNullable(result[discordName]),
                        Optional.of(code),
                        result[linked],
                        result[everLinked]
                    )
                )
            }
        }

        /**
         * Get a user by Discord id.
         *
         * @param id The id to search for
         * @return The DatabaseUser if applicable
         */
        suspend fun get(id: Long): Optional<DatabaseUser> {
            val result = newSuspendedTransaction {
                LinkTable.select {
                    LinkTable.discordID eq id
                }.firstOrNull()
            } ?: return Optional.empty()

            return with(LinkTable) {
                Optional.of(
                    DatabaseUser(
                        result[playerUUID],
                        result[playerName],
                        Optional.of(id),
                        Optional.ofNullable(result[discordName]),
                        Optional.ofNullable(result[code]),
                        result[linked],
                        result[everLinked]
                    )
                )
            }
        }

        /**
         * Insert a user.
         *
         * @param uuid The Minecraft UUID of the player
         * @param name The Minecraft name of the player
         * @param linkCode The verification code
         */
        suspend fun insert(uuid: UUID, name: String, linkCode: String) {
            newSuspendedTransaction {
                LinkTable.insert {
                    it[playerUUID] = uuid
                    it[playerName] = name
                    it[discordID] = null
                    it[discordName] = null
                    it[code] = linkCode
                    it[linked] = false
                    it[everLinked] = false
                }
            }
        }

        /**
         * Update an existing entry in the database.
         *
         * @param uuid Minecraft UUID of the entry to update
         * @param discordID The new Discord id
         * @param discordName The new Discord username
         * @param code The verification code
         * @param linked If the player is linked
         * @param everLinked If the player has ever linked
         */
        suspend fun update(
            uuid: UUID,
            discordID: Optional<Long>,
            discordName: Optional<String>,
            code: Optional<String>,
            linked: Boolean,
            everLinked: Boolean
        ) {
            newSuspendedTransaction {
                LinkTable.update({ LinkTable.playerUUID eq uuid }) {
                    it[this.discordID] = discordID.orElse(null)
                    it[this.discordName] = discordName.orElse(null)
                    it[this.code] = code.orElse(null)
                    it[this.linked] = linked
                    it[this.everLinked] = everLinked
                }
            }
        }

        /**
         * Update an existing entry in the database from a DatabaseUser.
         *
         * @param databaseUser The user to take the data from
         */
        suspend fun update(databaseUser: DatabaseUser) {
            update(
                databaseUser.playerUUID,
                databaseUser.discordID,
                databaseUser.discordName,
                databaseUser.code,
                databaseUser.linked,
                databaseUser.everLinked
            )
        }

        /**
         * Convert a DatabaseUser to a User.
         *
         * @param user The DatabaseUser to convert
         * @return The converted User
         */
        fun toUniversalUser(user: DatabaseUser): User {
            return User(
                user.playerUUID,
                user.playerName,
                user.discordID.orElse(null),
                user.discordName.orElse(null),
                user.code.orElse(null),
                user.linked,
                user.everLinked
            )
        }

    }

}