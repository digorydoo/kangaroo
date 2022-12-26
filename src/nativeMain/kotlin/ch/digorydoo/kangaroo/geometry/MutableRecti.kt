package ch.digorydoo.kangaroo.geometry

@Suppress("unused", "MemberVisibilityCanBePrivate")
class MutableRecti(
    theLeft: Int,
    theTop: Int,
    theRight: Int,
    theBottom: Int,
): Recti() {
    constructor(): this(0, 0, 0, 0)
    constructor(l: Float, t: Float, r: Float, b: Float): this(l.toInt(), t.toInt(), r.toInt(), b.toInt())
    constructor(r: Recti): this(r.left, r.top, r.right, r.bottom)

    override var left = theLeft
    override var top = theTop
    override var right = theRight
    override var bottom = theBottom

    fun set(l: Int, t: Int, r: Int, b: Int): MutableRecti {
        left = l
        top = t
        right = r
        bottom = b
        return this
    }

    fun set(other: Recti): MutableRecti {
        left = other.left
        top = other.top
        right = other.right
        bottom = other.bottom
        return this
    }

    fun set(origin: Point2i, size: Point2i): MutableRecti {
        left = origin.x
        top = origin.y
        right = origin.x + size.x
        bottom = origin.y + size.y
        return this
    }

    fun offset(dx: Int, dy: Int): MutableRecti {
        left += dx
        top += dy
        right += dx
        bottom += dy
        return this
    }

    fun inset(dx: Int, dy: Int): MutableRecti {
        left += dx
        top += dy
        right -= dx
        bottom -= dy
        return this
    }

    fun inset(d: Int): MutableRecti {
        left += d
        top += d
        right -= d
        bottom -= d
        return this
    }

    fun toImmutable() =
        Recti(left, top, right, bottom)
}
