package ch.digorydoo.kangaroo.audio.waveform

import ch.digorydoo.kangaroo.math.lerp

inline fun osc3Shape(t: Double, freq: Double, shape: Double): Double {
    val tf = t * freq
    return when {
        shape < 1.0 / 8 -> {
            val s = shape / (1.0 / 8)
            val a = organPatch1(tf)
            val b = organPatch2(tf)
            lerp(a, b, s)
        }
        shape < 2.0 / 8 -> {
            val s = (shape - (1.0 / 8)) / (1.0 / 8)
            val a = organPatch2(tf)
            val b = organPatch3(tf)
            lerp(a, b, s)
        }
        shape < 3.0 / 8 -> {
            val s = (shape - (2.0 / 8)) / (1.0 / 8)
            val a = organPatch3(tf)
            val b = organPatch4(tf)
            lerp(a, b, s)
        }
        shape < 4.0 / 8 -> {
            val s = (shape - (3.0 / 8)) / (1.0 / 8)
            val a = organPatch4(tf)
            val b = organPatch5(tf)
            lerp(a, b, s)
        }
        shape < 5.0 / 8 -> {
            val s = (shape - (4.0 / 8)) / (1.0 / 8)
            val a = organPatch5(tf)
            val b = organPatch6(tf)
            lerp(a, b, s)
        }
        shape < 6.0 / 8 -> {
            val s = (shape - (5.0 / 8)) / (1.0 / 8)
            val a = organPatch6(tf)
            val b = organPatch7(tf)
            lerp(a, b, s)
        }
        shape < 7.0 / 8 -> {
            val s = (shape - (6.0 / 8)) / (1.0 / 8)
            val a = organPatch7(tf)
            val b = organPatch8(tf)
            lerp(a, b, s)
        }
        else -> {
            val s = (shape - (7.0 / 8)) / (1.0 / 8)
            val a = organPatch8(tf)
            val b = organPatch9(tf)
            lerp(a, b, s)
        }
    }
}
