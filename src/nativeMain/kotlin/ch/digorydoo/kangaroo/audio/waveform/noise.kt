package ch.digorydoo.kangaroo.audio.waveform

import ch.digorydoo.kangaroo.math.clamp
import ch.digorydoo.kangaroo.math.sign
import kotlin.math.floor
import kotlin.math.sin

/**
 * @return: [-1..+1]
 */
inline fun noise(x: Double): Double {
    var a = 9943758.51 * sin(3.01745 * floor(x * 4.0) + 0.04)
    a = 2.0 * (a - floor(a) - 0.5)
    val b = 43758.5453 * sin(6.28297 * a)
    return (b - floor(b)) * sign(b)
}

/**
 * @param colour: [0..1]
 * @return: [-1..+1]
 */
inline fun noise2(t: Double, colour: Double = 0.5): Double {
    val n1 = noise(t)
    val n2 = noise(t + 0.01 + 0.5 * colour)
    return clamp(n1 + n2, -1.0, 1.0)
}

/**
 * @param amount: 0 >.. 1.0f
 * @return: [-1..+1]
 */
inline fun crackle(t: Double, amount: Double = 0.2) =
    if (noise(t) > 1.0 - amount) pulse(t * 0.5, 0.9, 0.05, 0.05) else 0.0
