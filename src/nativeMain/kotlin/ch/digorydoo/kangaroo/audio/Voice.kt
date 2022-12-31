package ch.digorydoo.kangaroo.audio

import ch.digorydoo.kangaroo.audio.AudioProcessor.StereoSample
import ch.digorydoo.kangaroo.param.AudioParams

class Voice(private val params: AudioParams) {
    private var sampleRate = 44100.0
    private var layerA = VoiceLayer(sampleRate, params.layerA)

    var key = 0; private set // key that was pressed; does not change while same key is playing
    val isPlaying get() = layerA.isPlaying

    fun setSampleRate(rate: Double) {
        sampleRate = rate
        layerA = VoiceLayer(rate, params.layerA)
    }

    fun onNoteOn(newKey: Int, detune: Float, velocity: Float) {
        key = newKey
        layerA.onNoteOn(newKey, detune, velocity)
    }

    fun onNoteOff() {
        layerA.onNoteOff()
    }

    fun forceStop() {
        layerA.forceStop()
    }

    fun addNextSample(t: Double, s: StereoSample) {
        if (!isPlaying) {
            return
        }

        layerA.addNextSample(t, s)
    }
}
