package ch.digorydoo.kangaroo.param

import ch.digorydoo.kangaroo.param.ParamTag.*
import interop.cppWriteDouble
import interop.cppWriteInt16

class ParamWriteStream(private val ptr: Long): ParamStream() {
    fun write(params: ParamsList): Boolean {
        params.forEach { param, value ->
            writeParam(param.tag, value.toDouble())
        }
        writeInt16(END_MARKER)
        return true
    }

    fun write(params: AudioParams): Boolean {
        ParamTag.values.forEach { tag ->
            when (tag) {
                MASTER_VOLUME -> writeParam(tag, params.masterVolume)
                MOD_WHEEL -> Unit
                PITCH_BEND -> Unit
                AFTER_TOUCH -> Unit
                DAMPER_PEDAL -> Unit
                ALL_NOTES_OFF -> Unit
                OUTPUT_LEFT_PEAK -> Unit
                OUTPUT_RIGHT_PEAK -> Unit
                OSC1_SHAPE -> writeParam(tag, params.layerA.osc1Shape)
                OSC1_VOLUME -> writeParam(tag, params.layerA.osc1Volume)
                OSC1_PITCH -> writeParamWithRange(tag, params.layerA.osc1Pitch)
                OSC1_DETUNE -> writeBalancedParam(tag, params.layerA.osc1Detune)
                OSC2_SHAPE -> writeParam(tag, params.layerA.osc2Shape)
                OSC2_VOLUME -> writeParam(tag, params.layerA.osc2Volume)
                OSC2_PITCH -> writeParamWithRange(tag, params.layerA.osc2Pitch)
                OSC2_DETUNE -> writeBalancedParam(tag, params.layerA.osc2Detune)
                OSC3_SHAPE -> writeParam(tag, params.layerA.osc3Shape)
                OSC3_VOLUME -> writeParam(tag, params.layerA.osc3Volume)
                OSC3_DETUNE -> writeBalancedParam(tag, params.layerA.osc3Detune)
                OSC3_FOLD_OSC1 -> writeParam(tag, params.layerA.osc3FoldOsc1)
                NOISE_SHAPE -> writeParam(tag, params.layerA.noiseShape)
                NOISE_VOLUME -> writeParam(tag, params.layerA.noiseVolume)
                LOW_PASS_FREQ -> writeParamWithRange(tag, params.layerA.lpfFreq)
                HIGH_PASS_FREQ -> writeParamWithRange(tag, params.layerA.hpfFreq)
                BAND_PASS_FREQ -> writeParamWithRange(tag, params.layerA.bpfFreq)
                BAND_PASS_AMOUNT -> writeParam(tag, params.layerA.bpfAmount)
                VOWEL_FORMANT -> writeParam(tag, params.layerA.vowelFormant)
                VOWEL_AMOUNT -> writeParam(tag, params.layerA.vowelAmount)
                LFO1_SHAPE -> writeParam(tag, params.layerA.lfo1Shape)
                LFO2_SHAPE -> writeParam(tag, params.layerA.lfo2Shape)
                ENV1_ATTACK -> writeParamWithRange(tag, params.layerA.env1.attackMillis)
                ENV1_DECAY -> writeParamWithRange(tag, params.layerA.env1.decayMillis)
                ENV1_SUSTAIN -> writeParam(tag, params.layerA.env1.sustainLevel)
                ENV1_RELEASE -> writeParamWithRange(tag, params.layerA.env1.releaseMillis)
            }
        }
        writeInt16(END_MARKER)
        return true
    }

    private fun writeBalancedParam(tag: ParamTag, value: Double) {
        val normValue = (value + 1) / 2
        writeParam(tag, normValue)
    }

    private fun writeParamWithRange(tag: ParamTag, value: Double) {
        val normValue = (ParamsList.get(tag) as? Param.RangeParam)
            ?.let { (value - it.minValue) / (it.maxValue - it.minValue) }
            ?: value
        writeParam(tag, normValue)
    }

    private fun writeParam(tag: ParamTag, value: Double) {
        writeInt16(tag.value)
        writeDouble(value)
    }

    private fun writeInt16(value: Int): Boolean {
        return cppWriteInt16(ptr, value) != 0
    }

    private fun writeDouble(value: Double): Boolean {
        return cppWriteDouble(ptr, value) != 0
    }

    override fun toString() =
        "ParamWriteStream($ptr)"
}
