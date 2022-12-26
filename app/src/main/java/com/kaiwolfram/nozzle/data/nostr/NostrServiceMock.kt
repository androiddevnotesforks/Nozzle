package com.kaiwolfram.nozzle.data.nostr

import com.kaiwolfram.nozzle.data.room.entity.EventEntity
import com.kaiwolfram.nozzle.data.utils.derivePubkey
import com.kaiwolfram.nozzle.data.utils.generatePrivkey
import java.util.*
import kotlin.random.Random

class NostrServiceMock : INostrService {
    private val baseUrl = "https://robohash.org/"

    override fun getFollowerCount(pubkey: String): Int {
        return Random.nextInt(2000)
    }

    override fun getFollowingCount(pubkey: String): Int {
        return Random.nextInt(2000)
    }

    override fun getProfile(pubkey: String): NostrProfile {
        return createRndProfile(pubkey)
    }

    override fun listPosts(pubkey: String): List<EventEntity> {
        val result = mutableListOf<EventEntity>()
        val max = Random.nextInt(15)
        if (max != 0) {
            for (i in 0..max) {
                result.add(createRndPost())
            }
        }

        return result
    }

    override fun listPosts(): List<EventEntity> {
        return listPosts("lol what")
    }

    override fun listFollowedProfiles(pubKey: String): List<NostrProfile> {
        val result = mutableListOf<NostrProfile>()
        val max = Random.nextInt(15)
        if (max != 0) {
            for (i in 0..max) {
                result.add(createRndProfile())
            }
        }

        return result
    }

    override fun getPost(postId: String): EventEntity {
        return createRndPost()
    }

    override fun likePost(postId: String) {
        // No return
    }

    override fun repost(postId: String) {
        // No return
    }

    override fun follow(pubkey: String) {
        // No return
    }

    override fun unfollow(pubkey: String) {
        // No return
    }

    override fun send(recipientPubkey: String, reply: String) {
        // No return
        // Real implementation should create event, save in db and send to relays
    }

    private fun createRndPost(): EventEntity {
        return EventEntity(
            id = UUID.randomUUID().toString(),
            pubkey = derivePubkey(generatePrivkey()),
            kind = 3,
            createdAt = System.currentTimeMillis(),
            content = UUID.randomUUID().toString().repeat(Random.nextInt(20))
        )
    }

    private fun createRndProfile(pubkey: String): NostrProfile {
        return NostrProfile(
            name = UUID.randomUUID().toString(),
            pubkey = pubkey,
            about = UUID.randomUUID().toString().repeat(Random.nextInt(15)),
            picture = "$baseUrl${UUID.randomUUID()}"
        )
    }

    private fun createRndProfile(): NostrProfile {
        return createRndProfile(UUID.randomUUID().toString())
    }
}
