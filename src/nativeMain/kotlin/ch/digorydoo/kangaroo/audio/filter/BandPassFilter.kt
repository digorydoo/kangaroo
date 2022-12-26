package ch.digorydoo.kangaroo.audio.filter

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class BandPassFilter(sampleRate: Double): AbstrFilter(sampleRate) {
    private var curFreq = 0.0
    private var curQ = 0.0

    /**
     * @param freq: 0..sampleRate/2
     * @param q: 1..+x, higher values make the bandwidth thinner
     */
    fun set(freq: Double, q: Double) {
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

        // band pass with constant 0 dB peak gain
        a0 = 1 + alpha
        a1 = -2 * cw0
        a2 = 1 - alpha
        b0 = alpha
        b1 = 0.0
        b2 = -alpha
    }
}
