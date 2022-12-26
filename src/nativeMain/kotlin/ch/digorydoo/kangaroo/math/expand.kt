package ch.digorydoo.kangaroo.math

import kotlin.math.abs
import kotlin.math.pow

/**
 * Pushes a value in -1..+1 outwards towards the boundaries -1..+1.
 * @param x: -1..+1
 * @param c: 0=linear, 1=quadratic compression
 * @return [-1..+1]
 */
inline fun expand(x: Double, c: Double) =
    sign(x) * (1.0 - (1.0 - abs(x)).pow(c + 1.0))
