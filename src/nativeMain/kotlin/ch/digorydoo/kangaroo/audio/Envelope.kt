package ch.digorydoo.kangaroo.audio

import ch.digorydoo.kangaroo.math.lerp
import ch.digorydoo.kangaroo.param.AudioParams.EnvelopeParams

class Envelope(private val sampleRate: Double) {
    private enum class Stage { ATTACK, DECAY, SUSTAIN, RELEASE, FINISHED }

    private var attackMillis = 0.0
    private var decayMillis = 0.0
    private var sustainLevel = 0.0
    private var releaseMillis = 0.0

    private var stage = Stage.FINISHED
    private var samplesOfStage = 0.0
    private var sampleCounter = 0.0
    private var releaseStartLevel = 0.0

    val finished get() = stage == Stage.FINISHED

    fun onNoteOn(params: EnvelopeParams) {
        attackMillis = params.attackMillis
        decayMillis = params.decayMillis
        sustainLevel = params.sustainLevel
        releaseMillis = params.releaseMillis
        stage = Stage.ATTACK
        samplesOfStage = millisToSamples(attackMillis)
        sampleCounter = 0.0
    }

    fun onNoteOff() {
        val startLevel = when (stage) {
            Stage.ATTACK, Stage.DECAY -> evaluate()
            Stage.SUSTAIN -> sustainLevel
            else -> return
        }

        stage = Stage.RELEASE
        samplesOfStage = millisToSamples(releaseMillis)
        sampleCounter = 0.0
        releaseStartLevel = startLevel
    }

    fun forceStop() {
        stage = Stage.FINISHED
    }

    fun evaluateNext(): Double {
        when (stage) {
            Stage.ATTACK -> {
                if (sampleCounter >= samplesOfStage) {
                    stage = Stage.DECAY
                    samplesOfStage = millisToSamples(decayMillis)
                    sampleCounter = 0.0
                }
            }
            Stage.DECAY -> {
                if (sampleCounter >= samplesOfStage) {
                    stage = Stage.SUSTAIN
                }
            }
            Stage.SUSTAIN -> Unit
            Stage.RELEASE -> {
                if (sampleCounter >= samplesOfStage) {
                    stage = Stage.FINISHED
                }
            }
            Stage.FINISHED -> Unit
        }

        val result = evaluate()
        sampleCounter++
        return result
    }

    private fun evaluate() =
        when (stage) {
            Stage.ATTACK -> sampleCounter / samplesOfStage
            Stage.DECAY -> lerp(1.0, sustainLevel, sampleCounter / samplesOfStage)
            Stage.SUSTAIN -> sustainLevel
            Stage.RELEASE -> releaseStartLevel * (1.0 - sampleCounter / samplesOfStage)
            Stage.FINISHED -> 0.0
        }

    private fun millisToSamples(ms: Double) =
        ms / 1000.0 * sampleRate
}
