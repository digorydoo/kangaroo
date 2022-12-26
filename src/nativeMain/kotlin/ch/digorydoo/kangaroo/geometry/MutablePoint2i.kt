package ch.digorydoo.kangaroo.geometry

@Suppress("unused", "MemberVisibilityCanBePrivate")
class MutablePoint2i(theX: Int, theY: Int): Point2i() {
    constructor(): this(0, 0)
    constructor(pt: Point2i): this(pt.x, pt.y)

    override var x: Int = theX
    override var y: Int = theY

    fun set(other: Point2i): MutablePoint2i {
        x = other.x
        y = other.y
        return this
    }

    fun set(theX: Int, theY: Int): MutablePoint2i {
        x = theX
        y = theY
        return this
    }

    fun add(pt: Point2i): MutablePoint2i {
        x += pt.x
        y += pt.y
        return this
    }

    fun add(dx: Int, dy: Int): MutablePoint2i {
        x += dx
        y += dy
        return this
    }

    fun subtract(pt: Point2i): MutablePoint2i {
        x -= pt.x
        y -= pt.y
        return this
    }

    fun subtract(dx: Int, dy: Int): MutablePoint2i {
        x -= dx
        y -= dy
        return this
    }

    override operator fun plus(other: Point2i) =
        MutablePoint2i(x + other.x, y + other.y)

    override operator fun minus(other: Point2i) =
        MutablePoint2i(x - other.x, y - other.y)

    operator fun plusAssign(other: Point2i) {
        x += other.x
        y += other.y
    }

    operator fun minusAssign(other: Point2i) {
        x -= other.x
        y -= other.y
    }

    fun toImmutable() =
        Point2i(x, y)
}

