package ch.digorydoo.kangaroo.ui.panel

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.gadget.Gadget
import ch.digorydoo.kangaroo.ui.gadget.KnobAndLabel
import ch.digorydoo.kangaroo.ui.gadget.PanelFrame
import ch.digorydoo.kangaroo.ui.gadget.WavePreview

class Osc3Panel(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int,
    height: Int,
): Gadget(canvas, left, top, width, height) {
    override val children: List<Gadget> = listOf(
        PanelFrame(
            canvas,
            "Osc 3",
            titleSpace = 51,
            listOf(
                WavePreview(canvas, 16, 32, big = true, tag = ParamTag.OSC3_SHAPE),
                KnobAndLabel(canvas, 76, 16, "Shape", big = true, tag = ParamTag.OSC3_SHAPE),
                KnobAndLabel(canvas, 8, 90, "Fold Osc1", tag = ParamTag.OSC3_FOLD_OSC1),
                KnobAndLabel(canvas, 56, 90, "Detune", tag = ParamTag.OSC3_DETUNE),
                KnobAndLabel(canvas, 104, 90, "Volume", tag = ParamTag.OSC3_VOLUME),
            )
        )
    )

    override fun render(ctx: DrawContext, dirty: Recti) {
        renderChildren(ctx, dirty)
    }
}
