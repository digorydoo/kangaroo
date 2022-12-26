package ch.digorydoo.kangaroo.param

import interop.cppReadDouble
import interop.cppReadInt16
import kotlinx.cinterop.useContents

class ParamReadStream(private val ptr: Long): ParamStream() {
    fun read(params: ParamsList) =
        readEntries { tag, value ->
            params.setValueNormalized(tag, value.toFloat())
        }

    fun read(params: AudioParams) =
        readEntries { tag, value ->
            params.set(tag, value)
        }

    private fun readEntries(lambda: (tag: ParamTag, value: Double) -> Unit): Boolean {
        while (true) {
            val marker = readInt16()

            if (marker == END_MARKER) {
                break
            } else if (marker == null) {
                println("ParamReadStream: Warning: Missing end marker")
                break
            }

            val tag = ParamTag.fromInt(marker)

            if (tag == null) {
                println("ParamReadStream: Warning: Ignoring unknown marker $marker")
                readDouble() // skip expected value
                continue
            }

            val value = readDouble()

            if (value == null) {
                println("ParamReadStream: Warning: Unexpected end of stream after $tag")
                break
            }

            lambda(tag, value)
        }
        return true
    }

    private fun readInt16(): Int? {
        return cppReadInt16(ptr).useContents {
            if (ok == 0) null else value
        }
    }

    private fun readDouble(): Double? {
        return cppReadDouble(ptr).useContents {
            if (ok == 0) null else value
        }
    }

    override fun toString() =
        "ParamReadStream($ptr)"
}
