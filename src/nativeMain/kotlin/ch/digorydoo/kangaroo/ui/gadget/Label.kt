package ch.digorydoo.kangaroo.ui.gadget

import ch.digorydoo.kangaroo.geometry.Point2i
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.Colour
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.DrawContext.TextAlign

class Label(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int = CHILD_SIZE,
    var text: String,
    size: Size = Size.NORMAL,
    private val align: TextAlign = TextAlign.LEFT,
    private val fgColour: Colour? = null,
    private val bgColour: Colour? = null,
): Gadget(canvas, left, top, width, CHILD_SIZE) {
    enum class Size { LARGE, NORMAL, SMALL, TINY }

    private var textWidth = 0
    private var textHeight = 0

    private val fontSize = when (size) {
        Size.LARGE -> 18.0f
        Size.NORMAL -> 13.0f
        Size.SMALL -> 11.0f
        Size.TINY -> 9.0f
    }

    override fun layout(parentOrigin: Point2i, parentSize: Point2i, ctx: DrawContext) {
        ctx.setFontSize(fontSize)
        textWidth = ctx.measureText(text)
        textHeight = ctx.getFontProps(fontSize).height
        intrinsicSize.set(textWidth, textHeight)
        super.layout(parentOrigin, parentSize, ctx)
    }

    override fun render(ctx: DrawContext, dirty: Recti) {
        if (bgColour != null) {
            ctx.setFillColour(bgColour)
            ctx.fillRect(currentArea)
        }

        ctx.setFontColour(fgColour ?: theme.blueGrey300)
        ctx.setFontSize(fontSize)
        val ascent = ctx.getFontProps(fontSize).ascent
        val x = when (align) {
            TextAlign.LEFT -> currentArea.left
            TextAlign.RIGHT -> currentArea.right - 1
            TextAlign.CENTRED -> (currentArea.left + currentArea.right) / 2
        }
        ctx.drawText(text, x, currentArea.top + ascent, align)
    }
}
