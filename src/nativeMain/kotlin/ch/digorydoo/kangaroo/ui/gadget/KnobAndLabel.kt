package ch.digorydoo.kangaroo.ui.gadget

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.math.toPrecision
import ch.digorydoo.kangaroo.param.Param
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.param.ParamsList
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.DrawContext.TextAlign

class KnobAndLabel(
    canvas: CanvasView,
    left: Int,
    top: Int,
    private val title: String,
    big: Boolean = false,
    dark: Boolean = false,
    inverse: Boolean = false,
    disabled: Boolean = false,
    private val tag: ParamTag? = null,
): Gadget(canvas, left, top, CHILD_SIZE, CHILD_SIZE) {
    private val label = Label(
        canvas,
        left = 0,
        top = 0,
        width = if (big) 64 else 48,
        text = title,
        size = Label.Size.TINY,
        align = TextAlign.CENTRED,
    )

    private val knob = Knob(
        canvas,
        left = 8,
        top = 16,
        big = big,
        dark = dark,
        inverse = inverse,
        disabled = disabled,
        tag = tag,
        beginEdit = ::beginEdit,
        endEdit = ::endEdit,
        valueChanged = ::valueChanged
    )

    override val children: List<Gadget> = listOf(label, knob)
    private var isEditing = false

    private fun setLabel(text: String) {
        label.text = text
        label.invalidate()
    }

    private fun setLabelFromValue(normValue: Float) {
        val tag = tag ?: return
        val param = ParamsList.get(tag) as? Param.RangeParam ?: return
        val actualValue = param.minValue + normValue * (param.maxValue - param.minValue)
        val formatted = actualValue.toPrecision(param.precision)
        val units = param.units ?: ""
        setLabel("$formatted $units".trim())
    }

    private fun beginEdit() {
        val tag = tag ?: return
        controller?.beginParamEdit(tag)
        setLabelFromValue(knob.normValue)
        isEditing = true
    }

    private fun endEdit() {
        val tag = tag ?: return
        controller?.endParamEdit(tag)
        setLabel(title)
        isEditing = false
    }

    private fun valueChanged() {
        val tag = tag ?: return
        controller?.setParamNormalized(tag, knob.normValue)
        if (isEditing) setLabelFromValue(knob.normValue)
    }

    override fun render(ctx: DrawContext, dirty: Recti) {
        renderChildren(ctx, dirty)
    }
}
