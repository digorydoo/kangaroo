package ch.digorydoo.kangaroo.ui.panel

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.gadget.Gadget
import ch.digorydoo.kangaroo.ui.gadget.KnobAndLabel
import ch.digorydoo.kangaroo.ui.gadget.PanelFrame

class NoisePanel(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int,
    height: Int,
): Gadget(canvas, left, top, width, height) {
    override val children: List<Gadget> = listOf(
        PanelFrame(
            canvas,
            "Noise",
            titleSpace = 28,
            listOf(
                KnobAndLabel(canvas, 8, 24, "Shape", tag = ParamTag.NOISE_SHAPE),
                KnobAndLabel(canvas, 56, 24, "Volume", tag = ParamTag.NOISE_VOLUME),
            )
        )
    )

    override fun render(ctx: DrawContext, dirty: Recti) {
        renderChildren(ctx, dirty)
    }
}
