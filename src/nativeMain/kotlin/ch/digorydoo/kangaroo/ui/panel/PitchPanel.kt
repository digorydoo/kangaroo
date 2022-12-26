package ch.digorydoo.kangaroo.ui.panel

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.gadget.Button
import ch.digorydoo.kangaroo.ui.gadget.Gadget
import ch.digorydoo.kangaroo.ui.gadget.KnobAndLabel
import ch.digorydoo.kangaroo.ui.gadget.PanelFrame

class PitchPanel(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int,
    height: Int
): Gadget(canvas, left, top, width, height) {
    override val children: List<Gadget> = listOf(
        PanelFrame(
            canvas,
            "Pitch",
            titleSpace = 28,
            listOf(
                Button(canvas, 16, 25, width = 40, caption = "Mono", toggle = true, disabled = true),
                Button(canvas, 16, 45, width = 40, caption = "Retrig", toggle = true, disabled = true),
                KnobAndLabel(canvas, 56, 16, "Legato", disabled = true),
            )
        )
    )

    override fun render(ctx: DrawContext, dirty: Recti) {
        renderChildren(ctx, dirty)
    }
}
