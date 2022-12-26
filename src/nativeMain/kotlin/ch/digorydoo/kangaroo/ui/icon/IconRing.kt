package ch.digorydoo.kangaroo.ui.icon

import ch.digorydoo.kangaroo.geometry.Point2f
import ch.digorydoo.kangaroo.geometry.Point2i
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.math.lerp
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.gadget.Gadget
import kotlin.math.*

class IconRing(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int,
    height: Int,
    private val icons: List<AbstrIcon>,
): Gadget(canvas, left, top, width, height) {
    private var radius = 0.0f
    private val startAngle = (0.85 * PI).toFloat()
    private val endAngle = (2.15 * PI).toFloat()

    override fun layout(parentOrigin: Point2i, parentSize: Point2i, ctx: DrawContext) {
        super.layout(parentOrigin, parentSize, ctx)

        val maxIconSize = icons.fold(0) { result, icon ->
            val diagonal = ceil(Point2f().distanceTo(icon.size)).toInt()
            max(diagonal, result)
        }

        val centre = currentArea.centre()
        val mySize = min(currentArea.width, currentArea.height)
        radius = mySize / 2.0f - maxIconSize / 2.0f

        icons.forEachIndexed { idx, icon ->
            val rel = idx.toFloat() / (icons.size - 1)
            val phi = lerp(startAngle, endAngle, rel)
            val x = radius * cos(phi) + centre.x - icon.size.x / 2.0f
            val y = radius * sin(phi) + centre.y - icon.size.y / 2.0f
            icon.setOrigin(x.toInt(), y.toInt())
        }
    }

    override fun render(ctx: DrawContext, dirty: Recti) {
        icons.forEach { it.render(ctx, dirty) }
    }
}
