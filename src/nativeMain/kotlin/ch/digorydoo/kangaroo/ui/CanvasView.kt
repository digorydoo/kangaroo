package ch.digorydoo.kangaroo.ui

import ch.digorydoo.kangaroo.geometry.MutableRecti
import ch.digorydoo.kangaroo.geometry.Point2i
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.ui.event.KeyboardEvent
import ch.digorydoo.kangaroo.ui.event.MouseEvent
import ch.digorydoo.kangaroo.ui.gadget.Gadget
import ch.digorydoo.kangaroo.ui.panel.RootContainer
import interop.cppGetBitmapCached
import interop.cppInvalidRect

class CanvasView(private val ptr: Long) {
    class Bitmap(val ptr: Long)

    val theme = Theme(ptr)
    var controller: UIController? = null; private set
    private var root: RootContainer? = null
    private val viewRectOfLayout = MutableRecti()
    private var mouseOver = mutableSetOf<Gadget>()
    private var mouseCapture: Gadget? = null

    fun link(c: UIController) {
        if (controller != null) {
            println("CanvasView: link: Error: There is already a UIController!")
        } else {
            println("CanvasView: Linking to $c")
            controller = c
        }

        root = RootContainer(this)
    }

    fun invalidate(r: Recti) {
        cppInvalidRect(ptr, r.left, r.top, r.right, r.bottom)
    }

    fun getBitmap(name: String): Bitmap? {
        return cppGetBitmapCached(ptr, name)
            .takeIf { it != 0L }
            ?.let { Bitmap(it) }
    }

    fun onSized(viewRect: Recti) {
        println("onSized($viewRect)")
    }

    fun onParamChanged(tag: ParamTag, newNormValue: Float) {
        root?.onParamChanged(tag, newNormValue)
    }

    fun onPaint(ctx: DrawContext, viewRect: Recti, dirty: Recti) {
        try {
            if (!viewRectOfLayout.isSame(viewRect)) {
                println("viewRect changed")
                root?.layout(Point2i(viewRect.left, viewRect.top), Point2i(viewRect.width, viewRect.height), ctx)
                viewRectOfLayout.set(viewRect)
            }

            root?.render(ctx, dirty)
        } catch (e: Exception) {
            println("Error: An exception occurred in CanvasView.onRedraw: $e\n${e.stackTraceToString()}")
        }
    }

    fun onMouseEvent(evt: MouseEvent) {
        when (evt.kind) {
            MouseEvent.Kind.DOWN -> {
                // Mouse capture is implicitly set to the gadget that consumes the onMouseDown event.
                // We call onMouseDown even for double-clicks (or triple clicks), because ignoring the
                // click doesn't feel right for controls that don't implement double-clicks.
                mouseCapture = root?.onMouseDown(evt)

                if (evt.clickCount == 2) {
                    root?.onDblClick(evt)
                }
            }
            MouseEvent.Kind.MOVE -> {
                // Mouse move directly goes to mouseCapture while the mouse button is held down.

                if (mouseCapture != null) {
                    mouseCapture?.onMouseMove(evt)
                } else {
                    handleEnterLeave(evt) // sets mouseOver anew
                    mouseOver.forEach { it.onMouseMove(evt) }
                }
            }
            MouseEvent.Kind.UP -> {
                mouseCapture?.onMouseUp(evt)
                mouseCapture = null
                handleEnterLeave(evt) // because it was withheld by the capture
            }
            MouseEvent.Kind.CANCEL -> {
                mouseCapture?.onMouseUp(evt) // probably OK to treat this like up
                mouseCapture = null
            }
        }
    }

    private fun handleEnterLeave(evt: MouseEvent) {
        val newMouseOver = mutableSetOf<Gadget>()
        root?.gatherMouseOver(evt.pos, newMouseOver)

        mouseOver.forEach { gadget ->
            if (!newMouseOver.contains(gadget)) {
                gadget.isMouseInside = false
                gadget.onMouseLeave(evt)
            }
        }

        newMouseOver.forEach { gadget ->
            if (!mouseOver.contains(gadget)) {
                gadget.isMouseInside = true
                gadget.onMouseEnter(evt)
            }
        }

        mouseOver = newMouseOver
    }

    fun onKeyboardEvent(evt: KeyboardEvent): Boolean {
        println("onKeyboardEvent $evt")
        return false
    }
}
