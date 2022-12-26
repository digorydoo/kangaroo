package ch.digorydoo.kangaroo.ui.panel

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.gadget.Gadget
import ch.digorydoo.kangaroo.ui.gadget.KnobAndLabel
import ch.digorydoo.kangaroo.ui.gadget.PanelFrame

class FilterPanel(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int,
    height: Int
): Gadget(canvas, left, top, width, height) {
    override val children: List<Gadget> = listOf(
        PanelFrame(
            canvas,
            "Filter",
            titleSpace = 28,
            listOf(
                KnobAndLabel(canvas, 8, 24, "LPF", tag = ParamTag.LOW_PASS_FREQ, inverse = true),
                KnobAndLabel(canvas, 56, 24, "HPF", tag = ParamTag.HIGH_PASS_FREQ),

                KnobAndLabel(canvas, 8, 80, "BPF", tag = ParamTag.BAND_PASS_FREQ),
                KnobAndLabel(canvas, 56, 80, "Amount", tag = ParamTag.BAND_PASS_AMOUNT),

                KnobAndLabel(canvas, 8, 136, "Vowel", tag = ParamTag.VOWEL_FORMANT),
                KnobAndLabel(canvas, 56, 136, "Amount", tag = ParamTag.VOWEL_AMOUNT),
            )
        )
    )

    override fun render(ctx: DrawContext, dirty: Recti) {
        renderChildren(ctx, dirty)
    }
}
