package com.kaiwolfram.lib

import com.google.gson.annotations.SerializedName

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
    fun toJson() = gson.toJson(this)
}
