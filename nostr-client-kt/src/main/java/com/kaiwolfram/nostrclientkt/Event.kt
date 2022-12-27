package com.kaiwolfram.nostrclientkt

import com.google.gson.annotations.SerializedName

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

        fun create(kind: Int, tags: List<Tag>, content: String, keys: Keys): Event {
            TODO()
        }

        fun createMetaDataEvent(metaData: MetaData, keys: Keys): Event {
            TODO()
        }

        fun createContactListEvent(contacts: List<Contact>, keys: Keys): Event {
            TODO()
        }

        fun createTextNoteEvent(post: Post, keys: Keys): Event {
            TODO()
        }

        fun createReactionEvent(
            eventId: String,
            eventPubkey: String,
            isPositive: Boolean,
            keys: Keys
        ): Event {
            TODO()
        }
    }

    fun toJson(): String = gson.toJson(this)

    fun verify(): Boolean {
        TODO()
    }
}
