package com.kaiwolfram.nozzle.data

import com.kaiwolfram.nozzle.model.Post
import com.kaiwolfram.nozzle.model.Profile
import java.time.LocalDateTime
import java.util.*
import kotlin.random.Random

class NostrRepositoryMock : INostrRepository {
    private val baseUrl = "https://robohash.org/"

    override fun getFollowerCount(): UInt {
        return Random.nextInt(2000).toUInt()
    }

    override fun getFollowingCount(): UInt {
        return Random.nextInt(2000).toUInt()
    }

    override fun getProfile(pubKey: String): Profile {
        return Profile(
            name = UUID.randomUUID().toString(),
            pubKey = pubKey,
            bio = UUID.randomUUID().toString().repeat(Random.nextInt(15)),
            picture = "$baseUrl${UUID.randomUUID()}"
        )
    }

    override fun getPosts(pubKey: String): List<Post> {
        val result = mutableListOf<Post>()
        val max = Random.nextInt(20)
        if (max != 0) {
            for (i in 0..max) {
                result.add(createRndPost())
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
}
