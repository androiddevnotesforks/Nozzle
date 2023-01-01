package com.kaiwolfram.nostrclientkt

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.kaiwolfram.nostrclientkt.utils.JsonUtils.gson
import com.kaiwolfram.nostrclientkt.utils.SchnorrUtils
import com.kaiwolfram.nostrclientkt.utils.SchnorrUtils.secp256k1
import com.kaiwolfram.nostrclientkt.utils.Sha256Utils.sha256
import fr.acinq.secp256k1.Hex

typealias Tag = List<String>

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
        const val SET_METADATA = 0
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
                kind = Kind.SET_METADATA,
                tags = listOf(),
                content = gson.toJson(metadata),
                keys = keys
            )
        }

        fun createContactListEvent(contacts: List<String>, keys: Keys): Event {
            return create(
                kind = Kind.CONTACT_LIST,
                // TODO: Set relayUrl
                tags = contacts.map { listOf("p", it, "", "") },
                content = "",
                keys = keys
            )
        }

        fun createTextNoteEvent(post: Post, keys: Keys): Event {
            val tags = mutableListOf<List<String>>()

            post.replyTo?.replyToRoot?.let { replyToRoot ->
                post.replyTo.let { tags.add(listOf("e", replyToRoot, it.relayUrl, "root")) }
            }
            post.replyTo?.let { tags.add(listOf("e", it.replyTo, it.relayUrl, "reply")) }
            post.repostId?.let { tags.add(listOf("e", it.repostId, it.relayUrl)) }

            val mentionTag = mutableListOf("p")
            post.mentions.forEach { mentionTag.add(it) }
            tags.add(mentionTag)

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

    fun isReaction() = this.kind == Kind.REACTION
    fun isPost() = this.kind == Kind.TEXT_NOTE
    fun isProfileMetadata() = this.kind == Kind.SET_METADATA
    fun isContactList() = this.kind == Kind.CONTACT_LIST
}
