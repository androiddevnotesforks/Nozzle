package com.kaiwolfram.nozzle.data

import fr.acinq.secp256k1.Secp256k1
import java.security.SecureRandom

private val rnd = SecureRandom()
private val secp256k1 = Secp256k1.get()

fun generatePrivkey(): String {
    val bytes = ByteArray(32)
    rnd.nextBytes(bytes)
    return bytes.toHex()
}

fun derivePubkey(privkey: String): String {
    val bytes = privkey.chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
    return secp256k1.pubKeyCompress(secp256k1.pubkeyCreate(bytes))
        .copyOfRange(1, 33)
        .toHex()
}

fun ellipsatePubkey(pubkey: String): String = "${pubkey.take(32)}..."

private fun ByteArray.toHex(): String {
    return this.joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
}
