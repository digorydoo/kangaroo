package ch.digorydoo.kangaroo.ui.panel

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.gadget.Gadget
import ch.digorydoo.kangaroo.ui.gadget.KnobAndLabel
import ch.digorydoo.kangaroo.ui.gadget.PanelFrame

class EffectPanel(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int,
    height: Int
): Gadget(canvas, left, top, width, height) {
    override val children: List<Gadget> = listOf(
        PanelFrame(
            canvas,
            "Effect",
            titleSpace = 28,
            listOf(
                KnobAndLabel(canvas, 56, 8, "Reverb", disabled = true),
                KnobAndLabel(canvas, 8, 64, "Chorus", disabled = true),
                KnobAndLabel(canvas, 56, 64, "Depth", disabled = true),
            )
        )
    )

    override fun render(ctx: DrawContext, dirty: Recti) {
        renderChildren(ctx, dirty)
    }
}
