package de.skyslycer.hmclink.backend.utils

import de.skyslycer.hmclink.backend.EnvironmentVariables
import org.apache.commons.lang3.RandomStringUtils

class CodeGeneration {

    companion object {
        /**
         * Generate a verification code.
         *
         * @return The generated code
         */
        fun generateCode(): String = RandomStringUtils.randomAlphabetic(32)

        /**
         * Generate a redirect link with the given code.
         *
         * @param code The code to use
         * @return The link
         */
        fun generateLink(code: String): String = String.format(System.getenv(EnvironmentVariables.LINK_TEMPLATE), code)
    }

}