package ch.digorydoo.kangaroo.ui.icon

import ch.digorydoo.kangaroo.geometry.MutableRecti
import ch.digorydoo.kangaroo.geometry.Point2f
import ch.digorydoo.kangaroo.geometry.Point2i
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.Theme

abstract class AbstrIcon(theme: Theme) {
    open var colour = theme.blueGrey300
    protected abstract val points: List<Point2f?>
    open val size = Point2i(8, 8)
    private val bounds = MutableRecti()

    fun setOrigin(originX: Int, originY: Int) {
        bounds.set(originX, originY, originX + size.x, originY + size.y)
    }

    fun render(ctx: DrawContext, dirty: Recti) {
        if (dirty.overlaps(bounds)) {
            ctx.setAntiAliasing(true)
            ctx.setLineWidth(1)
            ctx.setFrameColour(colour)
            // Since points uses 1.0 to represent the right/bottom end, we need to subtract size by 1.
            ctx.drawLines(points, bounds.origin.x, bounds.origin.y, size.x - 1, size.y - 1)
        }
    }
}
