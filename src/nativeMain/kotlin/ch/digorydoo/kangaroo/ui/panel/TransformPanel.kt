package ch.digorydoo.kangaroo.ui.panel

import ch.digorydoo.kangaroo.geometry.Point2i
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.gadget.Button
import ch.digorydoo.kangaroo.ui.gadget.Gadget
import ch.digorydoo.kangaroo.ui.gadget.PanelFrame
import ch.digorydoo.kangaroo.ui.icon.TransformIcon

class TransformPanel(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int,
    height: Int,
): Gadget(canvas, left, top, width, height) {
    override val children: List<Gadget> = listOf(
        PanelFrame(
            canvas,
            "Transformers",
            titleSpace = 160,
            roundBottomCorners = false,
            listOf(
                Button(canvas, 192, 8, width = 48, caption = "LFO 1", toggle = true, disabled = true),
                Button(canvas, 244, 8, width = 48, caption = "LFO 2", toggle = true, disabled = true),
                Button(canvas, 296, 8, width = 48, caption = "LFO 3", toggle = true, disabled = true),
                Button(canvas, 348, 8, width = 48, caption = "AEnv", toggle = true, disabled = true),
                Button(canvas, 400, 8, width = 48, caption = "Env 2", toggle = true, disabled = true),
                Button(canvas, 452, 8, width = 48, caption = "Env 3", toggle = true, disabled = true),
                Button(canvas, 504, 8, width = 48, caption = "Touch", toggle = true, disabled = true),
                Button(canvas, 556, 8, width = 48, caption = "Key", toggle = true, disabled = true),
                Button(canvas, 608, 8, width = 48, caption = "Velo", toggle = true, disabled = true),
                Button(canvas, 660, 8, width = 48, caption = "Wheel", toggle = true, disabled = true),
                Button(canvas, 712, 8, width = 48, caption = "Bend", toggle = true, disabled = true),
            )
        )
    )

    private val icon = TransformIcon(theme)

    override fun layout(parentOrigin: Point2i, parentSize: Point2i, ctx: DrawContext) {
        super.layout(parentOrigin, parentSize, ctx)
        icon.setOrigin(currentArea.left + 84, currentArea.top + 4)
    }

    override fun render(ctx: DrawContext, dirty: Recti) {
        renderChildren(ctx, dirty)
        icon.render(ctx, dirty)
    }
}
