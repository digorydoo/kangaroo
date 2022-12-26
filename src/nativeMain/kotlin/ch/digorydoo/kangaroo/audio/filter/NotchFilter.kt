package ch.digorydoo.kangaroo.audio.filter

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Suppress("unused")
class NotchFilter(sampleRate: Double): AbstrFilter(sampleRate) {
    private var curFreq = 0.0
    private var curQ = 0.0

    /**
     * @param freq: 0..sampleRate/2
     * @param q: 1..+x, higher values make the bandwidth thinner
     */
    fun setNotch(freq: Double, q: Double) {
        if (freq == curFreq && q == curQ) {
            return // no change
        }

        curFreq = freq
        curQ = q

        val f0 = min(freq, sampleRate / 2.0)
        val w0 = 2.0 * PI * f0 / sampleRate
        val sw0 = sin(w0)
        val cw0 = cos(w0)
        val alpha = sw0 / (2 * q)

        a0 = 1 + alpha
        a1 = -2 * cw0
        a2 = 1 - alpha
        b0 = 1.0
        b1 = -2 * cw0
        b2 = 1.0
    }
}
