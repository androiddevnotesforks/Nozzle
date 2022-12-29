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
        const val SET_META_DATA = 0
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

        fun createMetaDataEvent(metaData: MetaData, keys: Keys): Event {
            return create(
                kind = Kind.SET_META_DATA,
                tags = listOf(),
                content = gson.toJson(metaData),
                keys = keys
            )
        }

        fun createContactListEvent(contacts: List<Contact>, keys: Keys): Event {
            return create(
                kind = Kind.CONTACT_LIST,
                tags = contacts.map { listOf("p", it.pubkey, it.relay, it.alias) },
                content = "",
                keys = keys
            )
        }

        fun createTextNoteEvent(post: Post, keys: Keys): Event {
            val tags = mutableListOf<List<String>>()
            post.replyTos.forEach { tags.add(listOf("e", it, post.relayUrl, "reply")) }

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
            eventId: String,
            eventPubkey: String,
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
}
