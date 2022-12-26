package ch.digorydoo.kangaroo.ui.gadget

import ch.digorydoo.kangaroo.geometry.MutableRecti
import ch.digorydoo.kangaroo.geometry.Point2i
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext

class PanelFrame(
    canvas: CanvasView,
    private val title: String?,
    private val titleSpace: Int,
    private val roundBottomCorners: Boolean,
    override val children: List<Gadget?>,
): Gadget(canvas, 0, 0, PARENT_SIZE, PARENT_SIZE) {
    constructor(canvas: CanvasView, title: String, titleSpace: Int, children: List<Gadget?>):
        this(canvas, title, titleSpace, true, children)

    private var textWidth = 0

    private val textEnd get() = currentArea.left + LABEL_X_OFFSET + (if (titleSpace > 0) titleSpace else textWidth)

    override fun layout(parentOrigin: Point2i, parentSize: Point2i, ctx: DrawContext) {
        super.layout(parentOrigin, parentSize, ctx)
        ctx.setFontSize(FONT_SIZE)
        textWidth = title?.let { ctx.measureText(it) } ?: 0
    }

    override fun render(ctx: DrawContext, dirty: Recti) {
        ctx.setAntiAliasing(true)

        if (title == null) {
            ctx.setFillColour(theme.blueGrey800)
            ctx.fillRoundRect(currentArea, NORMAL_CORNER_SIZE)
        } else {
            ctx.setFontColour(theme.blueGrey600)
            ctx.setFontSize(FONT_SIZE)
            ctx.drawText(title, textEnd - textWidth, currentArea.top + LABEL_Y_OFFSET)

            ctx.setFillColour(theme.blueGrey800)
            val r = MutableRecti(
                textEnd + TITLE_CORNER_X_DELTA,
                currentArea.top,
                textEnd + TITLE_CORNER_X_DELTA + TITLE_CORNER_SIZE,
                currentArea.top + TITLE_HEIGHT + TITLE_CORNER_Y_DELTA,
            )
            ctx.fillRoundRect(
                r,
                TITLE_CORNER_SIZE,
                hasTopLeftCorner = true,
                hasTopRightCorner = false,
                hasBottomLeftCorner = false,
                hasBottomRightCorner = false
            )

            r.left = r.right
            r.right = currentArea.right
            ctx.fillRoundRect(
                r,
                NORMAL_CORNER_SIZE,
                hasTopLeftCorner = false,
                hasTopRightCorner = true,
                hasBottomLeftCorner = false,
                hasBottomRightCorner = false
            )

            r.set(
                currentArea.left,
                currentArea.top + TITLE_HEIGHT + TITLE_CORNER_Y_DELTA,
                currentArea.right,
                currentArea.top + TITLE_HEIGHT + TITLE_CORNER_Y_DELTA + TITLE_CORNER_SIZE
            )
            ctx.fillRoundRect(
                r,
                TITLE_CORNER_SIZE,
                hasTopLeftCorner = true,
                hasTopRightCorner = false,
                hasBottomLeftCorner = false,
                hasBottomRightCorner = false
            )

            r.top += TITLE_CORNER_SIZE
            r.bottom = currentArea.bottom

            if (roundBottomCorners) {
                ctx.fillRoundRect(
                    r,
                    NORMAL_CORNER_SIZE,
                    hasTopLeftCorner = false,
                    hasTopRightCorner = false,
                    hasBottomLeftCorner = true,
                    hasBottomRightCorner = true,
                )
            } else {
                ctx.fillRect(r)
            }
        }

        renderChildren(ctx, dirty)
    }

    companion object {
        private const val NORMAL_CORNER_SIZE = 16
        private const val FONT_SIZE = 11.0f
        private const val TITLE_CORNER_SIZE = 12
        private const val TITLE_CORNER_X_DELTA = 8
        private const val TITLE_CORNER_Y_DELTA = 6
        private const val TITLE_HEIGHT = (FONT_SIZE / 11 * 10).toInt()
        private const val LABEL_X_OFFSET = TITLE_CORNER_SIZE
        private const val LABEL_Y_OFFSET = 12
    }
}
