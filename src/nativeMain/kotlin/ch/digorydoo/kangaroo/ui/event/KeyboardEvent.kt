package ch.digorydoo.kangaroo.ui.event

@Suppress("MemberVisibilityCanBePrivate")
class KeyboardEvent(
    val kind: Kind,
    val c: Char,
    val vkey: Int,
    val repeat: Boolean,
    val shift: Boolean,
    val alt: Boolean
) {
    enum class Kind { DOWN, UP }

    override fun toString() =
        "KeyboardEvent($kind, c=$c, vkey=$vkey, repeat=$repeat, shift=$shift, alt=$alt)"
}
