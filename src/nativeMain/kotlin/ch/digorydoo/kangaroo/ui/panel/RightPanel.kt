package ch.digorydoo.kangaroo.ui.panel

import ch.digorydoo.kangaroo.audio.CCNumber
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.gadget.*

class RightPanel(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int,
    height: Int,
): Gadget(canvas, left, top, width, height) {
    var outputLEDs: OutputLEDs; private set

    override val children: List<Gadget> = listOf(
        KnobAndLabel(canvas, 64, 24, "Volume", dark = true, big = false, tag = ParamTag.MASTER_VOLUME),

        OutputLEDs(canvas, 24, 80).also { outputLEDs = it },

        Button(canvas, 40, 256, width = 48, caption = "Panic !") {
            controller?.sendMIDICCOutEvent(CCNumber.AllSoundsOff)
        },

        KnobAndLabel(canvas, 16, 300, "Drift", dark = true, disabled = true),
        KnobAndLabel(canvas, 64, 300, "Width", dark = true, disabled = true),

        KnobAndLabel(canvas, 16, 356, "Crunch", dark = true, disabled = true),
        KnobAndLabel(canvas, 64, 356, "Punch", dark = true, disabled = true),

        Label(canvas, 24, 474, text = "Jihad", size = Label.Size.TINY),
        Keyboard(
            canvas,
            left = 24,
            top = 492,
            width = 64,
            height = 24,
            numKeys = 12,
            directToggle = true,
            onKeyPressed = { println("Arab keys CLICK key=$it") },
        ),
    )

    override fun render(ctx: DrawContext, dirty: Recti) {
        renderChildren(ctx, dirty)
    }
}
