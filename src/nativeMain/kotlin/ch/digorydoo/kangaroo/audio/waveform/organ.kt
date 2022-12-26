package ch.digorydoo.kangaroo.audio.waveform

import ch.digorydoo.kangaroo.math.lerp
import kotlin.math.floor

private const val DRAWBAR1 = 16.0 / 16
private const val DRAWBAR2 = 16.0 / (5 + 1 / 3.0)
private const val DRAWBAR3 = 16.0 / 8
private const val DRAWBAR4 = 16.0 / 4
private const val DRAWBAR5 = 16.0 / (2 + 2 / 3.0)
private const val DRAWBAR6 = 16.0 / 2
private const val DRAWBAR7 = 16.0 / (1 + 3 / 5.0)
private const val DRAWBAR8 = 16.0 / (1 + 1 / 3.0)
private const val DRAWBAR9 = 16.0 / 1

fun organPatch1(x: Double) =
    organBase(x * DRAWBAR1) * 1.02

fun organPatch2(x: Double) =
    (organBase(x * DRAWBAR1) + organBase(x * DRAWBAR3)) / 2 * 1.33 + 0.16

fun organPatch3(x: Double) =
    (organBase(x * DRAWBAR1) +
        organBase(x * DRAWBAR2) +
        organBase(x * DRAWBAR3)) / 3 * 1.37 + 0.13

fun organPatch4(x: Double) =
    (organBase(x * DRAWBAR1) +
        organBase(x * DRAWBAR3) +
        organBase(x * DRAWBAR4)) / 3 * 1.62 + 0.19

fun organPatch5(x: Double) =
    (organBase(x * DRAWBAR1) +
        organBase(x * DRAWBAR3) +
        organBase(x * DRAWBAR4) +
        organBase(x * DRAWBAR5)) / 4 * 1.7 + 0.21

fun organPatch6(x: Double) =
    (organBase(x * DRAWBAR1) +
        organBase(x * DRAWBAR3) +
        organBase(x * DRAWBAR5) +
        organBase(x * DRAWBAR6)) / 4 * 1.7 + 0.13

fun organPatch7(x: Double) =
    (organBase(x * DRAWBAR3) +
        organBase(x * DRAWBAR5) +
        organBase(x * DRAWBAR6) +
        organBase(x * DRAWBAR7)) / 4 * 1.4 + 0.13

fun organPatch8(x: Double) =
    (organBase(x * DRAWBAR3) +
        organBase(x * DRAWBAR5) +
        organBase(x * DRAWBAR6) +
        organBase(x * DRAWBAR7) +
        organBase(x * DRAWBAR8)) / 5 * 1.44 + 0.15

fun organPatch9(x: Double) =
    (organBase(x * DRAWBAR1) +
        organBase(x * DRAWBAR2) +
        organBase(x * DRAWBAR3) +
        organBase(x * DRAWBAR4) +
        organBase(x * DRAWBAR5) +
        organBase(x * DRAWBAR6) +
        organBase(x * DRAWBAR7) +
        organBase(x * DRAWBAR8) +
        organBase(x * DRAWBAR9)) / 9 * 1.92 + 0.23

private inline fun organBase(x: Double): Double {
    val m = x - floor(x)
    return nsin(x * 2.0) * lerp(0.5, 1.0, m)
}
