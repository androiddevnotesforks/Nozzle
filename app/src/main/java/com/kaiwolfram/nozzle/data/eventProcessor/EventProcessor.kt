package com.kaiwolfram.nozzle.data.eventProcessor

import android.util.Log
import com.google.gson.Gson
import com.kaiwolfram.nostrclientkt.Event
import com.kaiwolfram.nostrclientkt.Metadata
import com.kaiwolfram.nostrclientkt.Tag
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.data.room.dao.ReactionDao

private const val TAG = "EventProcessor"

class EventProcessor(
    private val reactionDao: ReactionDao,
    private val contactDao: ContactDao,
    private val profileDao: ProfileDao,
    private val postDao: PostDao,
) : IEventProcessor {
    private val gson = Gson()
    override fun process(event: Event) {
        Log.i(TAG, "Process event ${event.id} kind ${event.kind}")
        if (event.isPost()) {
            processPost(event)
            return
        }
        if (event.isReaction()) {
            processReaction(event)
            return
        }
        if (event.isContactList()) {
            processContactList(event)
            return
        }
        if (event.isProfileMetadata()) {
            processMetadata(event)
            return
        }

    }

    private fun processPost(event: Event) {
        if (!verify(event)) {
            return
        }
        postDao.insert(
            id = event.id,
            pubkey = event.pubkey,
            replyTo = event.getReplyId(),
            replyToRoot = event.getRootReplyId(),
            content = event.content,
            createdAt = event.createdAt,
        )
    }

    private fun processReaction(event: Event) {
        if (event.content != "+") {
            Log.w(TAG, "Reaction event is not a like, content ${event.content}")
            return
        }
        if (!verify(event)) {
            return
        }
        reactionDao.like(eventId = event.id, pubkey = event.pubkey)
    }

    private fun processContactList(event: Event) {
        if (!verify(event)) {
            return
        }
        contactDao.deleteIfOutdated(pubkey = event.pubkey, createdAt = event.createdAt)
        contactDao.insert(
            pubkey = event.pubkey,
            contactPubkeys = getContactPubkeys(event.tags),
            createdAt = event.createdAt
        )
    }

    private fun processMetadata(event: Event) {
        if (!verify(event)) {
            return
        }
        deserializeMetadata(event.content)?.let {
            profileDao.deleteIfOutdated(pubkey = event.pubkey, createdAt = event.createdAt)
            profileDao.insert(
                pubkey = event.pubkey,
                name = it.name.orEmpty(),
                about = it.about.orEmpty(),
                picture = it.picture.orEmpty(),
                nip05 = it.nip05.orEmpty(),
                createdAt = event.createdAt,
            )
        }
    }

    private fun verify(event: Event): Boolean {
        val isValid = event.verify()
        if (!isValid) {
            Log.w(TAG, "Invalid event ${event.id}")
        }
        return isValid
    }

    private fun deserializeMetadata(json: String): Metadata? {
        try {
            return gson.fromJson(json, Metadata::class.java)
        } catch (t: Throwable) {
            Log.i(TAG, "Failed to deserialize $json")
        }
        return null
    }

    private fun getContactPubkeys(tags: List<Tag>): List<String> {
        val result = mutableListOf<String>()
        for (tag in tags) {
            if (tag.size >= 2 && tag[0] == "p") {
                result.add(tag[1])
            }
        }
        return result
    }
}
