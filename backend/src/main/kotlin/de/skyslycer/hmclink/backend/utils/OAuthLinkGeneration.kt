package de.skyslycer.hmclink.backend.utils

import de.skyslycer.hmclink.backend.EnvironmentVariables

class OAuthLinkGeneration {

    companion object {
        /**
         * Generate the oAuth link with a specific code.
         *
         * @param code The code to send along
         * @return The newly built link
         */
        fun generateOAuthLink(code: String): String = System.getenv(EnvironmentVariables.OAUTH_URL).replace("%code%", code)
    }

}