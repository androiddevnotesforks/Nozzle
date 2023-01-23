package com.kaiwolfram.nozzle.data.eventProcessor

import com.kaiwolfram.nostrclientkt.model.Event

interface IEventProcessor {
    fun process(event: Event)
}
