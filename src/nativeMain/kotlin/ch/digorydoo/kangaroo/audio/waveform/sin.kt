package ch.digorydoo.kangaroo.audio.waveform

import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.sin

/**
 * Normalised sine function.
 * @param x: unit angle, 1.0 is once around the circle.
 * @return: -1..+1
 */
inline fun nsin(x: Double) =
    sin(x * 2.0 * PI)

/**
 * Bit-crushed sine function.
 * @param x: unit angle, 1.0 is once around the circle.
 * @param steps: 1..n
 * @return: -1..+1
 */
inline fun stepsin(x: Double, steps: Double): Double {
    val m = x - floor(x)
    return nsin(floor(m * steps) / steps)
}
