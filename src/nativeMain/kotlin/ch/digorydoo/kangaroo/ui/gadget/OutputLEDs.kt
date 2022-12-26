package ch.digorydoo.kangaroo.ui.gadget

import ch.digorydoo.kangaroo.geometry.MutableRecti
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.Colour
import ch.digorydoo.kangaroo.ui.DrawContext
import kotlin.math.abs

class OutputLEDs(canvas: CanvasView, left: Int, top: Int): Gadget(canvas, left, top, FRAME_WIDTH, FRAME_HEIGHT) {
    private var leftPeakValue = 0.0f
    private var rightPeakValue = 0.0f
    private var leftPeakChanged = false
    private val leftLEDs = Array(NUM_LR_LEDS) { 0.0f }
    private val rightLEDs = Array(NUM_LR_LEDS) { 0.0f }
    private val centreLEDs = Array(NUM_CENTRE_LEDS) { 0.0f }

    private fun getLEDColour(intensity: Float) =
        Colour.mix(theme.blueGrey900, theme.accentDark, intensity)

    private fun updateLEDValues() {
        updateLEDValues(leftLEDs, leftPeakValue)
        updateLEDValues(rightLEDs, rightPeakValue)
        updateLEDValues(centreLEDs, (leftPeakValue + rightPeakValue) * 0.5f)
    }

    private fun updateLEDValues(leds: Array<Float>, peak: Float) {
        val last = (leds.size - 1).toFloat()
        val sigs = peak * leds.size

        for (i in leds.indices) {
            val rels = abs(((i + 0.5f) / last - 0.5f) * 2) * leds.size
            leds[i] = when {
                sigs >= rels -> 1.0f
                sigs + 1 <= rels -> 0.0f
                else -> 1.0f - (rels - sigs)
            }
        }
    }

    override fun render(ctx: DrawContext, dirty: Recti) {
        updateLEDValues()

        ctx.setAntiAliasing(true)
        ctx.setFillColour(theme.blueGrey950)
        ctx.fillRoundRect(currentArea, CORNER_SIZE)

        val r = MutableRecti(currentArea).apply {
            left += FRAME_INSET_X + LED_WIDTH + LED_GAP_X
            right = left + LED_WIDTH
            top += FRAME_INSET_Y
            bottom = top + LED_HEIGHT
        }

        centreLEDs.forEach { intensity ->
            ctx.setFillColour(getLEDColour(intensity))
            ctx.fillRect(r)
            r.offset(0, LED_HEIGHT + LED_GAP_Y)
        }

        val topLR = currentArea.top + FRAME_INSET_Y + (LED_HEIGHT + LED_GAP_Y) * (NUM_CENTRE_LEDS - NUM_LR_LEDS) / 2

        r.apply {
            left -= LED_GAP_X + LED_WIDTH
            right = left + LED_WIDTH
            top = topLR
            bottom = top + LED_HEIGHT
        }

        leftLEDs.forEach { intensity ->
            ctx.setFillColour(getLEDColour(intensity))
            ctx.fillRect(r)
            r.offset(0, LED_HEIGHT + LED_GAP_Y)
        }

        r.apply {
            left += 2 * (LED_GAP_X + LED_WIDTH)
            right = left + LED_WIDTH
            top = topLR
            bottom = top + LED_HEIGHT
        }

        rightLEDs.forEach { intensity ->
            ctx.setFillColour(getLEDColour(intensity))
            ctx.fillRect(r)
            r.offset(0, LED_HEIGHT + LED_GAP_Y)
        }
    }

    override fun onParamChanged(tag: ParamTag, newNormValue: Float) {
        // For efficiency reasons, only the right value invalidates the control. We expect both left and right values
        // to always be sent in quick succession anyway.
        when (tag) {
            ParamTag.OUTPUT_LEFT_PEAK -> {
                leftPeakChanged = newNormValue != leftPeakValue
                leftPeakValue = newNormValue
            }
            ParamTag.OUTPUT_RIGHT_PEAK -> {
                if (newNormValue != rightPeakValue || leftPeakChanged) {
                    rightPeakValue = newNormValue
                    leftPeakChanged = false
                    invalidate()
                }
            }
            else -> Unit
        }
    }

    companion object {
        private const val CORNER_SIZE = 8
        private const val FRAME_INSET_X = 21
        private const val FRAME_INSET_Y = 21
        private const val LED_WIDTH = 10
        private const val LED_HEIGHT = 5
        private const val LED_GAP_X = 4
        private const val LED_GAP_Y = 2
        private const val NUM_CENTRE_LEDS = 17
        private const val NUM_LR_LEDS = 13
        private const val FRAME_WIDTH = 3 * LED_WIDTH + 2 * LED_GAP_X + 2 * FRAME_INSET_X
        private const val FRAME_HEIGHT = NUM_CENTRE_LEDS * (LED_HEIGHT + LED_GAP_Y) - LED_GAP_Y + 2 * FRAME_INSET_Y
    }
}
