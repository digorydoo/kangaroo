package ch.digorydoo.kangaroo.audio

import ch.digorydoo.kangaroo.audio.AudioProcessor.StereoSample
import ch.digorydoo.kangaroo.audio.filter.BandPassFilter
import ch.digorydoo.kangaroo.audio.filter.FormantFilter
import ch.digorydoo.kangaroo.audio.filter.HighPassFilter
import ch.digorydoo.kangaroo.audio.filter.LowPassFilter
import ch.digorydoo.kangaroo.audio.waveform.noise
import ch.digorydoo.kangaroo.audio.waveform.osc1Shape
import ch.digorydoo.kangaroo.audio.waveform.osc2Shape
import ch.digorydoo.kangaroo.audio.waveform.osc3Shape
import ch.digorydoo.kangaroo.math.lerp
import ch.digorydoo.kangaroo.param.AudioParams.LayerParams
import ch.digorydoo.kangaroo.param.ParamsList
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class VoiceLayer(sampleRate: Double, private val params: LayerParams) {
    private val env1 = Envelope(sampleRate)
    private val noiseFilter = BandPassFilter(sampleRate)
    private val hpf = HighPassFilter(sampleRate)
    private val bpf = BandPassFilter(sampleRate)
    private val lpf = LowPassFilter(sampleRate)
    private val formantFilter = FormantFilter(sampleRate)

    private var key = 0
    private var noteOnDetune = 0.0
    private var noteOnVolume = 0.0

    val isPlaying get() = !env1.finished

    fun onNoteOn(newKey: Int, detune: Float, velocity: Float) {
        key = newKey
        noteOnDetune = detune.toDouble()
        noteOnVolume = velocity.toDouble()
        env1.onNoteOn(params.env1)
    }

    fun onNoteOff() {
        env1.onNoteOff()
    }

    fun forceStop() {
        env1.forceStop()
    }

    fun addNextSample(t: Double, s: StereoSample) {
        val o1 = if (params.osc1Volume <= 0.0 && (params.osc3FoldOsc1 <= 0.0 || params.osc3Volume <= 0.0)) {
            0.0
        } else {
            osc1Shape(
                t,
                keyFreq(key + noteOnDetune + params.osc1Pitch + 0.5 * params.osc1Detune),
                params.osc1Shape
            )
        }

        val o2 = if (params.osc2Volume <= 0.0) {
            0.0
        } else {
            osc2Shape(
                t,
                keyFreq(key + noteOnDetune + params.osc2Pitch + 0.5 * params.osc2Detune),
                params.osc2Shape
            )
        }

        val o3 = if (params.osc3Volume <= 0.0) {
            0.0
        } else {
            val o3Raw = osc3Shape(
                t,
                keyFreq(key + noteOnDetune + params.osc1Pitch - 12 + 0.5 * params.osc3Detune),
                params.osc3Shape
            )
            lerp(o3Raw, o1 * o3Raw, params.osc3FoldOsc1)
        }

        val osc1 = params.osc1Volume * o1
        val osc2 = params.osc2Volume * o2
        val osc3 = params.osc3Volume * o3

        val noise = if (params.noiseVolume <= 0) {
            0.0
        } else {
            noiseFilter.set(lerp(200.0, 6000.0, params.noiseShape), 2.0)
            val n0 = noise(t * keyFreq(A4_KEY + key / 32))
            val n1 = noiseFilter.evaluate(n0)
            params.noiseVolume * n1
        }

        val oscSum = osc1 + osc2 + osc3 + noise
        var signal = oscSum

        if (params.lpfFreq < ParamsList.LPF_FREQ_MAX) {
            // Fade out LPF amount as the frequency reaches the maximum.
            val lpfFreqRel = (params.lpfFreq - ParamsList.LPF_FREQ_MIN) /
                (ParamsList.LPF_FREQ_MAX - ParamsList.LPF_FREQ_MIN)
            val lpfAmount = 1.0 - max(0.0, lpfFreqRel - 0.9) / 0.1
            lpf.set(params.lpfFreq, 1.0)
            signal = lerp(signal, lpf.evaluate(signal), lpfAmount)
        }

        if (params.hpfFreq > ParamsList.HPF_FREQ_MIN) {
            // Fade out HPF amount as the frequency reaches the minimum.
            val hpfFreqRel = (params.hpfFreq - ParamsList.HPF_FREQ_MIN) /
                (ParamsList.HPF_FREQ_MAX - ParamsList.HPF_FREQ_MIN)
            val hpfAmount = min(0.1, hpfFreqRel) / 0.1
            hpf.set(params.hpfFreq, 1.0)
            signal = lerp(signal, hpf.evaluate(signal), hpfAmount)
        }

        if (params.bpfAmount > 0.0) {
            // The BPF takes the unmodified oscSum, and fades from adding the result to the solo result.
            val bpfQ = lerp(1.0, 15.0, params.bpfAmount)
            bpf.set(params.bpfFreq, bpfQ)
            val bpfValue = 10.0 * params.bpfAmount * bpf.evaluate(oscSum)
            signal = lerp(signal + bpfValue, bpfValue, params.bpfAmount)
        }

        if (params.vowelAmount > 0.0) {
            val formant = FormantFilter.continuous(params.vowelFormant)
            formantFilter.set(formant, q = 8.0)
            signal = lerp(signal, formantFilter.evaluate(signal), params.vowelAmount)
        }

        signal *= noteOnVolume * env1.evaluateNext()
        s.left += signal
        s.right += signal
    }

    companion object {
        private const val A4_KEY = 69 // MIDI index of note A4; index 0 is C-1
        private val LOW_C_FREQ = 440.0 * 2.0.pow(-A4_KEY / 12.0) // C-1, the lowest MIDI note

        /**
         * @param key: MIDI index of key; 0 is C-1; 69 is A4_KEY
         * @return: frequency in Hz for equal temperament with A4 = 440 Hz
         */
        private fun keyFreq(key: Double) =
            LOW_C_FREQ * 2.0.pow(key / 12.0)

        /**
         * @param key: MIDI index of key; 0 is C-1; 69 is A4_KEY
         * @return: frequency in Hz for equal temperament with A4 = 440 Hz
         */
        private fun keyFreq(key: Int) =
            LOW_C_FREQ * 2.0.pow(key / 12.0)
    }
}
