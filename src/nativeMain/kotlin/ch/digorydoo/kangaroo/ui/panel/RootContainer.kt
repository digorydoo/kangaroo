package ch.digorydoo.kangaroo.ui.panel

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.gadget.Gadget
import ch.digorydoo.kangaroo.ui.gadget.Keyboard

class RootContainer(canvas: CanvasView): Gadget(canvas, 0, 0, PARENT_SIZE, PARENT_SIZE) {
    private var rightPanel: RightPanel

    override val children: List<Gadget> = listOf(
        TopPanel(canvas, 0, 0, RIGHT_PANEL_LEFT, TOP_PANEL_HEIGHT),
        MainPanel(canvas, 0, MAIN_PANEL_TOP, MAIN_PANEL_WIDTH, MAIN_PANEL_HEIGHT),
        RightPanel(canvas, RIGHT_PANEL_LEFT, 0, PARENT_SIZE, PARENT_SIZE).also { rightPanel = it },
        StatusPanel(canvas, 0, STATUS_PANEL_TOP, MAIN_PANEL_WIDTH, STATUS_PANEL_HEIGHT),
        Keyboard(
            canvas,
            left = 0,
            top = STATUS_PANEL_TOP + STATUS_PANEL_HEIGHT,
            width = MAIN_PANEL_WIDTH,
            height = 32,
            numKeys = 125,
            directToggle = false,
            onKeyPressed = { key ->
                controller?.sendNoteOnEvent(key, 0.8f, 0, 0.0f)
            },
            onKeyReleased = { key ->
                controller?.sendNoteOffEvent(key, 1.0f, 0)
            },
        ),
    )

    override fun render(ctx: DrawContext, dirty: Recti) {
        ctx.setFillColour(theme.blueGrey900)
        ctx.fillRect(currentArea)
        renderChildren(ctx, dirty)
    }

    override fun onParamChanged(tag: ParamTag, newNormValue: Float) {
        // For efficiency reasons, some tags are not bubbled through the UI, but delivered directly.
        when (tag) {
            ParamTag.OUTPUT_LEFT_PEAK -> rightPanel.outputLEDs.onParamChanged(tag, newNormValue)
            ParamTag.OUTPUT_RIGHT_PEAK -> rightPanel.outputLEDs.onParamChanged(tag, newNormValue)
            else -> super.onParamChanged(tag, newNormValue)
        }
    }

    companion object {
        private const val TOP_PANEL_HEIGHT = 64
        private const val MAIN_PANEL_TOP = TOP_PANEL_HEIGHT
        private const val MAIN_PANEL_WIDTH = 884
        private const val MAIN_PANEL_HEIGHT = 408
        private const val RIGHT_PANEL_LEFT = MAIN_PANEL_WIDTH
        private const val STATUS_PANEL_TOP = MAIN_PANEL_TOP + MAIN_PANEL_HEIGHT
        private const val STATUS_PANEL_HEIGHT = 32
    }
}
