package ch.digorydoo.kangaroo.geometry

import ch.digorydoo.kangaroo.math.clamp
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Suppress("unused", "MemberVisibilityCanBePrivate")
class MutablePoint2f(theX: Float, theY: Float): Point2f() {
    constructor(): this(0.0f, 0.0f)
    constructor(ix: Int, iy: Int): this(ix.toFloat(), iy.toFloat())
    constructor(pt: Point2f): this(pt.x, pt.y)

    override var x: Float = theX
    override var y: Float = theY

    fun set(other: Point2f): MutablePoint2f {
        x = other.x
        y = other.y
        return this
    }

    fun set(theX: Int, theY: Int): MutablePoint2f {
        x = theX.toFloat()
        y = theY.toFloat()
        return this
    }

    fun set(theX: Float, theY: Float): MutablePoint2f {
        x = theX
        y = theY
        return this
    }

    fun add(pt: Point2f): MutablePoint2f {
        x += pt.x
        y += pt.y
        return this
    }

    fun add(theX: Float, theY: Float): MutablePoint2f {
        x += theX
        y += theY
        return this
    }

    fun subtract(pt: Point2f): MutablePoint2f {
        x -= pt.x
        y -= pt.y
        return this
    }

    fun subtract(theX: Float, theY: Float): MutablePoint2f {
        x -= theX
        y -= theY
        return this
    }

    fun scale(byX: Float, byY: Float): MutablePoint2f {
        x *= byX
        y *= byY
        return this
    }

    fun scale(factor: Float): MutablePoint2f {
        x *= factor
        y *= factor
        return this
    }

    fun scale(factor: Point2f): MutablePoint2f {
        x *= factor.x
        y *= factor.y
        return this
    }

    override operator fun plus(other: Point2f) =
        MutablePoint2f(x + other.x, y + other.y)

    override operator fun minus(other: Point2f) =
        MutablePoint2f(x - other.x, y - other.y)

    override operator fun times(factor: Float) =
        MutablePoint2f(x * factor, y * factor)

    // operator fun times(factor: Point2f) dot product...

    override operator fun div(divisor: Float) =
        MutablePoint2f(x / divisor, y / divisor)

    operator fun plusAssign(other: Point2f) {
        x += other.x
        y += other.y
    }

    operator fun minusAssign(other: Point2f) {
        x -= other.x
        y -= other.y
    }

    operator fun timesAssign(factor: Float) {
        x *= factor
        y *= factor
    }

    operator fun divAssign(divisor: Float) {
        x /= divisor
        y /= divisor
    }

    fun clampTo(left: Float, top: Float, right: Float, bottom: Float): MutablePoint2f {
        x = clamp(x, left, right)
        y = clamp(y, top, bottom)
        return this
    }

    fun roundToGrid(xgrid: Int, ygrid: Int) =
        roundToGrid(xgrid.toFloat(), ygrid.toFloat())

    fun roundToGrid(xgrid: Float, ygrid: Float): MutablePoint2f {
        x = (x / xgrid).roundToInt() * xgrid
        y = (y / ygrid).roundToInt() * ygrid
        return this
    }

    fun normalize(): MutablePoint2f {
        val len = length()

        if (len > 0) {
            x /= len
            y /= len
        } else {
            x = 0.0f
            y = 0.0f
        }

        return this
    }

    fun rotate(alpha: Float): MutablePoint2f {
        val ca = cos(alpha)
        val sa = sin(alpha)
        val newX = x * ca + y * sa
        val newY = y * ca - x * sa
        x = newX
        y = newY
        return this
    }

    fun rotate(alpha: Double): MutablePoint2f {
        val ca = cos(alpha)
        val sa = sin(alpha)
        val newX = x * ca + y * sa
        val newY = y * ca - x * sa
        x = newX.toFloat()
        y = newY.toFloat()
        return this
    }

    fun toImmutable() =
        Point2f(x, y)
}

