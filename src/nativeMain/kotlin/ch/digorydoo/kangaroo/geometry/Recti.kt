package ch.digorydoo.kangaroo.geometry

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Recti(
    open val left: Int,
    open val top: Int,
    open val right: Int,
    open val bottom: Int,
) {
    constructor(): this(0, 0, 0, 0)
    constructor(l: Float, t: Float, r: Float, b: Float): this(l.toInt(), t.toInt(), r.toInt(), b.toInt())
    constructor(r: Recti): this(r.left, r.top, r.right, r.bottom)

    val origin: Point2i
        get() = Point2i(left, top)

    val width: Int
        get() = right - left

    val height: Int
        get() = bottom - top

    val size: Point2i
        get() = Point2i(width, height)

    fun centre() =
        Point2f(
            (left + right) / 2.0f,
            (top + bottom) / 2.0f
        )

    fun isSame(other: Recti) =
        left == other.left && top == other.top && right == other.right && bottom == other.bottom

    fun contains(pt: Point2i) =
        pt.x >= left && pt.x < right && pt.y >= top && pt.y < bottom

    fun overlaps(r: Recti) =
        (left >= r.left || right > r.left) &&
            (left < r.right || right < r.right) &&
            (top >= r.top || bottom > r.top) &&
            (top < r.bottom || bottom < r.bottom)

    fun toMutable() =
        MutableRecti(left, top, right, bottom)

    final override fun toString() =
        "($left, $top)-($right, $bottom)"
}
