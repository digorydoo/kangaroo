package ch.digorydoo.kangaroo.math

/**
 * Signum function for Int. (Kotlin only has Float and Double.)
 * @return: -1, 0 or 1
 */
inline fun sign(value: Int) =
    when {
        value < 0 -> -1
        value > 0 -> 1
        else -> 0
    }

/**
 * Signum function for Float. (Provided for completeness, even though Kotlin has this, too.)
 * @return: -1.0f, 0.0f, or 1.0f
 */
inline fun sign(value: Float) =
    when {
        value < 0.0f -> -1.0f
        value > 0.0f -> 1.0f
        else -> 0.0f
    }

/**
 * Signum function for Double. (Provided for completeness, even though Kotlin has this, too.)
 * @return: -1.0, 0.0, or 1.0
 */
inline fun sign(value: Double) =
    when {
        value < 0.0 -> -1.0
        value > 0.0 -> 1.0
        else -> 0.0
    }

