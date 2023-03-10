package com.kaiwolfram.nozzle.data.eventProcessor

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kaiwolfram.nostrclientkt.model.Event
import com.kaiwolfram.nostrclientkt.model.Metadata
import com.kaiwolfram.nostrclientkt.model.Tag
import com.kaiwolfram.nozzle.data.room.dao.*
import com.kaiwolfram.nozzle.data.room.entity.ContactEntity
import com.kaiwolfram.nozzle.data.room.entity.Nip65Entity
import com.kaiwolfram.nozzle.data.room.entity.PostEntity
import com.kaiwolfram.nozzle.data.room.entity.ProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "EventProcessor"

class EventProcessor(
    private val reactionDao: ReactionDao,
    private val contactDao: ContactDao,
    private val profileDao: ProfileDao,
    private val postDao: PostDao,
    private val eventRelayDao: EventRelayDao,
    private val nip65Dao: Nip65Dao,
) : IEventProcessor {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val gson: Gson = GsonBuilder().disableHtmlEscaping().create()
    private val idCache = Collections.synchronizedSet(mutableSetOf<String>())
    private val idRelayCache = Collections.synchronizedSet(mutableSetOf<String>())

    override fun process(event: Event, relayUrl: String?) {
        if (event.isReaction()) {
            processReaction(event = event)
            return
        }
        if (event.isPost()) {
            processPost(event = event, relayUrl = relayUrl)
            return
        }
        if (event.isProfileMetadata()) {
            processMetadata(event = event)
            return
        }
        if (event.isContactList()) {
            processContactList(event = event)
            return
        }
        if (event.isNip65()) {
            processNip65(event = event)
            return
        }
    }

    private fun processPost(event: Event, relayUrl: String?) {
        if (!verify(event)) return
        insertEventRelay(eventId = event.id, relayUrl = relayUrl)

        if (idCache.contains(event.id)) return
        idCache.add(event.id)

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
        if (idCache.contains(event.id)) return
        if (!verify(event)) return

        idCache.add(event.id)

        event.getReactedToId()?.let {
            scope.launch {
                reactionDao.like(eventId = it, pubkey = event.pubkey)
            }
        }
    }

    private fun processContactList(event: Event) {
        if (idCache.contains(event.id)) return
        if (!verify(event)) return

        idCache.add(event.id)

        scope.launch {
            // TODO: dao.deleteIfOutdated():Boolean to save one call
            // TODO: room Transaction
            val latestTimestamp = contactDao.getLatestTimestamp(event.pubkey) ?: 0
            if (event.createdAt > latestTimestamp) {
                contactDao.deleteList(pubkey = event.pubkey)
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
    }

    private fun processMetadata(event: Event) {
        if (idCache.contains(event.id)) return
        if (!verify(event)) return

        idCache.add(event.id)

        Log.d(TAG, "Process profile event ${event.content}")
        deserializeMetadata(event.content)?.let {
            scope.launch {
                // TODO: Return if deleted to save insert call
                // TODO: room Transaction
                profileDao.deleteIfOutdated(pubkey = event.pubkey, createdAt = event.createdAt)
                profileDao.insertOrIgnore(
                    ProfileEntity(
                        pubkey = event.pubkey,
                        name = it.name.orEmpty(),
                        about = it.about.orEmpty(),
                        picture = it.picture.orEmpty(),
                        nip05 = it.nip05.orEmpty(),
                        lud16 = it.lud16.orEmpty(),
                        createdAt = event.createdAt,
                    )
                )
            }
        }
    }

    private fun processNip65(event: Event) {
        if (idCache.contains(event.id)) return

        val nip65Entries = event.getNip65Entries()
        if (nip65Entries.isEmpty()) return
        if (!verify(event)) return

        idCache.add(event.id)

        Log.d(TAG, "Process ${nip65Entries.size} nip65 entries from ${event.pubkey}")
        scope.launch {
            val entities = nip65Entries.map {
                Nip65Entity(
                    pubkey = event.pubkey,
                    url = it.url,
                    isRead = it.isRead,
                    isWrite = it.isWrite,
                    createdAt = event.createdAt,
                )
            }
            nip65Dao.insertAndDeleteOutdated(
                pubkey = event.pubkey,
                timestamp = event.createdAt,
                nip65Entities = entities.toTypedArray()
            )
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
            if (tag.size >= 2 && tag[0] == "p") {
                result.add(Pair(tag[1], tag.getOrNull(2).orEmpty()))
            }
        }
        return result
    }

    private fun insertEventRelay(eventId: String, relayUrl: String?) {
        if (relayUrl == null) return

        val id = eventId + relayUrl
        if (idRelayCache.contains(id)) return
        idRelayCache.add(id)

        scope.launch {
            eventRelayDao.insertOrIgnore(eventId = eventId, relayUrl = relayUrl)
        }
    }
}
