package com.kaiwolfram.nozzle.data.utils

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

// TODO: Real implementations
fun hexToNpub(pubkey: String) = "npub$pubkey"
fun hexToNsec(privkey: String) = "nsec$privkey"
fun npubToHex(npub: String) = npub.substring(4)
fun nsecToHex(nsec: String) = nsec.substring(4)

fun privkeyToHex(privkey: String): String {
    return if (privkey.startsWith("nsec")) nsecToHex(privkey) else privkey
}

fun isValidPrivkey(privkey: String): Boolean {
    val hex = privkeyToHex(privkey)
    return hex.length == 64 && hex.isHex()
}


private fun ByteArray.toHex(): String {
    return this.joinToString(separator = "") { eachByte -> "%02x".format(eachByte) }
}
