package ch.digorydoo.kangaroo.audio.waveform

inline fun lfoShape(t: Double, freq: Double, shape: Double): Double {
    val tf = t * freq
    return when {
        shape <= 1.0 / 4 -> nsin(tf)
        shape <= 2.0 / 4 -> pulse(tf, unbalance = 0.0, upSlope = 0.0, downSlope = 0.0)
        shape <= 3.0 / 4 -> upsaw(tf)
        else -> triang(tf)
    }
}
