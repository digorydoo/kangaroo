@file:Suppress("unused")

package ch.digorydoo.kangaroo.glue

import ch.digorydoo.kangaroo.geometry.Point2i
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.event.KeyboardEvent
import ch.digorydoo.kangaroo.ui.event.MouseEvent

private val canvasViews = mutableMapOf<Long, CanvasView>()

fun getCanvasView(ptr: Long) =
    canvasViews[ptr]

fun createCanvasView(ptr: Long) {
    if (canvasViews.containsKey(ptr)) {
        println("Error: CanvasViewGlue: The map already contains a canvas with ptr=$ptr")
    } else {
        canvasViews[ptr] = CanvasView(ptr)
    }
}

fun destroyCanvasView(ptr: Long) {
    if (canvasViews.containsKey(ptr)) {
        val canvas = canvasViews[ptr]
        canvas?.controller?.unlink(canvas)
        canvasViews.remove(ptr)
    } else {
        println("Error: CanvasViewGlue: The map does not contain any canvas with ptr=$ptr")
    }
}

fun uiSetViewSize(ptr: Long, left: Int, top: Int, right: Int, bottom: Int) {
    canvasViews[ptr]?.onSized(Recti(left, top, right, bottom))
}

fun uiOnPaint(
    viewPtr: Long,
    ctxPtr: Long,
    viewLeft: Int,
    viewTop: Int,
    viewRight: Int,
    viewBottom: Int,
    dirtyLeft: Int,
    dirtyTop: Int,
    dirtyRight: Int,
    dirtyBottom: Int
) {
    val canvas = canvasViews[viewPtr] ?: return
    // We always create a new instance of DrawContext. The instance shouldn't keep state across calls.
    val ctx = DrawContext(ctxPtr)
    ctx.setDefaults() // make sure the context has a defined state on each redraw
    val viewRect = Recti(viewLeft, viewTop, viewRight, viewBottom)
    val dirtyRect = Recti(dirtyLeft, dirtyTop, dirtyRight, dirtyBottom)
    canvas.onPaint(ctx, viewRect, dirtyRect)
}

fun uiOnMouseDownEvent(ptr: Long, x: Int, y: Int, clickCount: Int, shift: Boolean, alt: Boolean) {
    canvasViews[ptr]?.onMouseEvent(MouseEvent(MouseEvent.Kind.DOWN, Point2i(x, y), shift, alt, clickCount))
}

fun uiOnMouseMoveEvent(ptr: Long, x: Int, y: Int, shift: Boolean, alt: Boolean) {
    canvasViews[ptr]?.onMouseEvent(MouseEvent(MouseEvent.Kind.MOVE, Point2i(x, y), shift, alt))
}

fun uiOnMouseUpEvent(ptr: Long, x: Int, y: Int, shift: Boolean, alt: Boolean) {
    canvasViews[ptr]?.onMouseEvent(MouseEvent(MouseEvent.Kind.UP, Point2i(x, y), shift, alt))
}

fun uiOnMouseCancelEvent(ptr: Long) {
    canvasViews[ptr]?.onMouseEvent(MouseEvent(MouseEvent.Kind.CANCEL, Point2i()))
}

fun uiOnKeyboardEvent(
    ptr: Long,
    c: Char,
    vkey: Int,
    down: Boolean,
    repeat: Boolean,
    shift: Boolean,
    alt: Boolean
): Boolean {
    val canvas = canvasViews[ptr] ?: return false
    val kind = if (down) KeyboardEvent.Kind.DOWN else KeyboardEvent.Kind.UP
    val evt = KeyboardEvent(kind, c, vkey, repeat, shift, alt)
    return canvas.onKeyboardEvent(evt)
}
