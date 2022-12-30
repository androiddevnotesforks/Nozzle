package com.kaiwolfram.nostrclientkt

import com.google.gson.annotations.SerializedName
import com.kaiwolfram.nostrclientkt.utils.JsonUtils.gson

class Filter(
    val ids: List<String>? = null,
    val authors: List<String>? = null,
    val kinds: List<Int>? = null,
    @SerializedName("#e") val e: List<String>? = null,
    @SerializedName("#p") val p: List<String>? = null,
    val since: Long? = null,
    val until: Long? = null,
    val limit: Int? = null
) {
    fun toJson(): String = gson.toJson(this)

    companion object {
        fun createProfileFilter(
            pubkey: String,
        ): Filter {
            return Filter(
                authors = listOf(pubkey),
                kinds = listOf(Event.Kind.CONTACT_LIST),
                limit = 1
            )
        }

        fun createPersonalPostsFilter(
            pubkey: String,
            since: Long? = null,
            until: Long? = null,
            limit: Int? = null
        ): Filter {
            return Filter(
                authors = listOf(pubkey),
                kinds = listOf(Event.Kind.TEXT_NOTE),
                since = since,
                until = until,
                limit = limit
            )
        }

        fun createPersonalContactListFilter(
            pubkey: String,
            since: Long? = null,
            until: Long? = null,
            limit: Int? = null
        ): Filter {
            return Filter(
                authors = listOf(pubkey),
                kinds = listOf(Event.Kind.CONTACT_LIST),
                since = since,
                until = until,
                limit = limit
            )
        }
    }
}
