package ch.digorydoo.kangaroo.audio

import ch.digorydoo.kangaroo.math.lerp
import ch.digorydoo.kangaroo.param.ParamTag
import kotlin.math.abs
import kotlin.math.max

class OutputPeakUpdater(private val processor: AudioProcessor) {
    private var samplesUntilUpdatePeaks = 0
    private var sampleCount = 0
    private var leftPeakValue = 0.0
    private var rightPeakValue = 0.0
    private var prevLeftPeakValue = 0.0
    private var prevRightPeakValue = 0.0

    fun setSampleRate(rate: Double) {
        leftPeakValue = 0.0
        rightPeakValue = 0.0
        samplesUntilUpdatePeaks = (rate * MILLISECONDS_UNTIL_UPDATE_PEAKS / 1000).toInt()
    }

    fun update(sample: AudioProcessor.StereoSample) {
        leftPeakValue = max(abs(sample.left), leftPeakValue)
        rightPeakValue = max(abs(sample.right), rightPeakValue)

        if (++sampleCount >= samplesUntilUpdatePeaks) {
            sampleCount = 0

            // Make sure to always change both params, because the UI updates itself only for one of them!

            if (leftPeakValue != prevLeftPeakValue || rightPeakValue != prevRightPeakValue) {
                val left = lerp(prevLeftPeakValue, leftPeakValue, 0.9) // add a bit of inertia
                val right = lerp(prevRightPeakValue, rightPeakValue, 0.9)
                processor.setOutputParamChange(ParamTag.OUTPUT_LEFT_PEAK, left.toFloat())
                processor.setOutputParamChange(ParamTag.OUTPUT_RIGHT_PEAK, right.toFloat())
                prevLeftPeakValue = left
                prevRightPeakValue = right
            }

            leftPeakValue = 0.0
            rightPeakValue = 0.0
        }
    }

    companion object {
        private const val MILLISECONDS_UNTIL_UPDATE_PEAKS = 100
    }
}
