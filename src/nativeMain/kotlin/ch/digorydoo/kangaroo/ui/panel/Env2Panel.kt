package ch.digorydoo.kangaroo.ui.panel

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.gadget.EnvPreview
import ch.digorydoo.kangaroo.ui.gadget.Gadget
import ch.digorydoo.kangaroo.ui.gadget.KnobAndLabel
import ch.digorydoo.kangaroo.ui.gadget.PanelFrame

class Env2Panel(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int,
    height: Int
): Gadget(canvas, left, top, width, height) {
    override val children: List<Gadget> = listOf(
        PanelFrame(
            canvas,
            "Env 2",
            titleSpace = 70,
            listOf(
                EnvPreview(canvas, 8, 24),
                KnobAndLabel(canvas, 94, 8, "Attack", disabled = true),
                KnobAndLabel(canvas, 142, 8, "Decay", disabled = true),
                KnobAndLabel(canvas, 190, 8, "Sustain", disabled = true),
                KnobAndLabel(canvas, 238, 8, "Release", disabled = true),
            ),
        )
    )

    override fun render(ctx: DrawContext, dirty: Recti) {
        renderChildren(ctx, dirty)
    }
}
