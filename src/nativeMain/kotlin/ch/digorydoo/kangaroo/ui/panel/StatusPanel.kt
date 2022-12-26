package ch.digorydoo.kangaroo.ui.panel

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.gadget.Gadget
import ch.digorydoo.kangaroo.ui.gadget.Label

class StatusPanel(canvas: CanvasView, left: Int, top: Int, width: Int, height: Int):
    Gadget(canvas, left, top, width, height) {
    override val children: List<Gadget> = listOf(
        Label(
            canvas,
            0,
            10,
            width = PARENT_SIZE,
            text = "Kangaroo © 2022 by Digory Doolittle. All rights reserved. Written in Kotlin/native.",
            size = Label.Size.TINY,
            align = DrawContext.TextAlign.CENTRED,
            fgColour = theme.blueGrey800,
        ),
    )

    override fun render(ctx: DrawContext, dirty: Recti) {
        ctx.setAntiAliasing(true)
        ctx.setFillColour(theme.blueGrey920)
        ctx.fillRect(currentArea)

        renderChildren(ctx, dirty)
    }
}
