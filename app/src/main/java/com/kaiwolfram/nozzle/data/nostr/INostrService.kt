package com.kaiwolfram.nozzle.data.nostr

interface INostrService {
    fun publishProfile(name: String, about: String, picture: String, nip05: String)
}
