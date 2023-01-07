package com.kaiwolfram.nozzle.data.nostr

interface INostrSubscriber {
    fun subscribeToProfileMetadataAndContactList(pubkey: String): List<String>
    fun subscribeToFeed(contactPubkeys: List<String>, since: Long? = null): List<String>
}
