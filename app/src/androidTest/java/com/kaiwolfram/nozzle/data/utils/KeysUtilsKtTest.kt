package com.kaiwolfram.nozzle.data.utils

import org.junit.Test

internal class KeysUtilsKtTest {

    @Test
    fun generatePrivkeyGenerates64HexChars() {
        val privkey = generatePrivkey()

        assert(privkey.isHex())
        assert(privkey.length == 64)
    }

    @Test
    fun derivePubkeyDerivesCorrectPubkey() {
        val privkey = "a0244a7a2cf9172532d100c424ed5737c688a71f4e6c6e1b559d45f2684d1e93"
        val expectedPubkey = "8f83f7586cf53ae6fc4e78dc014860132a51cd1e4bdb27866baccf7acc090530"

        val derived = derivePubkey(privkey)

        assert(derived == expectedPubkey)
    }

    @Test
    fun hexToNpubConvertsHexPubkeyToNpub() {
        val hex = "c1a8cf318c6a1a0f27da4e202215cc1cfefe7f37b5b8d110552087b89328574a"
        val expectedNpub = "npub1cx5v7vvvdgdq7f76fcszy9wvrnl0ulehkkudzyz4yzrm3yeg2a9quvjyrg"

        val result = hexToNpub(hex)

        assert(result == expectedNpub)
    }
}
