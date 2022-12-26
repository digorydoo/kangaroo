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
                OSC1_SHAPE -> writeParam(tag, params.layerA.osc1.shape)
                OSC1_VOLUME -> writeParam(tag, params.layerA.osc1.volume)
                OSC1_PITCH -> writeParamWithRange(tag, params.layerA.osc1.pitch)
                OSC1_DETUNE -> writeBalancedParam(tag, params.layerA.osc1.detune)
                OSC2_SHAPE -> writeParam(tag, params.layerA.osc2.shape)
                OSC2_VOLUME -> writeParam(tag, params.layerA.osc2.volume)
                OSC2_PITCH -> writeParamWithRange(tag, params.layerA.osc2.pitch)
                OSC2_DETUNE -> writeBalancedParam(tag, params.layerA.osc2.detune)
                OSC3_SHAPE -> writeParam(tag, params.layerA.osc3.shape)
                OSC3_VOLUME -> writeParam(tag, params.layerA.osc3.volume)
                OSC3_DETUNE -> writeBalancedParam(tag, params.layerA.osc3.detune)
                OSC3_FOLD_OSC1 -> writeParam(tag, params.layerA.osc3.foldOsc1)
                NOISE_SHAPE -> writeParam(tag, params.layerA.noise.shape)
                NOISE_VOLUME -> writeParam(tag, params.layerA.noise.volume)
                LOW_PASS_FREQ -> writeParamWithRange(tag, params.layerA.filter.lpfFreq)
                HIGH_PASS_FREQ -> writeParamWithRange(tag, params.layerA.filter.hpfFreq)
                BAND_PASS_FREQ -> writeParamWithRange(tag, params.layerA.filter.bpfFreq)
                BAND_PASS_AMOUNT -> writeParam(tag, params.layerA.filter.bpfAmount)
                VOWEL_FORMANT -> writeParam(tag, params.layerA.filter.vowelFormant)
                VOWEL_AMOUNT -> writeParam(tag, params.layerA.filter.vowelAmount)
                LFO1_SHAPE -> writeParam(tag, params.layerA.lfo1.shape)
                LFO2_SHAPE -> writeParam(tag, params.layerA.lfo2.shape)
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
