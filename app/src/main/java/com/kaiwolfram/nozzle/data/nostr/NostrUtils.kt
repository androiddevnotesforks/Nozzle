package com.kaiwolfram.nozzle.data.nostr

/**
 * See NIP-01 https://github.com/nostr-protocol/nips/blob/master/01.md#basic-event-kinds
 */
private val usernameRegex by lazy { Regex("\\w[\\w\\-]+\\w") }

fun isValidUsername(username: String): Boolean {
    return usernameRegex.matches(username)
}
