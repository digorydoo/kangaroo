package ch.digorydoo.kangaroo.ui

import ch.digorydoo.kangaroo.geometry.MutablePoint2i
import ch.digorydoo.kangaroo.geometry.Point2f
import ch.digorydoo.kangaroo.geometry.Point2i
import ch.digorydoo.kangaroo.geometry.Recti
import interop.*
import kotlin.math.ceil
import kotlin.math.floor

@Suppress("unused", "MemberVisibilityCanBePrivate")
class DrawContext(private val ptr: Long) {
    enum class TextAlign { LEFT, RIGHT, CENTRED }
    class FontProps(val ascent: Int, val descent: Int, val height: Int)

    private val connectingPt = MutablePoint2i()

    fun setDefaults() {
        setGlobalAlpha(1.0f)
        setLineWidth(1.0f)
        setAntiAliasing(true)
        setFrameColour(Colour.white)
        setFillColour(Colour.black)
        setFontColour(Colour.white)
        setFontSize(12.0f)
    }

    fun setGlobalAlpha(alpha: Float) {
        cppSetGlobalAlpha(ptr, alpha)
    }

    fun setLineWidth(width: Float) {
        cppSetLineWidth(ptr, width)
    }

    fun setLineWidth(width: Int) {
        cppSetLineWidth(ptr, width.toFloat())
    }

    fun setAntiAliasing(enabled: Boolean) {
        cppSetAntiAliasing(ptr, if (enabled) 1 else 0)
    }

    fun setFrameColour(c: Colour) {
        cppSetFrameColor(ptr, c.red, c.green, c.blue)
    }

    fun setFillColour(c: Colour) {
        cppSetFillColor(ptr, c.red, c.green, c.blue)
    }

    fun setFontColour(c: Colour) {
        cppSetFontColor(ptr, c.red, c.green, c.blue)
    }

    fun setFontSize(size: Float) {
        cppSetFontSize(ptr, size)
    }

    // TODO call cpp to get the accurate values
    fun getFontProps(fontSize: Float) = FontProps(
        ascent = (fontSize / 11 * 10).toInt(),
        descent = (fontSize / 11 * 4).toInt(),
        height = ceil(fontSize).toInt() + 2,
    )

    fun drawArc(bounds: Recti, startAngle: Float, endAngle: Float) {
        cppDrawArc(ptr, bounds.left, bounds.top, bounds.right, bounds.bottom, startAngle, endAngle)
    }

    fun drawPixel(x: Int, y: Int, opacity: Float = 1.0f) {
        cppDrawPixel(ptr, x, y, opacity)
    }

    fun drawSubpixel(x: Float, y: Float) {
        val x0 = floor(x).toInt()
        val y0 = floor(y).toInt()
        val rx1 = x - x0 // in 0..<1
        val ry1 = y - y0 // in 0..<1
        val x1 = x0 + 1
        val y1 = y0 + 1
        val rx0 = 1.0f - rx1
        val ry0 = 1.0f - ry1
        cppDrawPixel(ptr, x0, y0, rx0 * ry0)
        cppDrawPixel(ptr, x1, y0, rx1 * ry0)
        cppDrawPixel(ptr, x0, y1, rx0 * ry1)
        cppDrawPixel(ptr, x1, y1, rx1 * ry1)
    }

    fun drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {
        cppDrawLine(ptr, x1, y1, x2, y2)
        connectingPt.set(x2, y2)
    }

    fun moveTo(x: Int, y: Int) {
        connectingPt.set(x, y)
    }

    fun moveTo(pt: Point2i) {
        connectingPt.set(pt)
    }

    fun lineTo(x2: Int, y2: Int) {
        cppDrawLine(ptr, connectingPt.x, connectingPt.y, x2, y2)
        connectingPt.set(x2, y2)
    }

    fun lineTo(pt: Point2i) {
        cppDrawLine(ptr, connectingPt.x, connectingPt.y, pt.x, pt.y)
        connectingPt.set(pt)
    }

    fun drawLines(points: List<Point2f?>, offset: Point2i, scale: Point2f? = null) {
        drawLines(points, offset.x, offset.y, scale?.x ?: 1.0f, scale?.y ?: 1.0f)
    }

    fun drawLines(points: List<Point2f?>, offset: Point2i, scale: Point2i? = null) {
        drawLines(points, offset.x, offset.y, scale?.x?.toFloat() ?: 1.0f, scale?.y?.toFloat() ?: 1.0f)
    }

    fun drawLines(points: List<Point2f?>, offsetX: Int, offsetY: Int, scaleX: Int, scaleY: Int) {
        drawLines(points, offsetX, offsetY, scaleX.toFloat(), scaleY.toFloat())
    }

