package ch.digorydoo.kangaroo.math

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sin

/**
 * S curve, starts with accelerating, ends with decelerating.
 * @param x: must be in [0..1]
 * @param k: must be > 0; 0 is linear, 1 is sine, >1 pushes sine further to boundaries
 * @return A value in [0..1]
 */
inline fun scurve(x: Double, k: Double): Double {
    var y = (1.0 + sin((x - 0.5) * PI)) / 2.0 // y is [0..1]

    if (k <= 1.0) {
        val q = 1.0 - k
        return k * y + q * x
    }

    y -= 0.5
    val s = sign(y)
    y = abs(y) * 2 // y is now [0..1]
    y = s * (1 - (1.0 - y).pow(k)) // y is now [-1..+1]
    return (1.0 + y) / 2.0
}
