package ch.digorydoo.kangaroo.param

import ch.digorydoo.kangaroo.audio.filter.FormantFilter
import ch.digorydoo.kangaroo.param.Param.RangeParam
import ch.digorydoo.kangaroo.param.ParamTag.*

class ParamsList {
    private val normValues = mutableMapOf<ParamTag, Float>()

    fun forEach(lambda: (param: Param) -> Unit) {
        list.forEach(lambda)
    }

    fun forEach(lambda: (param: Param, value: Float) -> Unit) {
        list.forEach { param -> lambda(param, normValues[param.tag] ?: param.normDefaultValue) }
    }

    fun setValueNormalized(tag: ParamTag, value: Float) {
        normValues[tag] = value
    }

    companion object {
        const val HPF_FREQ_MIN = 10.0f
        const val HPF_FREQ_MAX = 10000.0f
        const val LPF_FREQ_MIN = 10.0f
        const val LPF_FREQ_MAX = 10000.0f

        private val list: List<Param> = ParamTag.values.map { tag ->
            when (tag) {
                MASTER_VOLUME -> RangeParam(
                    "Master Volume",
                    tag,
                    "%",
                    minValue = 0.0f,
                    maxValue = 100.0f,
                    defaultValue = 80.0f,
                )
                MOD_WHEEL -> RangeParam(
                    "Mod Wheel",
                    tag,
                    minValue = 0.0f,
                    maxValue = 127.0f,
                    hidden = true,
                )
                PITCH_BEND -> RangeParam(
                    "Pitch Bend",
                    tag,
                    minValue = 0.0f,
                    maxValue = 127.0f,
                    hidden = true,
                )
                AFTER_TOUCH -> RangeParam(
                    "Aftertouch",
                    tag,
                    minValue = 0.0f,
                    maxValue = 127.0f,
                    hidden = true,
                )
                DAMPER_PEDAL -> RangeParam(
                    "Damper Pedal",
                    tag,
                    minValue = 0.0f,
                    maxValue = 127.0f,
                    hidden = true,
                )
                ALL_NOTES_OFF -> RangeParam(
                    "All Notes Off",
                    tag,
                    minValue = 0.0f,
                    maxValue = 127.0f,
                    hidden = true,
                )
                OUTPUT_LEFT_PEAK -> RangeParam(
                    "Output Left Peak",
                    tag,
                    minValue = 0.0f,
                    maxValue = 1.0f,
                    readOnly = true,
                    hidden = true,
                    precision = 1
                )
                OUTPUT_RIGHT_PEAK -> RangeParam(
                    "Output Right Peak",
                    tag,
                    minValue = 0.0f,
                    maxValue = 1.0f,
                    readOnly = true,
                    hidden = true,
                    precision = 1
                )
                OSC1_SHAPE -> RangeParam(
                    "Osc 1 Shape",
                    tag,
                    minValue = 1.0f,
                    maxValue = 6.0f,
                    defaultValue = 1.0f,
                    precision = 2,
                )
                OSC1_VOLUME -> RangeParam(
                    "Osc 1 Volume",
                    tag,
                    units = "%",
                    minValue = 0.0f,
                    maxValue = 100.0f,
                    defaultValue = 50.0f,
                )
                OSC1_PITCH -> RangeParam(
                    "Osc 1 Pitch",
                    tag,
                    minValue = -24.0f,
                    maxValue = 24.0f,
                    defaultValue = 0.0f,
                )
                OSC1_DETUNE -> RangeParam(
                    "Osc 1 Detune",
                    tag,
                    minValue = -50.0f,
                    maxValue = 50.0f,
                    defaultValue = 0.0f,
                )
                OSC2_SHAPE -> RangeParam(
                    "Osc 2 Shape",
                    tag,
                    minValue = 1.0f,
                    maxValue = 6.0f,
                    defaultValue = 1.0f,
                    precision = 2,
                )
                OSC2_VOLUME -> RangeParam(
                    "Osc 2 Volume",
                    tag,
                    units = "%",
                    minValue = 0.0f,
                    maxValue = 100.0f,
                    defaultValue = 25.0f,
                )
                OSC2_PITCH -> RangeParam(
                    "Osc 2 Pitch",
                    tag,
                    minValue = -24.0f,
                    maxValue = 24.0f,
                    defaultValue = 0.0f,
                )
                OSC2_DETUNE -> RangeParam(
                    "Osc 2 Detune",
                    tag,
                    minValue = -50.0f,
                    maxValue = 50.0f,
                    defaultValue = 0.0f,
                )
                OSC3_SHAPE -> RangeParam(
                    "Osc 3 Shape",
                    tag,
                    minValue = 1.0f,
                    maxValue = 9.0f,
                    defaultValue = 1.0f,
                    precision = 2,
                )
                OSC3_VOLUME -> RangeParam(
                    "Osc 3 Volume",
                    tag,
                    units = "%",
                    minValue = 0.0f,
                    maxValue = 100.0f,
                    defaultValue = 25.0f,
                )
                OSC3_DETUNE -> RangeParam(
                    "Osc 3 Detune",
                    tag,
                    minValue = -50.0f,
                    maxValue = 50.0f,
                    defaultValue = 0.0f,
                )
                OSC3_FOLD_OSC1 -> RangeParam(
                    "Osc 3 Fold Osc 1",
                    tag,
                    minValue = 0.0f,
                    maxValue = 100.0f,
                    defaultValue = 0.0f,
                )
                NOISE_SHAPE -> RangeParam(
                    "Noise Shape",
                    tag,
                    minValue = 0.0f,
                    maxValue = 100.0f,
                    defaultValue = 0.0f,
                )
                NOISE_VOLUME -> RangeParam(
                    "Noise Volume",
                    tag,
                    units = "%",
                    minValue = 0.0f,
                    maxValue = 100.0f,
                    defaultValue = 0.0f,
                )
                LOW_PASS_FREQ -> RangeParam(
                    "Low Pass Freq",
                    tag,
                    units = "Hz",
                    minValue = LPF_FREQ_MIN,
                    maxValue = LPF_FREQ_MAX,
                    defaultValue = LPF_FREQ_MAX,
                )
                HIGH_PASS_FREQ -> RangeParam(
                    "High Pass Freq",
                    tag,
                    units = "Hz",
                    minValue = HPF_FREQ_MIN,
                    maxValue = HPF_FREQ_MAX,
                    defaultValue = HPF_FREQ_MIN,
                )
                BAND_PASS_FREQ -> RangeParam(
                    "Band Pass Freq",
                    tag,
                    units = "Hz",
                    minValue = 200.0f,
                    maxValue = 8000.0f,
                    defaultValue = 1000.0f,
                )
                BAND_PASS_AMOUNT -> RangeParam(
                    "Band Pass Amount",
                    tag,
                    minValue = 0.0f,
                    maxValue = 24.0f,
                    defaultValue = 0.0f,
                    precision = 1,
                )
                VOWEL_FORMANT -> RangeParam(
                    "Vowel Formant",
                    tag,
                    minValue = 1.0f,
                    maxValue = FormantFilter.vowels.size.toFloat(),
                    defaultValue = 1.0f,
                    precision = 1,
                )
                VOWEL_AMOUNT -> RangeParam(
                    "Vowel Amount",
                    tag,
                    units = "%",
                    minValue = 0.0f,
                    maxValue = 100.0f,
                    defaultValue = 0.0f,
                )
                LFO1_SHAPE -> RangeParam(
                    "LFO 1 Shape",
                    tag,
                    minValue = 1.0f,
                    maxValue = 4.0f,
                    defaultValue = 1.0f,
                )
                LFO2_SHAPE -> RangeParam(
                    "LFO 2 Shape",
                    tag,
                    minValue = 1.0f,
                    maxValue = 4.0f,
                    defaultValue = 1.0f,
                )
                ENV1_ATTACK -> RangeParam(
                    "AEnv Attack Time",
                    tag,
                    units = "ms",
                    minValue = 0.5f,
                    maxValue = 10000.0f,
                    defaultValue = 10.0f,
                )
                ENV1_DECAY -> RangeParam(
                    "AEnv Decay Time",
                    tag,
                    units = "ms",
                    minValue = 0.5f,
                    maxValue = 10000.0f,
                    defaultValue = 42.0f,
                )
                ENV1_SUSTAIN -> RangeParam(
                    "AEnv Sustain Level",
                    tag,
                    units = "%",
                    minValue = 0.0f,
                    maxValue = 100.0f,
                    defaultValue = 80.0f,
                )
                ENV1_RELEASE -> RangeParam(
                    "AEnv Release Time",
                    tag,
                    units = "ms",
                    minValue = 10.0f,
                    maxValue = 20000.0f,
                    defaultValue = 300.0f,
                )
            }
        }

        private val map = mutableMapOf<ParamTag, Param>().also { map ->
            list.forEach { param -> map[param.tag] = param }
        }

        fun get(tag: ParamTag): Param? =
            map[tag]

        // StringListParam("Filter Type", FILTER_TYPE, list = listOf("LowPass", "Highpass", "Bandpass")),
        // GenericParam("Filter Q", FILTER_Q, null, precision = 2),
        // GenericParam("Bypass SNA", BYPASS_SNA, null, stepCount = 1),
    }
}