    fun drawLines(
        points: List<Point2f?>,
        offsetX: Int = 0,
        offsetY: Int = 0,
        scaleX: Float = 1.0f,
        scaleY: Float = 1.0f
    ) {
        var nextIsMove = true

        for (pt in points) {
            if (pt == null) {
                nextIsMove = true
            } else {
                val x = offsetX + (scaleX * pt.x).toInt()
                val y = offsetY + (scaleY * pt.y).toInt()

                if (nextIsMove) {
                    moveTo(x, y)
                    nextIsMove = false
                } else {
                    lineTo(x, y)
                }
            }
        }
    }

    fun drawRect(bounds: Recti) {
        cppDrawRect(ptr, bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    fun drawRoundRect(
        bounds: Recti,
        corner: Int,
        hasTopLeftCorner: Boolean = true,
        hasTopRightCorner: Boolean = true,
        hasBottomLeftCorner: Boolean = true,
        hasBottomRightCorner: Boolean = true,
    ) = drawRoundRect(
        bounds,
        corner,
        corner,
        hasTopLeftCorner,
        hasTopRightCorner,
        hasBottomLeftCorner,
        hasBottomRightCorner,
    )

    fun drawRoundRect(
        bounds: Recti,
        cornerX: Int,
        cornerY: Int,
        hasTopLeftCorner: Boolean = true,
        hasTopRightCorner: Boolean = true,
        hasBottomLeftCorner: Boolean = true,
        hasBottomRightCorner: Boolean = true,
    ) {
        cppDrawRoundRect(
            ptr,
            bounds.left,
            bounds.top,
            bounds.right,
            bounds.bottom,
            cornerX,
            cornerY,
            if (hasTopLeftCorner) 1 else 0,
            if (hasTopRightCorner) 1 else 0,
            if (hasBottomLeftCorner) 1 else 0,
            if (hasBottomRightCorner) 1 else 0,
        )
    }

    fun fillRoundRect(
        bounds: Recti,
        corner: Int,
        hasTopLeftCorner: Boolean = true,
        hasTopRightCorner: Boolean = true,
        hasBottomLeftCorner: Boolean = true,
        hasBottomRightCorner: Boolean = true,
    ) = fillRoundRect(
        bounds,
        corner,
        corner,
        hasTopLeftCorner,
        hasTopRightCorner,
        hasBottomLeftCorner,
        hasBottomRightCorner,
    )

    fun fillRoundRect(
        bounds: Recti,
        cornerX: Int,
        cornerY: Int,
        hasTopLeftCorner: Boolean = true,
        hasTopRightCorner: Boolean = true,
        hasBottomLeftCorner: Boolean = true,
        hasBottomRightCorner: Boolean = true,
    ) {
        cppFillRoundRect(
            ptr,
            bounds.left,
            bounds.top,
            bounds.right,
            bounds.bottom,
            cornerX,
            cornerY,
            if (hasTopLeftCorner) 1 else 0,
            if (hasTopRightCorner) 1 else 0,
            if (hasBottomLeftCorner) 1 else 0,
            if (hasBottomRightCorner) 1 else 0,
        )
    }

    fun drawEllipse(bounds: Recti) {
        cppDrawEllipse(ptr, bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    fun drawText(s: String, x: Int, y: Int) {
        cppDrawText(ptr, s, x, y)
    }

    fun drawText(s: String, x: Int, y: Int, align: TextAlign) {
        val xoff = when (align) {
            TextAlign.LEFT -> 0
            TextAlign.RIGHT -> -cppMeasureText(ptr, s)
            TextAlign.CENTRED -> -(cppMeasureText(ptr, s) / 2)
        }
        cppDrawText(ptr, s, x + xoff, y)
    }

    fun measureText(s: String) =
        cppMeasureText(ptr, s)

    fun fillRect(bounds: Recti) {
        cppFillRect(ptr, bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    fun fillEllipse(bounds: Recti) {
        cppFillEllipse(ptr, bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    fun fillRing(bounds: Recti, thickness: Int) {
        cppFillRing(ptr, bounds.left, bounds.top, bounds.right, bounds.bottom, thickness)
    }

    fun fillRingWithLinearGradient(bounds: Recti, thickness: Int, gradient: Gradient, start: Point2i, end: Point2i) {
        cppFillRingWithLinearGradient(
            ptr,
            bounds.left,
            bounds.top,
            bounds.right,
            bounds.bottom,
            thickness,
            gradient.ptr,
            start.x,
            start.y,
            end.x,
            end.y,
        )
    }

    fun drawBitmap(bmp: CanvasView.Bitmap, bounds: Recti) {
        cppDrawBitmap(ptr, bmp.ptr, bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    fun setClipRect(bounds: Recti) {
        cppSetClipRect(ptr, bounds.left, bounds.top, bounds.right, bounds.bottom)
    }

    fun resetClipRect() {
        cppResetClipRect(ptr)
    }

    override fun toString() =
        "DrawContext($ptr)"
}
