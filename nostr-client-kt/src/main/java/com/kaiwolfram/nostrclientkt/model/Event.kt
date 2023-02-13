package com.kaiwolfram.nostrclientkt.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.kaiwolfram.nostrclientkt.ContactListEntry
import com.kaiwolfram.nostrclientkt.Keys
import com.kaiwolfram.nostrclientkt.Post
import com.kaiwolfram.nostrclientkt.utils.JsonUtils.gson
import com.kaiwolfram.nostrclientkt.utils.SchnorrUtils
import com.kaiwolfram.nostrclientkt.utils.SchnorrUtils.secp256k1
import com.kaiwolfram.nostrclientkt.utils.Sha256Utils.sha256
import fr.acinq.secp256k1.Hex

typealias Tag = List<String>

fun Tag.getNip10Marker() = this.getOrNull(3)

class Event(
    val id: String,
    val pubkey: String,
    @SerializedName("created_at") val createdAt: Long,
    val kind: Int,
    val tags: List<Tag>,
    val content: String,
    val sig: String,
) {
    object Kind {
        const val METADATA = 0
        const val TEXT_NOTE = 1
        const val CONTACT_LIST = 3
        const val REACTION = 7
    }

    companion object {
        fun fromJson(json: String): Result<Event> {
            return kotlin.runCatching { gson.fromJson(json, Event::class.java) }
        }

        fun fromJson(json: JsonElement): Result<Event> {
            return kotlin.runCatching { gson.fromJson(json, Event::class.java) }
        }

        private fun generateId(
            pubkey: String,
            createdAt: Long,
            kind: Int,
            tags: List<Tag>,
            content: String
        ): ByteArray {
            val event = listOf(
                0,
                pubkey,
                createdAt,
                kind,
                tags,
                content
            )
            val json = gson.toJson(event)
            return sha256.digest(json.toByteArray())
        }

        fun create(kind: Int, tags: List<Tag>, content: String, keys: Keys): Event {
            val pubkey = Hex.encode(keys.pubkey)
            val createdAt = System.currentTimeMillis() / 1000
            val id = generateId(
                pubkey,
                createdAt,
                kind,
                tags,
                content
            )
            val sig = SchnorrUtils.sign(id, keys.privkey)
            return Event(
                id = Hex.encode(id),
                pubkey = pubkey,
                createdAt = createdAt,
                kind = kind,
                tags = tags,
                content = content,
                sig = Hex.encode(sig)
            )
        }

        fun createMetadataEvent(metadata: Metadata, keys: Keys): Event {
            return create(
                kind = Kind.METADATA,
                tags = listOf(),
                content = gson.toJson(metadata),
                keys = keys
            )
        }

        fun createContactListEvent(contacts: List<ContactListEntry>, keys: Keys): Event {
            return create(
                kind = Kind.CONTACT_LIST,
                tags = contacts.map { listOf("p", it.pubkey, it.relayUrl, "") }, // Empty petname
                content = "",
                keys = keys
            )
        }

        fun createTextNoteEvent(post: Post, keys: Keys): Event {
            val tags = mutableListOf<List<String>>()

            post.replyTo?.let { replyTo ->
                replyTo.replyToRoot?.let { tags.add(listOf("e", it, replyTo.relayUrl, "root")) }
            }
            post.replyTo?.let { tags.add(listOf("e", it.replyTo, it.relayUrl, "reply")) }
            post.repostId?.let { tags.add(listOf("e", it.repostId, it.relayUrl, "mention")) }

            if (post.mentions.isNotEmpty()) {
                val mentionTag = mutableListOf("p")
                post.mentions.forEach { mentionTag.add(it) }
                tags.add(mentionTag)
            }

            return create(
                kind = Kind.TEXT_NOTE,
                tags = tags,
                content = post.msg,
                keys = keys
            )
        }

        fun createReactionEvent(
            eventId: String, // Must be last e tag
            eventPubkey: String, // Must be last p tag
            isPositive: Boolean,
            keys: Keys
        ): Event {
            return create(
                kind = Kind.REACTION,
                tags = listOf(listOf("e", eventId), listOf("p", eventPubkey)),
                content = if (isPositive) "+" else "-",
                keys = keys
            )
        }
    }

    fun toJson(): String = gson.toJson(this)

    fun verify(): Boolean {
        val correctId = generateId(pubkey, createdAt, kind, tags, content)
        if (id != Hex.encode(correctId)) {
            return false
        }
        return secp256k1.verifySchnorr(Hex.decode(sig), Hex.decode(id), Hex.decode(pubkey))
    }

    fun getReplyId(): String? {
        val eventTags = tags.filter {
            it.size in 2..4
                    && it[0] == "e"
                    && (
                    when (it.getNip10Marker()) {
                        "reply" -> true
                        "root" -> true
                        null -> true
                        else -> false
                    }
                    )
        }
        if (eventTags.isEmpty()) return null

        val nip10Reply = eventTags.find { it.getNip10Marker() == "reply" }
        if (nip10Reply != null) return nip10Reply[1]

        val nip10Root = eventTags.find { it.getNip10Marker() == "root" }
        if (nip10Root != null) return nip10Root[1]

        // nip10 relational (deprecated)
        return when (eventTags.size) {
            1 -> eventTags[0][1]
            else -> eventTags[1][1]
        }
    }

    fun getRootReplyId(): String? {
        val eventTags = tags.filter {
            it.size in 2..4
                    && it[0] == "e"
                    && (it.getNip10Marker() == "root" || it.getNip10Marker() == null)
        }
        if (eventTags.isEmpty()) return null

        val nip10Marked = eventTags.find { it.getNip10Marker() == "root" }
        if (nip10Marked != null) return nip10Marked[1]

        // nip10 relational (deprecated)
        return eventTags[0][1]
    }

    fun getRepostedId(): String? {
        val astralCompliant = tags.find {
            it.size == 4
                    && it[0] == "e"
                    && it[3] == "mention"
        }
        return astralCompliant?.get(1)
    }

    fun getReactedToId(): String? {
        return tags.find { it.getOrNull(0) == "e" }?.getOrNull(1)
    }

    fun isReaction() = this.kind == Kind.REACTION
    fun isPost() = this.kind == Kind.TEXT_NOTE
    fun isProfileMetadata() = this.kind == Kind.METADATA
    fun isContactList() = this.kind == Kind.CONTACT_LIST
}
