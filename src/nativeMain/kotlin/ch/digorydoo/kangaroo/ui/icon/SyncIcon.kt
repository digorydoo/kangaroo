package ch.digorydoo.kangaroo.ui.icon

import ch.digorydoo.kangaroo.geometry.Point2f
import ch.digorydoo.kangaroo.geometry.Point2i
import ch.digorydoo.kangaroo.ui.Theme

class SyncIcon(theme: Theme): AbstrIcon(theme) {
    override val size = Point2i(16, 7)
    override var colour = theme.blueGrey600

    override val points = run {
        val ax1 = 0.13f
        val ax2 = 0.94f
        val bx1 = 0.41f
        val bx2 = 0.62f
        val by1 = 0.21f
        val by2 = 0.84f
        val cx1 = 0.27f
        val cx2 = 0.77f
        val cy = 0.55f
        listOf(
            Point2f(bx1, by2),
            Point2f(cx1, 1.0f),
            Point2f(ax1, 1.0f),
            Point2f(0.0f, 0.5f),
            Point2f(ax1, 0.0f),
            Point2f(cx1, 0.0f),
            Point2f(bx1, by1),
            null,
            Point2f(bx2, by2),
            Point2f(cx2, 1.0f),
            Point2f(ax2, 1.0f),
            Point2f(1.0f, 0.5f),
            Point2f(ax2, 0.0f),
            Point2f(cx2, 0.0f),
            Point2f(bx2, by1),
            null,
            Point2f(cx1, cy),
            Point2f(cx2, cy),
        )
    }
}
