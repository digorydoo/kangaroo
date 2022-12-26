package ch.digorydoo.kangaroo.audio

import ch.digorydoo.kangaroo.audio.AudioProcessor.StereoSample
import ch.digorydoo.kangaroo.param.AudioParams

@Suppress("ReplaceManualRangeWithIndicesCalls")
class VoiceCollection(private val params: AudioParams) {
    private val voices = Array(NUM_VOICES) { Voice(params) }
    private var voiceIdx = 0
    private var sampleRate = 44100.0
    private var sampleIdx: Double = 0.0

    fun setSampleRate(rate: Double) {
        sampleRate = rate
        sampleIdx = 0.0

        // We're not using forEach() or iterator syntax in order to avoid creating objects.

        for (i in 0 until voices.size) {
            voices[i].setSampleRate(sampleRate)
        }
    }

    fun anyPlaying(): Boolean {
        // We're not using any() or iterator syntax in order to avoid creating objects.

        for (i in 0 until voices.size) {
            if (voices[i].isPlaying) {
                return true
            }
        }

        return false
    }

    fun onNoteOn(key: Int, detune: Float, velocity: Float) {
        val voice = findFreeVoice()
        voice.onNoteOn(key, detune, velocity)
    }

    fun onNoteOff(key: Int) {
        for (i in 0 until voices.size) {
            val voice = voices[i]

            if (voice.isPlaying && voice.key == key) {
                voice.onNoteOff()
            }
        }
    }

    private fun allNotesOff() {
        for (i in 0 until voices.size) {
            voices[i].onNoteOff()
        }
    }

    private fun allSoundsOff() {
        for (i in 0 until voices.size) {
            voices[i].forceStop()
        }
    }

    fun onCCOutEvent(ccNumber: CCNumber) {
        // We come here when the UI posts a CC event via cppSendCCOutEvent.
        // Normal MIDI CC events will be processed by AudioProcessor directly as a param change event.
        when (ccNumber) {
            CCNumber.AllNotesOff -> allNotesOff()
            CCNumber.AllSoundsOff -> allSoundsOff()
            else -> Unit
        }
    }

    fun computeNextSample(s: StereoSample) {
        val t = sampleIdx / sampleRate
        sampleIdx++
        s.left = 0.0
        s.right = 0.0

        // We're not using forEach() or iterator syntax in order to avoid creating objects.

        for (i in 0 until voices.size) {
            val voice = voices[i]

            if (voice.isPlaying) {
                voice.addNextSample(t, s)
            }
        }

        s.left *= params.masterVolume
        s.right *= params.masterVolume
    }

    private fun findFreeVoice(): Voice {
        voiceIdx = (voiceIdx + 1) % voices.size

        for (i in 0 until voices.size) {
            val idx = (i + voiceIdx) % voices.size

            if (!voices[idx].isPlaying) {
                return voices[idx]
            }
        }

        // If we come here, we ran out of voices.
        return voices[voiceIdx]
    }

    companion object {
        private const val NUM_VOICES = 32
    }
}
