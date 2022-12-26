package ch.digorydoo.kangaroo.math

import kotlin.math.floor

/**
 * Forces a value into a period within 0..1, and makes the axis non-linear.
 * @param x: unit angle, 1.0 is once around the circle
 * @param unbalance: 0 is perfectly balanced (linear), 1 is quadratic towards the end; -n..+n
 * @return: 0..1
 */
inline fun unbalance(x: Double, unbalance: Double): Double {
    val m = x - floor(x)
    return when {
        unbalance > 0 -> spow(m, 1 + 2.0 * unbalance)
        unbalance < 0 -> 1.0 - spow(1.0 - m, 1 - 2.0 * unbalance)
        else -> m
    }
}
