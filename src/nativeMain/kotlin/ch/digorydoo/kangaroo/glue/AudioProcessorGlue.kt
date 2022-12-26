@file:Suppress("unused")

package ch.digorydoo.kangaroo.glue

import ch.digorydoo.kangaroo.audio.AudioProcessor
import ch.digorydoo.kangaroo.param.ParamReadStream
import ch.digorydoo.kangaroo.param.ParamWriteStream
import interop.SMidiEvent
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer

private val processors = mutableMapOf<Long, AudioProcessor>()

fun createAudioProcessor(ptr: Long) {
    if (processors.containsKey(ptr)) {
        println("Error: AudioProcessorGlue: The map already contains a processor with ptr=$ptr")
    } else {
        processors[ptr] = AudioProcessor(ptr)
    }
}

fun destroyAudioProcessor(ptr: Long) {
    if (processors.containsKey(ptr)) {
        processors.remove(ptr)
    } else {
        println("Error: AudioProcessorGlue: The map does not contain any processor with ptr=$ptr")
    }
}

fun audioSetActive(ptr: Long, sampleRate: Double) {
    processors[ptr]?.onSetActive(sampleRate)
}

fun audioSetInactive(ptr: Long) {
    processors[ptr]?.onSetInactive()
}

fun audioReadState(processorPtr: Long, streamPtr: Long): Boolean {
    val p = processors[processorPtr] ?: return false
    val stream = ParamReadStream(streamPtr)
    return p.onReadState(stream)
}

fun audioWriteState(processorPtr: Long, streamPtr: Long): Boolean {
    val p = processors[processorPtr] ?: return false
    val stream = ParamWriteStream(streamPtr)
    return p.onWriteState(stream)
}

fun audioSetParam(ptr: Long, param: Int, value: Float) {
    processors[ptr]?.onSetParam(param, value)
}

fun audioFlush(ptr: Long) {
    processors[ptr]?.onFlush()
}

fun audioProcessBlock(
    ptr: Long,
    numInputParamChanges: Int,
    numSamples: Int,
    leftBuf: CPointer<ByteVar>,
    rightBuf: CPointer<ByteVar>,
    samplesAreFloat: Int,
): Boolean {
    val p = processors[ptr] ?: return false
    return p.onProcessBlock(numInputParamChanges, numSamples, leftBuf, rightBuf, samplesAreFloat != 0)
}

fun audioProcessEvent(ptr: Long, evt: SMidiEvent) {
    processors[ptr]?.onProcessEvent(evt)
}
