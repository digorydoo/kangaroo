package ch.digorydoo.kangaroo.geometry

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Point2f(
    open val x: Float,
    open val y: Float,
) {
    constructor(): this(0.0f, 0.0f)
    constructor(ix: Int, iy: Int): this(ix.toFloat(), iy.toFloat())
    constructor(pt: Point2f): this(pt.x, pt.y)

    open operator fun plus(other: Point2f) =
        Point2f(x + other.x, y + other.y)

    open operator fun minus(other: Point2f) =
        Point2f(x - other.x, y - other.y)

    open operator fun times(factor: Float) =
        Point2f(x * factor, y * factor)

    open operator fun div(divisor: Float) =
        Point2f(x / divisor, y / divisor)

    fun newScaled(factor: Float) =
        Point2f(x * factor, y * factor)

    fun newScaled(xfactor: Float, yfactor: Float) =
        Point2f(x * xfactor, y * yfactor)

    fun newScaled(factor: Point2f) =
        Point2f(x * factor.x, y * factor.y)

    fun isSame(pt: Point2f) =
        x == pt.x && y == pt.y

    fun sqrDistanceTo(px: Float, py: Float) =
        sqrDistanceTo(px.toDouble(), py.toDouble())

    fun sqrDistanceTo(px: Double, py: Double): Double {
        val dx = x - px
        val dy = y - py
        return dx * dx + dy * dy
    }

    fun distanceTo(px: Float, py: Float) =
        sqrt(sqrDistanceTo(px, py))

    fun distanceTo(pt: Point2f) =
        sqrt(sqrDistanceTo(pt.x, pt.y))

    fun distanceTo(pt: Point2i) =
        sqrt(sqrDistanceTo(pt.x.toFloat(), pt.y.toFloat()))

    fun length() =
        sqrt(sqrDistanceTo(0.0, 0.0)).toFloat()

    fun newNormalized(): Point2f {
        val len = length()
        return Point2f(x / len, y / len)
    }

    fun maxAbsComponent() =
        max(abs(x), abs(y))

    fun isZero() =
        x == 0.0f && y == 0.0f

    fun toMutable() =
        MutablePoint2f(x, y)

    fun toPoint2i() =
        Point2i(x.toInt(), y.toInt())

    final override fun toString() =
        "($x, $y)"
}

