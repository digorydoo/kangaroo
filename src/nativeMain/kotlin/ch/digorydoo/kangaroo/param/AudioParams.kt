package ch.digorydoo.kangaroo.param

import ch.digorydoo.kangaroo.param.Param.RangeParam
import ch.digorydoo.kangaroo.param.ParamTag.*

class AudioParams {
    class Osc1Params {
        var shape = 0.0
        var volume = 0.0
        var pitch = 0.0
        var detune = 0.0
    }

    class Osc2Params {
        var shape = 0.0
        var volume = 0.0
        var pitch = 0.0
        var detune = 0.0
    }

    class Osc3Params {
        var shape = 0.0
        var volume = 0.0
        var detune = 0.0
        var foldOsc1 = 0.0
    }

    class NoiseParams {
        var shape = 0.0
        var volume = 0.0
    }

    class FilterParams {
        var lpfFreq = 0.0
        var hpfFreq = 0.0
        var bpfFreq = 0.0
        var bpfAmount = 0.0
        var vowelFormant = 0.0
        var vowelAmount = 0.0
    }

    class LFO1Params {
        var shape = 0.0
    }

    class LFO2Params {
        var shape = 0.0
    }

    class EnvelopeParams {
        var attackMillis = 0.0
        var decayMillis = 0.0
        var sustainLevel = 0.0
        var releaseMillis = 0.0
    }

    class LayerParams {
        val osc1 = Osc1Params()
        val osc2 = Osc2Params()
        val osc3 = Osc3Params()
        val noise = NoiseParams()
        val filter = FilterParams()
        val lfo1 = LFO1Params()
        val lfo2 = LFO2Params()
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
            OSC1_SHAPE -> layerA.osc1.shape = normValue
            OSC1_VOLUME -> layerA.osc1.volume = normValue
            OSC1_PITCH -> layerA.osc1.pitch = rangeValue
            OSC1_DETUNE -> layerA.osc1.detune = balancedValue
            OSC2_SHAPE -> layerA.osc2.shape = normValue
            OSC2_VOLUME -> layerA.osc2.volume = normValue
            OSC2_PITCH -> layerA.osc2.pitch = rangeValue
            OSC2_DETUNE -> layerA.osc2.detune = balancedValue
            OSC3_SHAPE -> layerA.osc3.shape = normValue
            OSC3_VOLUME -> layerA.osc3.volume = normValue
            OSC3_DETUNE -> layerA.osc3.detune = balancedValue
            OSC3_FOLD_OSC1 -> layerA.osc3.foldOsc1 = normValue
            NOISE_SHAPE -> layerA.noise.shape = normValue
            NOISE_VOLUME -> layerA.noise.volume = normValue
            LOW_PASS_FREQ -> layerA.filter.lpfFreq = rangeValue
            HIGH_PASS_FREQ -> layerA.filter.hpfFreq = rangeValue
            BAND_PASS_FREQ -> layerA.filter.bpfFreq = rangeValue
            BAND_PASS_AMOUNT -> layerA.filter.bpfAmount = normValue
            VOWEL_FORMANT -> layerA.filter.vowelFormant = normValue
            VOWEL_AMOUNT -> layerA.filter.vowelAmount = normValue
            LFO1_SHAPE -> layerA.lfo1.shape = normValue
            LFO2_SHAPE -> layerA.lfo2.shape = normValue
            ENV1_ATTACK -> layerA.env1.attackMillis = rangeValue
            ENV1_DECAY -> layerA.env1.decayMillis = rangeValue
            ENV1_SUSTAIN -> layerA.env1.sustainLevel = normValue
            ENV1_RELEASE -> layerA.env1.releaseMillis = rangeValue
        }
    }
}
