package com.kaiwolfram.nozzle.data.utils

private const val HEX_CHARS = "0123456789abcdef"

fun String.isHex(): Boolean {
    return this.all { char -> HEX_CHARS.contains(char = char, ignoreCase = true) }
}
