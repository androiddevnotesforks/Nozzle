package com.kaiwolfram.nozzle.data

import java.util.*

fun generatePrivateKey(): String {
    return UUID.randomUUID().toString()
}

fun derivePubkey(privateKey: String): String {
    return privateKey.reversed()
}
