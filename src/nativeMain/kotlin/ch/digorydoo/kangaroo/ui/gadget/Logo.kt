package ch.digorydoo.kangaroo.ui.gadget

import ch.digorydoo.kangaroo.geometry.MutablePoint2i
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext

class Logo(
    canvas: CanvasView,
    left: Int = 0,
    top: Int = 0,
    width: Int = CHILD_SIZE,
    height: Int = CHILD_SIZE,
): Gadget(canvas, left, top, width, height) {
    override val intrinsicSize = MutablePoint2i(80, 48);
    private val bmp = getBitmap("logo-small.png")

    override fun render(ctx: DrawContext, dirty: Recti) {
        ctx.setAntiAliasing(false) // otherwise it get's blurry
        bmp?.let { ctx.drawBitmap(it, currentArea) }
    }
}
