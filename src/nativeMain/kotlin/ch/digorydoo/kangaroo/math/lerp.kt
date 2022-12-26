package ch.digorydoo.kangaroo.math

/**
 * Linear interpolation or extrapolation
 * @param from
 * @param to
 * @param rel: use [0..1] for interpolation, values outside for extrapolation
 * @return from if rel is 0, to if rel is 1
 */
inline fun lerp(from: Double, to: Double, rel: Double) =
    (1.0 - rel) * from + rel * to

/**
 * Linear interpolation or extrapolation
 * @param from
 * @param to
 * @param rel: use [0..1] for interpolation, values outside for extrapolation
 * @return from if rel is 0, to if rel is 1
 */
inline fun lerp(from: Float, to: Float, rel: Float) =
    (1.0f - rel) * from + rel * to
