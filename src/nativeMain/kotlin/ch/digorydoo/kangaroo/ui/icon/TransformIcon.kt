package ch.digorydoo.kangaroo.ui.icon

import ch.digorydoo.kangaroo.geometry.Point2f
import ch.digorydoo.kangaroo.geometry.Point2i
import ch.digorydoo.kangaroo.ui.Theme

class TransformIcon(theme: Theme): AbstrIcon(theme) {
    override val size = Point2i(9, 9)
    override var colour = theme.blueGrey600

    override val points = listOf(
        Point2f(0.00f, 0.00f),
        Point2f(1.00f, 0.00f),
        Point2f(0.80f, 0.90f),
        Point2f(0.25f, 0.90f),
        Point2f(0.15f, 0.30f),
        Point2f(0.50f, 0.30f),
        Point2f(0.50f, 0.64f),
    )
}
