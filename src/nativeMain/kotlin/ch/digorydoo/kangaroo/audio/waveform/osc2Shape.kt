package ch.digorydoo.kangaroo.audio.waveform

import ch.digorydoo.kangaroo.math.lerp
import ch.digorydoo.kangaroo.math.spow
import ch.digorydoo.kangaroo.math.unbalance

inline fun osc2Shape(t: Double, freq: Double, shape: Double): Double {
    val tf = t * freq
    return when {
        shape < 1.0 / 5 -> {
            val s = shape / (1.0 / 5)
            val a = nsin(unbalance(tf, -2.0 + s))
            val b = pulse(tf, unbalance = -0.6, upSlope = 0.1 + 0.3 * s, downSlope = 0.1 + 0.3 * s)
            lerp(a, b, s)
        }
        shape < 2.0 / 5 -> {
            val s = (shape - (1.0 / 5)) / (1.0 / 5)
            pulse(tf, unbalance = -0.6 + 0.6 * s, upSlope = 0.4 - 0.3 * s, downSlope = 0.4)
        }
        shape < 3.0 / 5 -> {
            val s = (shape - (2.0 / 5)) / (1.0 / 5)
            val a = pulse(tf, unbalance = 0.3 * s, upSlope = 0.1, downSlope = 0.4)
            val b = spow(upsaw(unbalance(tf, -0.4 + s * 0.4)), 0.5)
            lerp(a, b, s)
        }
        shape < 4.0 / 5 -> {
            val s = (shape - (3.0 / 5)) / (1.0 / 5)
            val u = unbalance(tf, s * 0.4)
            val a = spow(upsaw(u), 0.5)
            val b = stepsin(u, 5.0)
            lerp(a, b, s)
        }
        else -> {
            val s = (shape - (4.0 / 5)) / (1.0 / 5)
            val a = stepsin(unbalance(tf, 0.4 + s * 0.4), 5.0)
            val b = spow(upsaw(unbalance(tf, 0.2 + s * 0.2)), 2.0)
            lerp(a, b, s)
        }
    }
}
