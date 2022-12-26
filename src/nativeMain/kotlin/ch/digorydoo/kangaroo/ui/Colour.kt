package ch.digorydoo.kangaroo.ui

import ch.digorydoo.kangaroo.math.clamp
import ch.digorydoo.kangaroo.math.lerp

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Colour(
    open val red: Float,
    open val green: Float,
    open val blue: Float,
    open val alpha: Float,
) {
    constructor(): this(0.0f, 0.0f, 0.0f, 1.0f)
    constructor(r: Float, g: Float, b: Float): this(r, g, b, 1.0f)

    protected var cachedARGB: Int? = null

    val redByte: Byte
        get() = (255.0f * red).toInt().toByte()

    val greenByte: Byte
        get() = (255.0f * green).toInt().toByte()

    val blueByte: Byte
        get() = (255.0f * blue).toInt().toByte()

    val alphaByte: Byte
        get() = (255.0f * alpha).toInt().toByte()

    val luminance: Float
        get() = (red * 1.0f + green * 1.2f + blue * 0.9f) / (1.0f + 1.2f + 0.9f)

    val isDark: Boolean
        get() = luminance < 0.5f

    open fun toARGB(): Int {
        var argb = cachedARGB

        if (argb != null) {
            return argb
        }

        val nA = clamp(alpha * 255.0f, 0f, 255f).toInt()
        val nR = clamp(red * 255.0f, 0f, 255f).toInt()
        val nG = clamp(green * 255.0f, 0f, 255f).toInt()
        val nB = clamp(blue * 255.0f, 0f, 255f).toInt()
        argb = nA shl 24 or (nR shl 16) or (nG shl 8) or nB
        cachedARGB = argb
        return argb
    }

    final override fun toString() =
        "#" + floatToHexByte(red) + floatToHexByte(green) + floatToHexByte(blue)

    open fun newMixed(other: Colour, rel: Float) =
        mix(this, other, rel)

    open fun newRGBMultiplied(factor: Float) =
        Colour(red * factor, green * factor, blue * factor, alpha)

    companion object {
        val white = Colour(1.0f, 1.0f, 1.0f)
        val black = Colour()
        val transparent = Colour(0.0f, 0.0f, 0.0f, 0.0f)

        fun fromBytes(r: Byte, g: Byte, b: Byte, a: Byte) =
            Colour(
                (r.toUInt() and 255u).toFloat() / 255.0f,
                (g.toUInt() and 255u).toFloat() / 255.0f,
                (b.toUInt() and 255u).toFloat() / 255.0f,
                (a.toUInt() and 255u).toFloat() / 255.0f
            )

        fun fromString(s: String) =
            if (s.length == 4 && s[0] == '#') {
                Colour(
                    hexNibbleToFloat(s[1]),
                    hexNibbleToFloat(s[2]),
                    hexNibbleToFloat(s[3])
                )
            } else if (s.length == 7 && s[0] == '#') {
                Colour(
                    hexByteToFloat(s.slice(1..2)),
                    hexByteToFloat(s.slice(3..4)),
                    hexByteToFloat(s.slice(5..6)),
                )
            } else {
                black
            }

        private fun hexNibbleToFloat(c: Char) =
            c.toString().toInt(radix = 16).let { ((it shl 4) + it).toFloat() / 255.0f }

        private fun hexByteToFloat(s: String) =
            s.toInt(radix = 16).toFloat() / 255.0f

        private fun floatToHexByte(f: Float) =
            (f * 255.0f).toInt().toString(radix = 16).padStart(2, '0')

        fun fromARGB(argb: Int): Colour {
            val a = (argb shr 24 and 0xFF).toFloat() / 255.0f
            val r = (argb shr 16 and 0xFF).toFloat() / 255.0f
            val g = (argb shr 8 and 0xFF).toFloat() / 255.0f
            val b = (argb and 0xFF).toFloat() / 255.0f
            return Colour(r, g, b, a).also { it.cachedARGB = argb }
        }

        fun mix(c1: Colour, c2: Colour, rel: Float) =
            Colour(
                lerp(c1.red, c2.red, rel),
                lerp(c1.green, c2.green, rel),
                lerp(c1.blue, c2.blue, rel),
                lerp(c1.alpha, c2.alpha, rel),
            )

        fun mixARGB(colour1: Int, colour2: Int, amount: Float): Int {
            val c1 = fromARGB(colour1)
            val c2 = fromARGB(colour2)
            return c1.newMixed(c2, amount).toARGB()
        }
    }
}
