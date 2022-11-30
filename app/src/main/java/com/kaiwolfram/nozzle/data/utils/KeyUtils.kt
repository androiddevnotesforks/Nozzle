package com.kaiwolfram.nozzle.data.utils

import java.util.*

fun generatePrivateKey(): String {
    return UUID.randomUUID().toString()
}

fun derivePublicKey(privateKey: String): String {
    return privateKey.reversed()
}
