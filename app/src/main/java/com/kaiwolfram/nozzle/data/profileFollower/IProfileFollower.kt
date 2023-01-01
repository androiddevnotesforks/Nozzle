package com.kaiwolfram.nozzle.data.profileFollower

interface IProfileFollower {
    suspend fun follow(pubkeyToFollow: String, relayUrl: String)
    suspend fun unfollow(pubkeyToUnfollow: String)
}
