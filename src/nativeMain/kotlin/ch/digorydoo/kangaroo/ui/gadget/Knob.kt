package ch.digorydoo.kangaroo.ui.gadget

import ch.digorydoo.kangaroo.geometry.MutablePoint2i
import ch.digorydoo.kangaroo.geometry.MutableRecti
import ch.digorydoo.kangaroo.geometry.Point2i
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.math.clamp
import ch.digorydoo.kangaroo.param.Param
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.param.ParamsList
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.event.MouseEvent
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class Knob(
    canvas: CanvasView,
    left: Int,
    top: Int,
    big: Boolean,
    private val dark: Boolean,
    private val inverse: Boolean,
    private val disabled: Boolean,
    private val tag: ParamTag?,
    private val beginEdit: () -> Unit,
    private val endEdit: () -> Unit,
    private val valueChanged: () -> Unit,
): Gadget(canvas, left, top, CHILD_SIZE, CHILD_SIZE) {
    private val param = tag?.let { ParamsList.get(it) }
    private val balanced = param?.balanced == true

    var normValue = 0.0f; private set // between 0..1 even when balanced

    private var dragging = false
    private val dragStartPos = MutablePoint2i()
    private var dragStartValue = 0.0f

    private val outerRadius: Int = if (big) BIG_OUTER_CIRCLE_RADIUS else SMALL_OUTER_CIRCLE_RADIUS
    private val innerRadius: Int = if (big) BIG_INNER_CIRCLE_RADIUS else SMALL_INNER_CIRCLE_RADIUS

    private val indicatorOuterRadius: Int = if (big) BIG_INDICATOR_OUTER_RADIUS else SMALL_INDICATOR_OUTER_RADIUS
    private val indicatorInnerRadius: Int = if (big) BIG_INDICATOR_INNER_RADIUS else SMALL_INDICATOR_INNER_RADIUS

    override val intrinsicSize = MutablePoint2i(outerRadius * 2, outerRadius * 2)

    private val startAngle
        get() = when (balanced) {
            true -> (1.5 * PI).toFloat()
            false -> (0.75 * PI).toFloat()
        }

    private val angleFactor
        get() = when (balanced) {
            true -> (0.75 * PI).toFloat()
            false -> (1.5 * PI).toFloat()
        }

    private val innerCircleColour
        get() = when {
            dark -> theme.blueGrey700
            else -> theme.blueGrey650
        }

    private val outerTrackColour
        get() = when (dark) {
            true -> theme.blueGrey950
            false -> theme.blueGrey900
        }

    private val minMaxLineColour
        get() = when (dark) {
            true -> theme.blueGrey900
            false -> theme.blueGrey800
        }

    private val valueArcColour
        get() = when (dark) {
            true -> theme.accentDark
            false -> theme.accent
        }

    override fun render(ctx: DrawContext, dirty: Recti) {
        ctx.setGlobalAlpha(if (disabled) theme.disabledOpacity else 1.0f)

        // Fill the inner circle

        ctx.setAntiAliasing(true)
        val r = MutableRecti(currentArea).apply { inset(outerRadius - innerRadius) }
        ctx.setFillColour(innerCircleColour)
        ctx.fillEllipse(r)

        // A thick arc from minAngle to maxAngle

        val minAngle = if (balanced) startAngle - angleFactor else startAngle
        val maxAngle = startAngle + angleFactor
        val lw = outerRadius - innerRadius
        r.inset(-lw / 2)
        ctx.setAntiAliasing(true)
        ctx.setLineWidth(lw)
        ctx.setFrameColour(outerTrackColour)
        ctx.drawArc(r, minAngle, maxAngle)

        // A thin line at minAngle

        val xm = (r.right + r.left) / 2
        val ym = (r.bottom + r.top) / 2
        ctx.setAntiAliasing(false)
        ctx.setLineWidth(1)
        ctx.setFrameColour(minMaxLineColour)
        ctx.drawLine(
            (xm + innerRadius * cos(minAngle)).toInt(),
            (ym + innerRadius * sin(minAngle)).toInt(),
            (xm + outerRadius * cos(minAngle)).toInt(),
            (ym + outerRadius * sin(minAngle)).toInt(),
        )

        // A thin line at maxAngle

        ctx.drawLine(
            (xm + innerRadius * cos(maxAngle)).toInt(),
            (ym + innerRadius * sin(maxAngle)).toInt(),
            (xm + outerRadius * cos(maxAngle)).toInt(),
            (ym + outerRadius * sin(maxAngle)).toInt(),
        )

        // A thin line at startAngle (same as minAngle when unbalanced)

        if (balanced) {
            ctx.drawLine(
                (xm + innerRadius * cos(startAngle)).toInt(),
                (ym + innerRadius * sin(startAngle)).toInt(),
                (xm + outerRadius * cos(startAngle)).toInt(),
                (ym + outerRadius * sin(startAngle)).toInt(),
            )
        }

        // A thin line at valueAngle

        val valueAngle = startAngle + angleFactor * (if (balanced) 2 * normValue - 1 else normValue)
        ctx.drawLine(
            (xm + innerRadius * cos(valueAngle)).toInt(),
            (ym + innerRadius * sin(valueAngle)).toInt(),
            (xm + outerRadius * cos(valueAngle)).toInt(),
            (ym + outerRadius * sin(valueAngle)).toInt(),
        )

        // A thick arc from originAngle to valueAngle

        val originAngle = if (inverse) startAngle + angleFactor else startAngle

        if (originAngle != valueAngle) {
            ctx.setLineWidth(lw)
            ctx.setAntiAliasing(true)
            ctx.setFrameColour(valueArcColour)

            when {
                inverse -> ctx.drawArc(r, valueAngle, originAngle)
                startAngle <= valueAngle -> ctx.drawArc(r, originAngle, valueAngle)
                else -> ctx.drawArc(r, valueAngle, originAngle) // otherwise the arc would be drawn around the circle
            }
        }

        // The indicator line

        val indx1 = xm + (indicatorInnerRadius * cos(valueAngle)).toInt()
        val indy1 = ym + (indicatorInnerRadius * sin(valueAngle)).toInt()
        val indx2 = xm + (indicatorOuterRadius * cos(valueAngle)).toInt()
        val indy2 = ym + (indicatorOuterRadius * sin(valueAngle)).toInt()
        ctx.setAntiAliasing(true)
        ctx.setFrameColour(theme.blueGrey300)
        ctx.setLineWidth(2.0f)
        ctx.drawLine(indx1, indy1, indx2, indy2)

        // The frame of the inner circle

        r.set(currentArea).inset(outerRadius - innerRadius)
        ctx.setFillColour(theme.blueGrey400)
        ctx.fillRingWithLinearGradient(
            r,
            thickness = 1,
            theme.knobGradient,
            Point2i((r.left + r.right) / 2, r.top),
            Point2i((r.left + r.right) / 2, r.bottom),
        )
    }

    override fun onMouseDown(evt: MouseEvent): Gadget? {
        if (disabled) {
            return null
        }

        if (evt.alt) {
            setToDefault()
            return null
        }

        dragStartPos.set(evt.pos)
        dragStartValue = normValue
        dragging = true
        beginEdit()
        return this // send mouse up here
    }

    override fun onMouseUp(evt: MouseEvent) {
        if (dragging) {
            endEdit()
            dragging = false
        }
    }

    override fun onMouseMove(evt: MouseEvent) {
        if (dragging) {
            val prevValue = normValue
            val dist = (evt.pos.x - dragStartPos.x) - (evt.pos.y - dragStartPos.y)
            val dragDist = when (param?.stepCount ?: -1) {
                in 1..10 -> DRAG_DISTANCE_FOR_RANGE_WHEN_STEPPED
                else -> DRAG_DISTANCE_FOR_RANGE
            }
            normValue = dragStartValue + dist.toFloat() / dragDist
            clampValueToRange()

            if (normValue != prevValue) {
                valueChanged()
                invalidate()
            }
        }
    }

    override fun onParamChanged(tag: ParamTag, newNormValue: Float) {
        if (tag == this.tag && !dragging && newNormValue != normValue) {
            normValue = newNormValue
            clampValueToRange()
            invalidate()
        }
    }

    private fun setToDefault() {
        val param = param as? Param.RangeParam

        normValue = when {
            balanced -> 0.5f
            param == null -> 0.0f
            else -> (param.defaultValue - param.minValue) / (param.maxValue - param.minValue)
        }

        valueChanged()
        invalidate()
    }

    private fun clampValueToRange() {
        normValue = clamp(normValue, 0.0f, 1.0f)

        (param as? Param.RangeParam)?.let { param ->
            if (param.precision == 0) {
                // Snap to discrete values
                val range = param.maxValue - param.minValue
                val actualValue = param.minValue + normValue * range
                normValue = (floor(actualValue) - param.minValue) / range
            }
        }
    }

    companion object {
        private const val SMALL_OUTER_CIRCLE_RADIUS = 16
        private const val SMALL_INNER_CIRCLE_RADIUS = 12
        private const val SMALL_INDICATOR_OUTER_RADIUS = 11
        private const val SMALL_INDICATOR_INNER_RADIUS = 5

        private const val BIG_OUTER_CIRCLE_RADIUS = 24
        private const val BIG_INNER_CIRCLE_RADIUS = 20
        private const val BIG_INDICATOR_OUTER_RADIUS = 19
        private const val BIG_INDICATOR_INNER_RADIUS = 9

        private const val DRAG_DISTANCE_FOR_RANGE = 200
        private const val DRAG_DISTANCE_FOR_RANGE_WHEN_STEPPED = 80
    }
}
