package com.kaiwolfram.nozzle.model

sealed class RelaySelection
object AllRelays : RelaySelection()
class MultipleRelays(val relays: List<String>) : RelaySelection()
