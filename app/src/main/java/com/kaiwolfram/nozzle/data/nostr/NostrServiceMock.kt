package com.kaiwolfram.nozzle.data.nostr

class NostrServiceMock : INostrService {
    override fun publishProfile(name: String, about: String, picture: String, nip05: String) {
        TODO("Not yet implemented")
    }

    override fun sendPost(content: String) {
        TODO("Not yet implemented")
    }

    override fun sendRepost(postId: String, quote: String) {
        TODO("Not yet implemented")
    }

    override fun sendLike(postId: String) {
        TODO("Not yet implemented")
    }

    override fun sendReply(recipientPubkey: String, content: String) {
        TODO("Not yet implemented")
    }

    override fun subscribeToProfileMetadata(pubkey: String) {
        TODO("Not yet implemented")
    }

    override fun follow(pubkey: String) {
        // No return
    }

    override fun unfollow(pubkey: String) {
        // No return
    }

}
