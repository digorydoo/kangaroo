package ch.digorydoo.kangaroo.ui.panel

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.gadget.*

class TopPanel(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int,
    height: Int,
): Gadget(canvas, left, top, width, height) {
    override val children: List<Gadget> = listOf(
        Logo(canvas, 16, 8),

        Button(canvas, 135, 44, width = 48, caption = "Import", disabled = true, shape = Button.Shape.PILL_LEFT_HALF),
        Button(canvas, 184, 44, width = 48, caption = "Export", disabled = true, shape = Button.Shape.PILL_RIGHT_HALF),

        SurpriseMeBtn(canvas, 264, 44),
        DuplicateLayerBtn(canvas, 376, 44),

        Button(canvas, 584, 44, caption = "Split", toggle = true, disabled = true),
        Label(canvas, 624, 47, text = "at", size = Label.Size.TINY),
        Label(canvas, 638, 46, text = "C2", size = Label.Size.SMALL, fgColour = theme.accent),

        TabLabel(canvas, 680, 40, "Layer A"),
        TabLabel(canvas, 760, 40, "Layer B").apply { selected = true },
    )

    override fun render(ctx: DrawContext, dirty: Recti) {
        renderChildren(ctx, dirty)
    }
}
