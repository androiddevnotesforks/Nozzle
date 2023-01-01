package com.kaiwolfram.nozzle.data.eventProcessor

import com.kaiwolfram.nostrclientkt.Event

interface IEventProcessor {
    fun process(event: Event)
}
