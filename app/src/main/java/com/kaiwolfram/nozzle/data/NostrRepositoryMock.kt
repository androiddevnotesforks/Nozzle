package com.kaiwolfram.nozzle.data

import com.kaiwolfram.nozzle.model.Post
import com.kaiwolfram.nozzle.model.Profile
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

class NostrRepositoryMock : INostrRepository {
    private val baseUrl = "https://robohash.org/"

    override fun getFollowerCount(publicKey: String): UInt {
        return Random.nextInt(2000).toUInt()
    }

    override fun getFollowingCount(publicKey: String): UInt {
        return Random.nextInt(2000).toUInt()
    }

    override fun getProfile(publicKey: String): Profile {
        return createRndProfile(publicKey)
    }

    override fun listPosts(publicKey: String): List<Post> {
        val result = mutableListOf<Post>()
        val max = Random.nextInt(15)
        if (max != 0) {
            for (i in 0..max) {
                result.add(createRndPost())
            }
        }

        return result
    }

    override fun listFollowedProfiles(pubKey: String): List<Profile> {
        val result = mutableListOf<Profile>()
        val max = Random.nextInt(15)
        if (max != 0) {
            for (i in 0..max) {
                result.add(createRndProfile())
            }
        }

        return result
    }

    private fun createRndPost(): Post {
        return Post(
            author = UUID.randomUUID().toString(),
            profilePicUrl = "$baseUrl${UUID.randomUUID()}",
            published = LocalDateTime.now(),
            content = UUID.randomUUID().toString().repeat(Random.nextInt(20))
        )
    }

    private fun createRndProfile(publicKey: String): Profile {
        return Profile(
            name = UUID.randomUUID().toString(),
            publicKey = publicKey,
            bio = UUID.randomUUID().toString().repeat(Random.nextInt(15)),
            pictureUrl = "$baseUrl${UUID.randomUUID()}"
        )
    }

    private fun createRndProfile(): Profile {
        return createRndProfile(UUID.randomUUID().toString())
    }
}
