package com.kaiwolfram.nozzle.data

import com.kaiwolfram.nozzle.model.Post
import com.kaiwolfram.nozzle.model.Profile

interface INostrRepository {
    fun getFollowerCount(publicKey: String): UInt
    fun getFollowingCount(publicKey: String): UInt
    fun getProfile(publicKey: String): Profile?
    fun listPosts(publicKey: String): List<Post>
    fun listFollowedProfiles(pubKey: String): List<Profile>
}
