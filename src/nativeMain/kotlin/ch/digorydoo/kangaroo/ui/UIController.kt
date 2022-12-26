package ch.digorydoo.kangaroo.ui

import ch.digorydoo.kangaroo.audio.CCNumber
import ch.digorydoo.kangaroo.param.Param
import ch.digorydoo.kangaroo.param.ParamReadStream
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.param.ParamWriteStream
import ch.digorydoo.kangaroo.param.ParamsList
import interop.*

class UIController(private val ptr: Long) {
    var canvas: CanvasView? = null; private set
    private val params = ParamsList()

    init {
        params.forEach { param ->
            when (param) {
                is Param.GenericParam -> cppAddGenericParam(
                    ptr,
                    param.tag.value,
                    param.title,
                    param.shortTitle,
                    param.units,
                    param.unitId,
                    param.flags,
                    param.defaultValue,
                    param.stepCount,
                    param.precision,
                )
                is Param.RangeParam -> cppAddRangeParam(
                    ptr,
                    param.tag.value,
                    param.title,
                    param.shortTitle,
                    param.units,
                    param.unitId,
                    param.flags,
                    param.minValue,
                    param.maxValue,
                    param.defaultValue,
                    param.stepCount,
                    param.precision,
                )
                is Param.StringListParam -> cppAddStringListParam(
                    ptr,
                    param.tag.value,
                    param.title,
                    param.shortTitle,
                    param.units,
                    param.unitId,
                    param.flags,
                    param.list.joinToString("\n"),
                )
            }
        }
    }

    fun link(c: CanvasView) {
        if (canvas != null) {
            println("UIController: link: Error: There is already a CanvasView!")
        } else {
            println("UIController: Linking to $c")
            canvas = c
            allParamsChanged() // update the UI
        }
    }

    fun unlink(c: CanvasView) {
        if (canvas != c) {
            println("UIController: unlink: Error: Not currently linked to $c")
        } else {
            println("UIController: Unlinking from $c")
            canvas = null
        }
    }

    fun getMidiControllerAssignment(busIndex: Int, channel: Int, cc: Int): Int {
        if (busIndex == 0 && channel == 0) {
            return CCNumber.getParamForCC(cc)?.value ?: -1
        }
        return -1
    }

    fun sendNoteOnEvent(pitch: Int, velocity: Float, channel: Int = 0, detune: Float = 0.0f) {
        cppSendNoteOnEvent(ptr, pitch, velocity, channel, detune)
    }

    fun sendNoteOffEvent(pitch: Int, velocity: Float, channel: Int = 0) {
        cppSendNoteOffEvent(ptr, pitch, velocity, channel)
    }

    fun sendMIDICCOutEvent(ccNumber: CCNumber, value: Int = 0, value2: Int = 0, channel: Int = 0) {
        cppSendMIDICCOutEvent(ptr, ccNumber.value, value, value2, channel)
    }

    fun beginParamEdit(tag: ParamTag) {
        cppBeginParamEdit(ptr, tag.value)
    }

    fun endParamEdit(tag: ParamTag) {
        cppEndParamEdit(ptr, tag.value)
    }

    /**
     * Informs cpp about a parameter change, e.g. when a knob is turned.
     * Cpp will call us back via paramChanged.
     * @param value: needs to be within 0..1
     * @param performEdit: true = notify the AudioProcessor
     */
    fun setParamNormalized(tag: ParamTag, value: Float, performEdit: Boolean = true) {
        cppSetParamNormalized(ptr, tag.value, value, if (performEdit) 1 else 0)
    }

    /**
     * Called by cpp when a parameter was changed. This is called as a result of setParam as well as when
     * the parameter was changed by the AudioProcessor.
     * @param newNormValue: will always be in 0..1
     */
    fun onParamChanged(tag: ParamTag, newNormValue: Float) {
        params.setValueNormalized(tag, newNormValue)
        canvas?.onParamChanged(tag, newNormValue)
    }

    private fun allParamsChanged() {
        params.forEach { param, value ->
            canvas?.onParamChanged(param.tag, value)
        }
    }

    fun onReadState(stream: ParamReadStream): Boolean {
        val result = stream.read(params)
        allParamsChanged()
        return result
    }

    fun onWriteState(stream: ParamWriteStream): Boolean {
        return stream.write(params)
    }
}
