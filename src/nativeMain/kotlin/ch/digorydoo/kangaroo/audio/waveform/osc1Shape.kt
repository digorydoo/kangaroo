package ch.digorydoo.kangaroo.audio.waveform

import ch.digorydoo.kangaroo.math.expand
import ch.digorydoo.kangaroo.math.lerp
import ch.digorydoo.kangaroo.math.spow
import ch.digorydoo.kangaroo.math.unbalance
import kotlin.math.pow

inline fun osc1Shape(t: Double, freq: Double, shape: Double): Double {
    val tf = t * freq
    return when {
        shape < 0.5 / 5 -> {
            val s = shape / (0.5 / 5)
            val a = nsin(tf)
            val b = expand(a, 2.0)
            lerp(a, b, s)
        }
        shape < 1.0 / 5 -> {
            val s = ((shape - (0.5 / 5)) / (0.5 / 5)).pow(2.0)
            val a = expand(nsin(tf), 2.0)
            val b = pulse(tf, unbalance = 0.0, upSlope = 0.1, downSlope = 0.1)
            lerp(a, b, s * 0.7)
        }
        shape < 1.5 / 5 -> {
            val s = ((shape - (1.0 / 5)) / (0.5 / 5))
            val b = pulse(tf, unbalance = 0.0, upSlope = 0.1 + 0.9 * s, downSlope = 0.1)
            val a = lerp(expand(nsin(tf), 2.0), b, 0.7)
            lerp(a, b, s)
        }
        shape < 2.0 / 5 -> {
            val s = (shape - (1.5 / 5)) / (0.5 / 5)
            val a = pulse(tf, unbalance = 0.0, upSlope = 1.0, downSlope = 0.1)
            val b = upsaw(tf)
            lerp(a, b, s)
        }
        shape < 3.0 / 5 -> {
            val s = (shape - (2.0 / 5)) / (1.0 / 5)
            saw2Triang(tf, s)
        }
        shape < 4.0 / 5 -> {
            val s = (shape - (3.0 / 5)) / (1.0 / 5)
            val a = triang(tf)
            val b = spow(nsin(tf), 1.0 + 20.0 * shape)
            lerp(a, b, s)
        }
        else -> {
            val s = (shape - (4.0 / 5)) / (1.0 / 5)
            spow(nsin(unbalance(tf, 0.25 * s)), 21.0)
        }
    }
}
