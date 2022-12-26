package ch.digorydoo.kangaroo.ui.event

import ch.digorydoo.kangaroo.geometry.Point2i

@Suppress("MemberVisibilityCanBePrivate")
class MouseEvent(
    val kind: Kind,
    val pos: Point2i,
    val shift: Boolean = false,
    val alt: Boolean = false,
    val clickCount: Int = -1,
) {
    enum class Kind { DOWN, MOVE, UP, CANCEL }

    override fun toString() =
        "MouseEvent($kind, $pos, clickCount=$clickCount, shift=$shift, alt=$alt)"
}
