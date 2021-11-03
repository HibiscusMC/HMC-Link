package de.skyslycer.hmclink.backend.utils

import de.skyslycer.hmclink.backend.EnvironmentVariables

class DiscordUtilities {

    companion object {
        /**
         * Build the linked nickname.
         *
         * @param discordName The Discord name of the user (username#discriminator)
         * @param minecraftName The Minecraft name of the user
         * @return The nickname
         */
        fun toNickName(discordName: String, minecraftName: String) =
            System.getenv(EnvironmentVariables.NICKNAME_TEMPLATE)
                .replace("%dc%", discordName)
                .replace("%mc%", minecraftName)

        /**
         * Build a full username out of name and discriminator.
         *
         * @param name The name of the user
         * @param discriminator The discriminator of the user
         * @return The full username
         */
        fun getDiscordName(name: String, discriminator: String): String = "$name#$discriminator"
    }

}