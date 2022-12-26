package ch.digorydoo.kangaroo.ui.gadget

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext

class ConfirmableBtn(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int,
    caption: String,
    private val onConfirmed: () -> Unit,
): Gadget(canvas, left, top, width, CHILD_SIZE) {
    private val actionBtn = Button(
        canvas,
        left = 0,
        top = 0,
        width = width,
        caption = caption,
        shape = Button.Shape.PILL,
        onClick = { actionBtnClicked() }
    )

    private val confirmBtn = Button(
        canvas,
        left = 0,
        top = 0,
        width = width - CANCEL_BTN_WIDTH - 1,
        caption = "Confirm",
        toggle = true,
        hidden = true,
        shape = Button.Shape.PILL_LEFT_HALF,
        onClick = { confirmBtnClicked() }
    )

    private val cancelBtn = Button(
        canvas,
        left = width - CANCEL_BTN_WIDTH,
        top = 0,
        width = CANCEL_BTN_WIDTH,
        caption = "X",
        hidden = true,
        shape = Button.Shape.PILL_RIGHT_HALF,
        onClick = { cancelBtnClicked() }
    )

    override val children: List<Gadget> = listOf(actionBtn, confirmBtn, cancelBtn)

    override fun render(ctx: DrawContext, dirty: Recti) {
        renderChildren(ctx, dirty)
    }

    private fun actionBtnClicked() {
        actionBtn.hidden = true
        confirmBtn.toggleState = true
        confirmBtn.hidden = false
        cancelBtn.hidden = false
        invalidate()
    }

    private fun cancelBtnClicked() {
        actionBtn.hidden = false
        confirmBtn.hidden = true
        cancelBtn.hidden = true
        invalidate()
    }

    private fun confirmBtnClicked() {
        actionBtn.hidden = false
        confirmBtn.hidden = true
        cancelBtn.hidden = true
        invalidate()
        onConfirmed()
    }

    companion object {
        private const val CANCEL_BTN_WIDTH = 20
    }
}
