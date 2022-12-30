package com.kaiwolfram.nostrclientkt.utils

object NostrUtils {
    val usernameRegex by lazy { Regex("\\w[\\w\\-]+\\w") }

    fun isValidUsername(username: String): Boolean {
        return usernameRegex.matches(username)
    }
}
