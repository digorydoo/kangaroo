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
import ch.digorydoo.kangaroo.param.AudioParams
import ch.digorydoo.kangaroo.param.ParamsList.Companion.HPF_FREQ_MAX
import ch.digorydoo.kangaroo.param.ParamsList.Companion.HPF_FREQ_MIN
import ch.digorydoo.kangaroo.param.ParamsList.Companion.LPF_FREQ_MAX
import ch.digorydoo.kangaroo.param.ParamsList.Companion.LPF_FREQ_MIN
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

class Voice(private val params: AudioParams) {
    var key = 0; private set // key that was pressed; does not change while same key is playing
    val isPlaying get() = !env1.finished

    private var noteOnDetune = 0.0
    private var noteOnVolume = 0.0

    private var sampleRate = 44100.0
    private var env1 = Envelope(sampleRate)
    private var noiseFilter = BandPassFilter(sampleRate)
    private var hpf = HighPassFilter(sampleRate)
    private var bpf = BandPassFilter(sampleRate)
    private var lpf = LowPassFilter(sampleRate)
    private var formantFilter = FormantFilter(sampleRate)

    fun setSampleRate(rate: Double) {
        sampleRate = rate
        env1 = Envelope(sampleRate)
        noiseFilter = BandPassFilter(sampleRate)
        hpf = HighPassFilter(sampleRate)
        bpf = BandPassFilter(sampleRate)
        lpf = LowPassFilter(sampleRate)
        formantFilter = FormantFilter(sampleRate)
    }

    fun onNoteOn(newKey: Int, detune: Float, velocity: Float) {
        key = newKey
        noteOnDetune = detune.toDouble()
        noteOnVolume = 0.8 * velocity.toDouble()
        env1.onNoteOn(params.layerA.env1)
    }

    fun onNoteOff() {
        env1.onNoteOff()
    }

    fun forceStop() {
        env1.forceStop()
    }

    fun addNextSample(t: Double, s: StereoSample) {
        if (!isPlaying) {
            return
        }

        val layer = params.layerA
        val osc1Params = layer.osc1
        val osc2Params = layer.osc2
        val osc3Params = layer.osc3

        val o1 = if (osc1Params.volume <= 0.0 && (osc3Params.foldOsc1 <= 0.0 || osc3Params.volume <= 0.0)) {
            0.0
        } else {
            osc1Shape(
                t,
                keyFreq(key + noteOnDetune + osc1Params.pitch + 0.5 * osc1Params.detune),
                osc1Params.shape
            )
        }

        val o2 = if (osc2Params.volume <= 0.0) {
            0.0
        } else {
            osc2Shape(
                t,
                keyFreq(key + noteOnDetune + osc2Params.pitch + 0.5 * osc2Params.detune),
                osc2Params.shape
            )
        }

        val o3 = if (osc3Params.volume <= 0.0) {
            0.0
        } else {
            val o3Raw = osc3Shape(
                t,
                keyFreq(key + noteOnDetune + osc1Params.pitch - 12 + 0.5 * osc3Params.detune),
                osc3Params.shape
            )
            lerp(o3Raw, o1 * o3Raw, osc3Params.foldOsc1)
        }

        val osc1 = osc1Params.volume * o1
        val osc2 = osc2Params.volume * o2
        val osc3 = osc3Params.volume * o3

        val noise = if (layer.noise.volume <= 0) {
            0.0
        } else {
            noiseFilter.set(lerp(200.0, 6000.0, layer.noise.shape), 2.0)
            val n0 = noise(t * keyFreq(A4_KEY + key / 32))
            val n1 = noiseFilter.evaluate(n0)
            layer.noise.volume * n1
        }

        val oscSum = osc1 + osc2 + osc3 + noise
        var signal = oscSum

        if (layer.filter.lpfFreq < LPF_FREQ_MAX) {
            // Fade out LPF amount as the frequency reaches the maximum.
            val lpfFreqRel = (layer.filter.lpfFreq - LPF_FREQ_MIN) / (LPF_FREQ_MAX - LPF_FREQ_MIN)
            val lpfAmount = 1.0 - max(0.0, lpfFreqRel - 0.9) / 0.1
            lpf.set(layer.filter.lpfFreq, 1.0)
            signal = lerp(signal, lpf.evaluate(signal), lpfAmount)
        }

        if (layer.filter.hpfFreq > HPF_FREQ_MIN) {
            // Fade out HPF amount as the frequency reaches the minimum.
            val hpfFreqRel = (layer.filter.hpfFreq - HPF_FREQ_MIN) / (HPF_FREQ_MAX - HPF_FREQ_MIN)
            val hpfAmount = min(0.1, hpfFreqRel) / 0.1
            hpf.set(layer.filter.hpfFreq, 1.0)
            signal = lerp(signal, hpf.evaluate(signal), hpfAmount)
        }

        if (layer.filter.bpfAmount > 0.0) {
            // The BPF takes the unmodified oscSum, and fades from adding the result to the solo result.
            val bpfQ = lerp(1.0, 15.0, layer.filter.bpfAmount)
            bpf.set(layer.filter.bpfFreq, bpfQ)
            val bpfValue = 10.0 * layer.filter.bpfAmount * bpf.evaluate(oscSum)
            signal = lerp(signal + bpfValue, bpfValue, layer.filter.bpfAmount)
        }

        if (layer.filter.vowelAmount > 0.0) {
            val formant = FormantFilter.continuous(layer.filter.vowelFormant)
            formantFilter.set(formant, q = 8.0)
            signal = lerp(signal, formantFilter.evaluate(signal), layer.filter.vowelAmount)
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
