package ch.digorydoo.kangaroo.audio

import ch.digorydoo.kangaroo.param.ParamTag
import interop.CC_OUT_EVENT
import interop.NOTE_OFF_EVENT
import interop.NOTE_ON_EVENT
import interop.SMidiEvent

/**
 * This is a queue of audio events. For efficiency reasons, we must avoid creating new objects in the
 * audio processor, that's why this class uses a fixed pool of entries.
 */
class Queue {
    enum class EntryType { UNUSED, PARAM_CHANGE, NOTE_ON, NOTE_OFF, CC_OUT }

    class Entry {
        var type = EntryType.UNUSED
        var sampleOffset = 0
        var tag: ParamTag = ParamTag.MASTER_VOLUME
        var paramValue = 0.0f
        var busIndex = 0
        var ppqPosition = 0.0
        var isLive = false
        var user1Bit = false
        var user2Bit = false
        var channel = 0
        var pitch = 0
        var tuning = 0.0f
        var velocity = 0.0f
        var noteLength = 0
        var noteId = 0
        var ccNumber = 0
        var ccValue = 0
        var ccValue2 = 0
    }

    private val pool = Array(POOL_SIZE) { Entry() }
    private val queue = Array(POOL_SIZE) { -1 }

    var size = 0; private set

    fun isNotEmpty() =
        size > 0

    fun clear() {
        size = 0
    }

    fun top() =
        if (size <= 0) {
            throw Error("Cannot access top, queue is empty!")
        } else {
            pool[queue[0]]
        }

    fun anyForOffset(offset: Int): Boolean =
        if (size <= 0) false else top().sampleOffset <= offset

    fun addParamChangeEvent(sampleOffset: Int, tag: ParamTag, value: Float) {
        val poolIdx = findUnusedPoolIdx()
        if (poolIdx < 0) return
        enqueue(poolIdx, sampleOffset) // stores poolIdx in queue

        // For efficiency reasons, we're not clearing the values we're not going to need.
        val entry = pool[poolIdx]
        entry.type = EntryType.PARAM_CHANGE
        entry.sampleOffset = sampleOffset
        entry.tag = tag
        entry.paramValue = value
    }

    fun addMidiEvent(evt: SMidiEvent) {
        val type = when (evt.type) {
            NOTE_ON_EVENT -> EntryType.NOTE_ON
            NOTE_OFF_EVENT -> EntryType.NOTE_OFF
            CC_OUT_EVENT -> EntryType.CC_OUT
            else -> return
        }

        val poolIdx = findUnusedPoolIdx()
        if (poolIdx < 0) return
        enqueue(poolIdx, evt.sampleOffset) // stores poolIdx in queue

        // The SMidiEvent's is a C struct whose lifetime is limited, so we need to copy the
        // values we need. For efficiency reasons, we're not clearing the entry's other values.
        val entry = pool[poolIdx]
        entry.type = type
        entry.sampleOffset = evt.sampleOffset
        entry.busIndex = evt.busIndex
        entry.ppqPosition = evt.ppqPosition
        entry.isLive = evt.isLive != 0
        entry.user1Bit = evt.user1Bit != 0
        entry.user2Bit = evt.user2Bit != 0
        entry.channel = evt.channel
        entry.pitch = evt.pitch
        entry.tuning = evt.tuning
        entry.velocity = evt.velocity
        entry.noteLength = evt.noteLength
        entry.noteId = evt.noteId
        entry.ccNumber = evt.ccNumber
        entry.ccValue = evt.ccValue
        entry.ccValue2 = evt.ccValue2
    }

    /**
     * Removes the top entry in queue by moving all entries on index up and decrementing the size by one.
     * The entry that was top is marked as UNUSED, so we may reuse it later.
     */
    fun removeTop() {
        if (size <= 0) {
            throw Error("Cannot remove top, queue is empty!")
        }

        val entry = pool[queue[0]]

        // Move all entries in queue one index up.

        for (i in 0 until size - 1) {
            queue[i] = queue[i + 1]
        }

        size--
        entry.type = EntryType.UNUSED
    }

    private fun enqueue(poolIdx: Int, sampleOffset: Int) {
        // Find the position in queue where to insert the new entry.
        // We're not using indexOfFirst() in order to avoid creating lambda objects.

        var idx = 0

        while (idx < size) {
            val entry = pool[queue[idx]]

            if (entry.sampleOffset > sampleOffset) {
                break
            } else {
                idx++
            }
        }

        // Move all entries below idx one index down to make room for the new entry.

        for (i in size downTo idx + 1) {
            queue[i] = queue[i - 1]
        }

        queue[idx] = poolIdx
        size++
    }

    private fun findUnusedPoolIdx(): Int {
        // We're not using find() in order to avoid creating lambda objects.
        for (i in pool.indices) {
            if (pool[i].type == EntryType.UNUSED) return i
        }
        println("Queue: Error: Ran out of pool entries!")
        return -1
    }

    companion object {
        private const val POOL_SIZE = 100
    }
}
