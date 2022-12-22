package com.kaiwolfram.nozzle.data.profileFollower

interface IProfileFollower {
    suspend fun follow(pubkey: String, pubkeyToFollow: String)
    suspend fun unfollow(pubkey: String, pubkeyToUnfollow: String)
}
