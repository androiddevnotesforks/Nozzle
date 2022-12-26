package com.kaiwolfram.nozzle.data.utils

import fr.acinq.secp256k1.Secp256k1
import java.security.SecureRandom

private val rnd = SecureRandom()
private val secp256k1 = Secp256k1.get()
private const val NPUB = "npub"
private const val NSEC = "nsec"

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

fun hexToNpub(pubkey: String): String {
    return Bech32.encode(NPUB, pubkey.decodeHex())
}

fun isValidPrivkey(privkey: String): Boolean {
    return privkey.length == 64 && privkey.isHex()
}

// TODO: Figure out how nsec to hex works

private fun ByteArray.toHex(): String {
    return this.joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
}
