package ch.digorydoo.kangaroo.audio.waveform

import ch.digorydoo.kangaroo.math.lerp
import kotlin.math.floor

/**
 * Pulse function that can transform to a triangle (when upSlope and downSlope are 1).
 * @param x: unit angle, 1.0 is once around the circle.
 * @param unbalance: affects pulse width; 0 is a square; -1..+1
 * @param upSlope: makes the upwards vertical jump less steep; 0 is the steepest; 0..1
 * @param downSlope: makes the downwards vertical jump less steep; 0 is the steepest; 0..1
 * @return: -1..+1
 */
inline fun pulse(x: Double, unbalance: Double, upSlope: Double, downSlope: Double): Double {
    val m = 2.0 * (x - floor(x)) - 1.0 // -1..+1
    val us = upSlope / 2
    val ds = downSlope / 2
    return when {
        us > 0 && m < -1.0 + us -> lerp(0.0, 1.0, 1.0 - (us - 1.0 - m) / us)
        m < unbalance - ds -> 1.0
        ds > 0 && m < unbalance + ds -> lerp(1.0, -1.0, 1.0 - (unbalance + ds - m) / (ds * 2.0))
        m < 1.0 - us -> -1.0
        us > 0 -> lerp(-1.0, 0.0, -(1.0 - us - m) / us)
        else -> 0.0
    }
}
