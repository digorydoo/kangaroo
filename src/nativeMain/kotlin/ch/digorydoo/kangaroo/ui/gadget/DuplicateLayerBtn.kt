package ch.digorydoo.kangaroo.ui.gadget

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext

class DuplicateLayerBtn(canvas: CanvasView, left: Int, top: Int): Gadget(canvas, left, top, FRAME_WIDTH, CHILD_SIZE) {
    override val children: List<Gadget> = listOf(
        ConfirmableBtn(canvas, 0, 0, FRAME_WIDTH, "Duplicate", ::onClick)
    )

    override fun render(ctx: DrawContext, dirty: Recti) {
        renderChildren(ctx, dirty)
    }

    private fun onClick() {
        println("TODO")
    }

    companion object {
        private const val FRAME_WIDTH = 80
    }
}
