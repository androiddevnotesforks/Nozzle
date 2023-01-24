package com.kaiwolfram.nozzle.data.eventProcessor

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kaiwolfram.nostrclientkt.model.Event
import com.kaiwolfram.nostrclientkt.model.Metadata
import com.kaiwolfram.nostrclientkt.model.Tag
import com.kaiwolfram.nozzle.data.room.dao.ContactDao
import com.kaiwolfram.nozzle.data.room.dao.PostDao
import com.kaiwolfram.nozzle.data.room.dao.ProfileDao
import com.kaiwolfram.nozzle.data.room.dao.ReactionDao
import com.kaiwolfram.nozzle.data.room.entity.ContactEntity
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.data.room.entity.ProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "EventProcessor"

class EventProcessor(
    private val reactionDao: ReactionDao,
    private val contactDao: ContactDao,
    private val profileDao: ProfileDao,
    private val postDao: PostDao,
) : IEventProcessor {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val gson: Gson = GsonBuilder().disableHtmlEscaping().create()
    override fun process(event: Event) {
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
            Log.d(TAG, "Process profile event ${event}")
            processMetadata(event)
            return
        }

    }

    private fun processPost(event: Event) {
        if (!verify(event)) {
            return
        }
        scope.launch {
            postDao.insertIfNotPresent(
                PostEntity(
                    id = event.id,
                    pubkey = event.pubkey,
                    replyToId = event.getReplyId(),
                    replyToRootId = event.getRootReplyId(),
                    repostedId = event.getRepostedId(),
                    content = event.content,
                    createdAt = event.createdAt,
                )
            )
        }
    }

    private fun processReaction(event: Event) {
        if (event.content != "+") return

        if (!verify(event)) {
            return
        }
        event.getReactedToId()?.let {
            scope.launch {
                reactionDao.like(eventId = it, pubkey = event.pubkey)
            }
        }
    }

    private fun processContactList(event: Event) {
        if (!verify(event)) {
            return
        }
        scope.launch {
            contactDao.deleteIfOutdated(pubkey = event.pubkey, createdAt = event.createdAt)
            val contacts = getContactPubkeysAndRelayUrls(event.tags).map {
                ContactEntity(
                    pubkey = event.pubkey,
                    contactPubkey = it.first,
                    relayUrl = it.second,
                    createdAt = event.createdAt
                )
            }
            contactDao.insertOrIgnore(*contacts.toTypedArray())
        }

    }

    private fun processMetadata(event: Event) {
        if (!verify(event)) {
            Log.d(TAG, "Metadata is invalid ${event.id}")
            return
        }
        deserializeMetadata(event.content)?.let {
            scope.launch {
                profileDao.deleteIfOutdated(pubkey = event.pubkey, createdAt = event.createdAt)
                profileDao.insertOrIgnore(
                    ProfileEntity(
                        pubkey = event.pubkey,
                        name = it.name.orEmpty(),
                        about = it.about.orEmpty(),
                        picture = it.picture.orEmpty(),
                        nip05 = it.nip05.orEmpty(),
                        createdAt = event.createdAt,
                    )
                )
            }
        }
    }

    private fun verify(event: Event): Boolean {
        val isValid = event.verify()
        if (!isValid) {
            Log.d(TAG, "Invalid event ${event.id} kind ${event.kind}")
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

    private fun getContactPubkeysAndRelayUrls(tags: List<Tag>): List<Pair<String, String>> {
        val result = mutableListOf<Pair<String, String>>()
        for (tag in tags) {
            if (tag.size >= 3 && tag[0] == "p") {
                result.add(Pair(tag[1], tag[2]))
            }
        }
        return result
    }
}
