package ch.digorydoo.kangaroo.ui.gadget

import ch.digorydoo.kangaroo.geometry.MutableRecti
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext

class EnvPreview(
    canvas: CanvasView,
    left: Int,
    top: Int,
): Gadget(canvas, left, top, FRAME_WIDTH, FRAME_HEIGHT) {
    override fun render(ctx: DrawContext, dirty: Recti) {
        ctx.setAntiAliasing(true)
        ctx.setLineWidth(1)
        ctx.setFillColour(theme.blueGrey900)
        ctx.fillRoundRect(currentArea, CORNER_SIZE)

        ctx.setAntiAliasing(false)

        val r = MutableRecti(currentArea)
        r.inset(INNER_FRAME_INSET)
        ctx.setFrameColour(theme.blueGrey850)
        ctx.drawRect(r)

        val susx = r.left + (r.width * 0.4f).toInt()
        val susy = r.top + (r.height * 0.3f).toInt()
        ctx.drawLine(susx, r.top + 2, susx, r.bottom - 3)
        ctx.drawLine(r.left + 2, susy, r.right - 3, susy)

        ctx.setAntiAliasing(true)
        ctx.setFrameColour(theme.accent)
        ctx.drawLine(r.left, r.bottom - 1, r.left + (r.width * 0.2f).toInt(), r.top)
        ctx.lineTo(susx, susy)
        ctx.lineTo(r.left + (r.width * 0.7f).toInt(), susy)
        ctx.lineTo(r.right - 1, r.bottom - 1)
    }

    companion object {
        private const val FRAME_WIDTH = 78
        private const val FRAME_HEIGHT = 32
        private const val CORNER_SIZE = WavePreview.SMALL_CORNER_SIZE
        private const val INNER_FRAME_INSET = WavePreview.SMALL_INNER_FRAME_INSET
    }
}
