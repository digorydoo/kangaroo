package ch.digorydoo.kangaroo.math

/**
 * @param x: The value to be clamped
 * @param min: Minimum value
 * @param max: Maximum value
 * @return The value x clamped to the minimum and maximum given
 */
inline fun clamp(x: Double, min: Double, max: Double) = when {
    x <= min -> min
    x >= max -> max
    else -> x
}

/**
 * @param x: The value to be clamped
 * @param min: Minimum value
 * @param max: Maximum value
 * @return The value x clamped to the minimum and maximum given
 */
inline fun clamp(x: Float, min: Float, max: Float) = when {
    x <= min -> min
    x >= max -> max
    else -> x
}
