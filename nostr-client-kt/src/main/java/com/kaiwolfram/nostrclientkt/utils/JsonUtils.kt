package com.kaiwolfram.nostrclientkt.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object JsonUtils {
    val gson: Gson by lazy { GsonBuilder().disableHtmlEscaping().create() }
}
