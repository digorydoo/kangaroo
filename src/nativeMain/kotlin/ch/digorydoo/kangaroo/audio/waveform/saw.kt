package ch.digorydoo.kangaroo.audio.waveform

import kotlin.math.floor
import kotlin.math.truncate

/**
 * Upwards saw function.
 * @param x: unit angle, 1.0 is once around the circle.
 * @return: -1..+1
 */
inline fun upsaw(x: Double): Double =
    if (x < 0.0) {
        -(2.0 * ((-x + 0.5) - truncate(-x + 0.5)) - 1.0)
    } else {
        2.0 * ((x + 0.5) - truncate(x + 0.5)) - 1.0
    }

/**
 * Function that transforms from an upwards saw to a triangle.
 * @param x: unit angle, 1.0 is once around the circle.
 * @param shape: 0 is an upwards saw, 1 is a triangle
 * @return: -1..+1
 */
inline fun saw2Triang(x: Double, shape: Double): Double {
    val s = shape * 0.25
    val m = x - floor(x)
    return when {
        m < 0.5 - s -> m / (0.5 - s)
        s > 0 && m < 0.5 + s -> 1 - 2 * (m - (0.5 - s)) / (2 * s)
        else -> -1 + (m - (0.5 + s)) / (0.5 - s)
    }
}
