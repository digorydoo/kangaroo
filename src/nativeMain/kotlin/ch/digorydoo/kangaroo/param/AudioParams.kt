package ch.digorydoo.kangaroo.param

import ch.digorydoo.kangaroo.param.Param.RangeParam
import ch.digorydoo.kangaroo.param.ParamTag.*

class AudioParams {
    class EnvelopeParams {
        var attackMillis = 0.0
        var decayMillis = 0.0
        var sustainLevel = 0.0
        var releaseMillis = 0.0
    }

    class LayerParams {
        var osc1Shape = 0.0
        var osc1Volume = 0.0
        var osc1Pitch = 0.0
        var osc1Detune = 0.0

        var osc2Shape = 0.0
        var osc2Volume = 0.0
        var osc2Pitch = 0.0
        var osc2Detune = 0.0

        var osc3Shape = 0.0
        var osc3Volume = 0.0
        var osc3Detune = 0.0
        var osc3FoldOsc1 = 0.0

        var noiseShape = 0.0
        var noiseVolume = 0.0

        var lpfFreq = 0.0
        var hpfFreq = 0.0
        var bpfFreq = 0.0
        var bpfAmount = 0.0
        var vowelFormant = 0.0
        var vowelAmount = 0.0

        var lfo1Shape = 0.0
        var lfo2Shape = 0.0

        val env1 = EnvelopeParams()
    }

    var masterVolume = 1.0
    val layerA = LayerParams()

    fun set(tag: ParamTag, normValue: Double) {
        val param = ParamsList.get(tag) as? RangeParam
        val rangeValue = param?.let { it.minValue + normValue * (it.maxValue - it.minValue) } ?: 0.0
        val balancedValue = normValue * 2 - 1

        when (tag) {
            MASTER_VOLUME -> masterVolume = normValue
            MOD_WHEEL -> Unit
            PITCH_BEND -> Unit
            AFTER_TOUCH -> Unit
            DAMPER_PEDAL -> Unit
            ALL_NOTES_OFF -> Unit
            OUTPUT_LEFT_PEAK -> Unit
            OUTPUT_RIGHT_PEAK -> Unit
            OSC1_SHAPE -> layerA.osc1Shape = normValue
            OSC1_VOLUME -> layerA.osc1Volume = normValue
            OSC1_PITCH -> layerA.osc1Pitch = rangeValue
            OSC1_DETUNE -> layerA.osc1Detune = balancedValue
            OSC2_SHAPE -> layerA.osc2Shape = normValue
            OSC2_VOLUME -> layerA.osc2Volume = normValue
            OSC2_PITCH -> layerA.osc2Pitch = rangeValue
            OSC2_DETUNE -> layerA.osc2Detune = balancedValue
            OSC3_SHAPE -> layerA.osc3Shape = normValue
            OSC3_VOLUME -> layerA.osc3Volume = normValue
            OSC3_DETUNE -> layerA.osc3Detune = balancedValue
            OSC3_FOLD_OSC1 -> layerA.osc3FoldOsc1 = normValue
            NOISE_SHAPE -> layerA.noiseShape = normValue
            NOISE_VOLUME -> layerA.noiseVolume = normValue
            LOW_PASS_FREQ -> layerA.lpfFreq = rangeValue
            HIGH_PASS_FREQ -> layerA.hpfFreq = rangeValue
            BAND_PASS_FREQ -> layerA.bpfFreq = rangeValue
            BAND_PASS_AMOUNT -> layerA.bpfAmount = normValue
            VOWEL_FORMANT -> layerA.vowelFormant = normValue
            VOWEL_AMOUNT -> layerA.vowelAmount = normValue
            LFO1_SHAPE -> layerA.lfo1Shape = normValue
            LFO2_SHAPE -> layerA.lfo2Shape = normValue
            ENV1_ATTACK -> layerA.env1.attackMillis = rangeValue
            ENV1_DECAY -> layerA.env1.decayMillis = rangeValue
            ENV1_SUSTAIN -> layerA.env1.sustainLevel = normValue
            ENV1_RELEASE -> layerA.env1.releaseMillis = rangeValue
        }
    }
}
