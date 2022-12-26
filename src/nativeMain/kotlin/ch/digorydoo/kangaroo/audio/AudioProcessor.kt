package ch.digorydoo.kangaroo.audio

import ch.digorydoo.kangaroo.param.AudioParams
import ch.digorydoo.kangaroo.param.ParamReadStream
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.param.ParamWriteStream
import interop.SMidiEvent
import interop.cppGetInputParamChange
import interop.cppSetOutputParamChange
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.DoubleVar
import kotlinx.cinterop.FloatVar
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.set
import kotlinx.cinterop.useContents

class AudioProcessor(private val ptr: Long) {
    class StereoSample {
        var left = 0.0
        var right = 0.0
    }

    private val params = AudioParams()
    private val queue = Queue()
    private val voices = VoiceCollection(params)
    private val peakUpdater = OutputPeakUpdater(this)
    private var active = false
    private var isInProcessBlock = false
    private val sample = StereoSample()

    fun onReadState(stream: ParamReadStream) =
        stream.read(params)

    fun onWriteState(stream: ParamWriteStream) =
        stream.write(params)

    fun onSetParam(param: Int, value: Float) {
        // FIXME can I remove this function?
        println("AudioProcessor: onSetParam($param, $value)")
    }

    fun onSetActive(rate: Double) {
        voices.setSampleRate(rate)
        peakUpdater.setSampleRate(rate)
        active = true
    }

    fun onSetInactive() {
        active = false
        queue.clear()
    }

    fun onProcessEvent(evt: SMidiEvent) {
        if (isInProcessBlock) {
            println("Warning: AudioProcessor: onProcessEvent called concurrently!")
        }

        queue.addMidiEvent(evt)
    }

    fun onFlush() {
        println("AudioProcessor: onFlush")
    }

    fun onProcessBlock(
        numInputParamChanges: Int,
        numSamples: Int,
        rawLeftBuf: CPointer<ByteVar>,
        rawRightBuf: CPointer<ByteVar>,
        samplesAreFloat: Boolean,
    ): Boolean {
        isInProcessBlock = true

        // Get the input param changes and add them to the queue

        for (i in 0 until numInputParamChanges) {
            cppGetInputParamChange(ptr, i).useContents {
                ParamTag.fromInt(tag)?.let { paramTag ->
                    queue.addParamChangeEvent(sampleOffset, paramTag, value)
                }
            }
        }

        // Fill the buffer

        if (samplesAreFloat) {
            val leftBuf = rawLeftBuf.reinterpret<FloatVar>()
            val rightBuf = rawRightBuf.reinterpret<FloatVar>()

            for (i in 0 until numSamples) {
                computeNextSample(i)
                leftBuf[i] = sample.left.toFloat()
                rightBuf[i] = sample.right.toFloat()
            }
        } else {
            val leftBuf = rawLeftBuf.reinterpret<DoubleVar>()
            val rightBuf = rawRightBuf.reinterpret<DoubleVar>()

            for (i in 0 until numSamples) {
                computeNextSample(i)
                leftBuf[i] = sample.left
                rightBuf[i] = sample.right
            }
        }

        // Handle all events still in the queue

        if (queue.isNotEmpty()) {
            println("Warning: AudioProcessor: onEndBlock: There are ${queue.size} unprocessed events")
        }

        while (queue.isNotEmpty()) {
            handleEvent(queue.top())
            queue.removeTop()
        }

        isInProcessBlock = false
        return voices.anyPlaying()
    }

    private inline fun computeNextSample(blockOffset: Int) {
        while (queue.anyForOffset(blockOffset)) {
            handleEvent(queue.top())
            queue.removeTop()
        }

        voices.computeNextSample(sample)
        peakUpdater.update(sample)
    }

    private fun handleEvent(evt: Queue.Entry) {
        when (evt.type) {
            Queue.EntryType.PARAM_CHANGE -> params.set(evt.tag, evt.paramValue.toDouble())
            Queue.EntryType.NOTE_ON -> voices.onNoteOn(evt.pitch, evt.tuning, evt.velocity)
            Queue.EntryType.NOTE_OFF -> voices.onNoteOff(evt.pitch)
            Queue.EntryType.CC_OUT -> CCNumber.fromInt(evt.ccNumber)?.let { voices.onCCOutEvent(it) }
            Queue.EntryType.UNUSED -> println("Error: AudioProcessor: UNUSED entry passed to handleEvent")
        }
    }

    fun setOutputParamChange(tag: ParamTag, value: Float) {
        cppSetOutputParamChange(ptr, tag.value, value)
    }
}
