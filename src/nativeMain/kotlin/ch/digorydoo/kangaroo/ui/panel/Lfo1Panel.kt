package ch.digorydoo.kangaroo.ui.panel

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.gadget.Button
import ch.digorydoo.kangaroo.ui.gadget.Gadget
import ch.digorydoo.kangaroo.ui.gadget.KnobAndLabel
import ch.digorydoo.kangaroo.ui.gadget.PanelFrame
import ch.digorydoo.kangaroo.ui.gadget.WavePreview

class Lfo1Panel(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int,
    height: Int,
): Gadget(canvas, left, top, width, height) {
    override val children: List<Gadget> = listOf(
        PanelFrame(
            canvas,
            "LFO 1",
            titleSpace = 28,
            listOf(
                WavePreview(canvas, 16, 28, tag = ParamTag.LFO1_SHAPE),
                KnobAndLabel(canvas, 55, 12, "Shape", tag = ParamTag.LFO1_SHAPE),
                KnobAndLabel(canvas, 102, 12, "Fade", disabled = true),
                KnobAndLabel(canvas, 8, 67, "Rate", disabled = true),
                KnobAndLabel(canvas, 55, 67, "Amount", disabled = true),
                Button(canvas, 107, 91, width = 40, caption = "Poly", toggle = true, disabled = true),
            )
        )
    )

    override fun render(ctx: DrawContext, dirty: Recti) {
        renderChildren(ctx, dirty)
    }
}
