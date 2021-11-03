package de.skyslycer.hmclink.plugin.chat

class Replacement(
    val placeholder: String,
    val value: String
) {

    companion object {
        fun replace(string: String, vararg replacements: Replacement): String {
            replacements.forEach {
                string.replace("%${it.placeholder}%", it.value)
            }

            return string
        }
    }

}