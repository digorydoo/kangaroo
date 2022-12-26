package ch.digorydoo.kangaroo.ui.panel

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.gadget.EnvPreview
import ch.digorydoo.kangaroo.ui.gadget.Gadget
import ch.digorydoo.kangaroo.ui.gadget.KnobAndLabel
import ch.digorydoo.kangaroo.ui.gadget.PanelFrame

class Env1Panel(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int,
    height: Int
): Gadget(canvas, left, top, width, height) {
    override val children: List<Gadget> = listOf(
        PanelFrame(
            canvas,
            "AEnv",
            titleSpace = 70,
            listOf(
                EnvPreview(canvas, 8, 24),
                KnobAndLabel(canvas, 94, 8, "Attack", tag = ParamTag.ENV1_ATTACK),
                KnobAndLabel(canvas, 142, 8, "Decay", tag = ParamTag.ENV1_DECAY),
                KnobAndLabel(canvas, 190, 8, "Sustain", tag = ParamTag.ENV1_SUSTAIN),
                KnobAndLabel(canvas, 238, 8, "Release", tag = ParamTag.ENV1_RELEASE),
            ),
        ),
    )

    override fun render(ctx: DrawContext, dirty: Recti) {
        renderChildren(ctx, dirty)
    }
}
