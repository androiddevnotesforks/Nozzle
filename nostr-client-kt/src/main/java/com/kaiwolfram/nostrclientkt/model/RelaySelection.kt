package com.kaiwolfram.nostrclientkt.model

sealed class RelaySelection
object AllRelays : RelaySelection()
object Autopilot : RelaySelection()
object PersonalNip65 : RelaySelection()
class MultipleRelays(val relays: List<String>) : RelaySelection()
