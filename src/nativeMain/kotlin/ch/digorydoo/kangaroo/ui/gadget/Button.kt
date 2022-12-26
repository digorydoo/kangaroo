package ch.digorydoo.kangaroo.ui.gadget

import ch.digorydoo.kangaroo.geometry.Point2i
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.Colour
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.event.MouseEvent
import ch.digorydoo.kangaroo.ui.icon.AbstrIcon
import kotlin.math.ceil
import kotlin.math.min

class Button(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int = CHILD_SIZE,
    height: Int = CHILD_SIZE,
    private val caption: String = "",
    private val toggle: Boolean = false,
    var disabled: Boolean = false,
    override var hidden: Boolean = false,
    private val shape: Shape = Shape.PILL,
    private val fgColour: Colour? = null,
    private val bgColour: Colour? = null,
    private val icon: AbstrIcon? = null,
    private val onClick: () -> Unit = {},
): Gadget(canvas, left, top, width, height) {
    enum class Shape { PILL, PILL_LEFT_HALF, PILL_RIGHT_HALF, CIRCULAR }

    var toggleState = false
    private var pressed = false
    private val fontSize = 11.0f
    private var textWidth = 0
    private var textHeight = 0
    private var cornerSize = 0

    private val actualBgColour
        get() = when {
            pressed -> theme.blueGrey600
            toggle && toggleState && !disabled -> theme.accentDarker
            else -> bgColour ?: theme.blueGrey700
        }

    private val actualFgColour
        get() = when {
            pressed -> theme.blueGrey200
            toggle && toggleState && !disabled -> Colour.white
            else -> fgColour ?: theme.blueGrey300
        }

    private val textXOffset: Int
        get() = when (shape) {
            Shape.PILL_LEFT_HALF -> 1
            Shape.PILL_RIGHT_HALF -> -1
            else -> 0
        }

    override fun layout(parentOrigin: Point2i, parentSize: Point2i, ctx: DrawContext) {
        ctx.setFontSize(fontSize)
        textWidth = ctx.measureText(caption)
        textHeight = ceil(fontSize).toInt()
        intrinsicSize.set(textWidth + 2 * PADDING_X, textHeight + 2 * PADDING_Y)
        cornerSize = min(intrinsicSize.x, intrinsicSize.y) / 2

        super.layout(parentOrigin, parentSize, ctx)

        icon?.setOrigin(
            (currentArea.left + currentArea.right) / 2 - icon.size.x / 2,
            (currentArea.top + currentArea.bottom) / 2 - icon.size.y / 2,
        )
    }

    override fun render(ctx: DrawContext, dirty: Recti) {
        if (hidden) {
            return
        }

        ctx.setGlobalAlpha(if (disabled) theme.disabledOpacity else 1.0f)
        ctx.setAntiAliasing(true) // to get nicer round rects
        ctx.setFillColour(actualBgColour)

        when (shape) {
            Shape.PILL -> ctx.fillRoundRect(currentArea, cornerSize)
            Shape.PILL_LEFT_HALF -> ctx.fillRoundRect(
                currentArea,
                cornerSize,
                hasTopLeftCorner = true,
                hasTopRightCorner = false,
                hasBottomLeftCorner = true,
                hasBottomRightCorner = false
            )
            Shape.PILL_RIGHT_HALF -> ctx.fillRoundRect(
                currentArea,
                cornerSize,
                hasTopLeftCorner = false,
                hasTopRightCorner = true,
                hasBottomLeftCorner = false,
                hasBottomRightCorner = true
            )
            Shape.CIRCULAR -> ctx.fillEllipse(currentArea)
        }

        ctx.setFontSize(11.0f)
        val descent = ctx.getFontProps(11.0f).descent
        ctx.setFontColour(actualFgColour)
        ctx.drawText(
            caption,
            (currentArea.left + currentArea.right) / 2 + textXOffset,
            (currentArea.top + currentArea.bottom) / 2 + descent,
            DrawContext.TextAlign.CENTRED
        )

        icon?.colour = actualFgColour
        icon?.render(ctx, dirty)
    }

    override fun onMouseDown(evt: MouseEvent) =
        if (hidden || disabled) {
            null
        } else {
            pressed = true
            invalidate()
            this // send onMouseUp here
        }

    override fun onMouseUp(evt: MouseEvent) {
        if (!hidden && !disabled && pressed) {
            if (currentArea.contains(evt.pos)) {
                if (toggle) {
                    toggleState = !toggleState
                }

                onClick()
            }

            pressed = false
            invalidate()
        }
    }

    companion object {
        private const val PADDING_X = 6
        private const val PADDING_Y = 3
    }
}
