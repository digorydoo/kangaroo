package ch.digorydoo.kangaroo.audio.filter

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

class HighPassFilter(sampleRate: Double): AbstrFilter(sampleRate) {
    private var curFreq = 0.0
    private var curSlope = 0.0

    /**
     * @param freq: 0..sampleRate/2
     * @param slope: 0..1, 0=error, 1=maximum steepness
     */
    fun set(freq: Double, slope: Double) {
        if (freq == curFreq && slope == curSlope) {
            return // no change
        }

        curFreq = freq
        curSlope = slope

        val f0 = min(freq, sampleRate / 2.0)
        val w0 = 2.0 * PI * f0 / sampleRate
        val sw0 = sin(w0)
        val cw0 = cos(w0)
        val a = 1.0 // 10.0.pow(gain / 40.0)
        val alpha = sw0 / 2.0 * sqrt((a + 1 / a) * (1 / slope - 1) + 2)

        a0 = 1 + alpha
        a1 = -2 * cw0
        a2 = 1 - alpha
        b0 = (1 + cw0) / 2
        b1 = -(1 + cw0)
        b2 = (1 + cw0) / 2
    }
}
