package ch.digorydoo.kangaroo.ui.gadget

import ch.digorydoo.kangaroo.audio.waveform.lfoShape
import ch.digorydoo.kangaroo.audio.waveform.osc1Shape
import ch.digorydoo.kangaroo.audio.waveform.osc2Shape
import ch.digorydoo.kangaroo.audio.waveform.osc3Shape
import ch.digorydoo.kangaroo.geometry.MutableRecti
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.math.sign
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import kotlin.math.abs

class WavePreview(
    canvas: CanvasView,
    left: Int,
    top: Int,
    private val big: Boolean = false,
    private val tag: ParamTag,
    frameSize: Int = if (big) BIG_FRAME_SIZE else SMALL_FRAME_SIZE,
): Gadget(canvas, left, top, frameSize, frameSize) {
    private var normValue = 0.0f
    private val cornerSize get() = if (big) BIG_CORNER_SIZE else SMALL_CORNER_SIZE
    private val innerFrameInset get() = if (big) BIG_INNER_FRAME_INSET else SMALL_INNER_FRAME_INSET

    override fun render(ctx: DrawContext, dirty: Recti) {
        ctx.setAntiAliasing(true)
        ctx.setLineWidth(1)
        ctx.setFillColour(theme.blueGrey900)
        ctx.fillRoundRect(currentArea, cornerSize)

        val r = MutableRecti(currentArea)
        r.inset(innerFrameInset)
        ctx.setAntiAliasing(false)
        ctx.setFrameColour(theme.blueGrey850)
        ctx.drawRect(r)

        val xm = (r.left + r.right) / 2
        val ym = (r.top + r.bottom) / 2
        ctx.drawLine(xm, r.top + 2, xm, r.bottom - 3)
        ctx.drawLine(r.left + 2, ym, r.right - 3, ym)

        val shape = when (tag) {
            ParamTag.OSC1_SHAPE -> ::osc1Shape
            ParamTag.OSC2_SHAPE -> ::osc2Shape
            ParamTag.OSC3_SHAPE -> ::osc3Shape
            ParamTag.LFO1_SHAPE -> ::lfoShape
            ParamTag.LFO2_SHAPE -> ::lfoShape
            else -> null
        }

        if (shape != null) {
            ctx.setFrameColour(theme.accent)
            val w2 = 2 * r.width
            val height = r.height + 0.5
            var prevy = 0.0f

            for (x2 in 0..w2) {
                val relx = x2.toDouble() / (w2 + 1)
                val rely = 0.5 - 0.5 * shape(relx, 1.0, normValue.toDouble())
                val y = (height * rely).toFloat() - 1
                val x = x2 / 2.0f

                if (x2 > 0) {
                    // Connect dot to previous y
                    val dy = abs(y - prevy).toInt()

                    if (dy > 1) {
                        val sy = sign(y - prevy)

                        for (i in 1 until dy) {
                            val dx = i.toFloat() / dy - 1.0f // in -1 <..< 0
                            ctx.drawSubpixel(r.left + x + dx, r.top + prevy + i * sy)
                        }
                    }
                }

                ctx.drawSubpixel(r.left + x, r.top + y)
                prevy = y
            }
        }
    }

    override fun onParamChanged(tag: ParamTag, newNormValue: Float) {
        if (tag == this.tag && newNormValue != normValue) {
            normValue = newNormValue
            invalidate()
        }
    }

    companion object {
        private const val BIG_FRAME_SIZE = 47 // must be odd
        private const val BIG_CORNER_SIZE = 12
        private const val BIG_INNER_FRAME_INSET = 8

        const val SMALL_FRAME_SIZE = 31 // must be odd
        const val SMALL_CORNER_SIZE = 8
        const val SMALL_INNER_FRAME_INSET = 4
    }
}
