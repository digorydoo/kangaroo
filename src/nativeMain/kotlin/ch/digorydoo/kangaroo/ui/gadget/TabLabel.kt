package ch.digorydoo.kangaroo.ui.gadget

import ch.digorydoo.kangaroo.geometry.Point2i
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.DrawContext.TextAlign

class TabLabel(
    canvas: CanvasView,
    left: Int,
    top: Int,
    private val text: String,
): Gadget(canvas, left, top, FRAME_WIDTH, FRAME_HEIGHT) {
    var selected = false
    private var textWidth = 0

    private val fgColour get() = if (selected) theme.blueGrey300 else theme.blueGrey400

    override fun layout(parentOrigin: Point2i, parentSize: Point2i, ctx: DrawContext) {
        super.layout(parentOrigin, parentSize, ctx)
        ctx.setFontSize(FONT_SIZE)
        textWidth = ctx.measureText(text)
    }

    override fun render(ctx: DrawContext, dirty: Recti) {
        ctx.setAntiAliasing(true)
        ctx.setFontColour(fgColour)
        ctx.setFontSize(FONT_SIZE)
        ctx.drawText(
            text,
            currentArea.left + currentArea.width / 2 - textWidth / 2,
            currentArea.top + TEXT_OFFSET_Y,
            TextAlign.LEFT
        )

        if (selected) {
            val r = Recti(currentArea.left, currentArea.bottom - 2, currentArea.right, currentArea.bottom)
            ctx.setFillColour(theme.accentDarker)
            ctx.fillRect(r)
        }
    }

    companion object {
        private const val FRAME_WIDTH = 80
        private const val FRAME_HEIGHT = 24
        private const val TEXT_OFFSET_Y = 16
        private const val FONT_SIZE = 11.0f
    }
}
