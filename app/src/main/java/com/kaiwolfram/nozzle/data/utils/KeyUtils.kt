package com.kaiwolfram.nozzle.data.utils

import java.util.*

fun generatePrivateKey(): String {
    // TODO: Generate real private key
    return UUID.randomUUID().toString()
}

fun derivePublicKey(privateKey: String): String {
    // TODO: Derive real public key
    return privateKey.reversed()
}
