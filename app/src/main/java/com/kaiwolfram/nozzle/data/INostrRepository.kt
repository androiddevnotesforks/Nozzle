package com.kaiwolfram.nozzle.data

import com.kaiwolfram.nozzle.model.Post
import com.kaiwolfram.nozzle.model.Profile

interface INostrRepository {
    fun getFollowerCount(): UInt
    fun getFollowingCount(): UInt
    fun getProfile(pubKey: String): Profile
    fun listPosts(pubKey: String): List<Post>
    fun listFollowedProfiles(pubKey: String): List<Profile>
}
