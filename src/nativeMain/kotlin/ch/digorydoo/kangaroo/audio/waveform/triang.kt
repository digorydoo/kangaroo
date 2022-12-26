package ch.digorydoo.kangaroo.audio.waveform

import kotlin.math.floor

/**
 * @param x: unit angle, 1.0 is once around the circle.
 * @return: [-1..+1]
 */
inline fun triang(x: Double): Double {
    val m = x - floor(x)
    return when {
        m < 0.25 -> 4.0 * m
        m < 0.50 -> 2.0 - 4.0 * m
        m < 0.75 -> 2.0 - 4.0 * m
        else -> 4.0 * m - 4.0
    }
}
