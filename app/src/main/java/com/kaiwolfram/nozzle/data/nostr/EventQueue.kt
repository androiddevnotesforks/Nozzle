package com.kaiwolfram.nozzle.data.nostr

import android.util.Log
import com.kaiwolfram.nostrclientkt.Event
import java.util.*
import java.util.concurrent.SynchronousQueue

private const val TAG = "EventQueue"

class EventQueue {
    private val postQueue = SynchronousQueue<Event>()
    private val profileQueue = SynchronousQueue<Event>()
    private val contactListQueue = SynchronousQueue<Event>()
    private val likeQueue = SynchronousQueue<Event>()

    fun add(event: Event): Boolean {
        if (event.isPost() && postQueue.none { it.id == event.id }) {
            Log.i(TAG, "Try to queue post event ${event.id}")
            return verifyAndAdd(postQueue, event)
        }
        if (event.isMetadata() && profileQueue.none { it.id == event.id }) {
            Log.i(TAG, "Try to queue profile event ${event.id}")
            return verifyAndAdd(profileQueue, event)
        }
        if (event.isContactList() && contactListQueue.none { it.id == event.id }) {
            Log.i(TAG, "Try to queue contact list event ${event.id}")
            return verifyAndAdd(contactListQueue, event)
        }
        if (event.isReaction() && event.content == "+" && likeQueue.none { it.id == event.id }) {
            Log.i(TAG, "Try to queue reaction event ${event.id}")
            return verifyAndAdd(likeQueue, event)
        }
        return false
    }

    private fun verifyAndAdd(queue: Queue<Event>, event: Event): Boolean {
        return if (!event.verify()) {
            // TODO: Blacklist relay where this event was coming from
            Log.w(TAG, "Event ${event.id} is invalid")
            false
        } else {
            queue.add(event)
        }
    }
}
