@file:Suppress("unused")

package ch.digorydoo.kangaroo.math

/**
 * Max function with vararg parameter. (Kotlin max has only two arguments.)
 * @return: The maximum value
 */
inline fun max(vararg args: Int): Int {
    var result = Int.MIN_VALUE

    for (a in args) {
        if (a > result) {
            result = a
        }
    }

    return result
}

/**
 * Max function with vararg parameter. (Kotlin max has only two arguments.)
 * @return: The maximum value
 */
inline fun max(vararg args: Float): Float {
    var result = Float.NEGATIVE_INFINITY // NOT Float.MIN_VALUE

    for (a in args) {
        if (a > result) {
            result = a
        }
    }

    return result
}

/**
 * Min function with vararg parameter. (Kotlin min has only two arguments.)
 * @return: The minimum value
 */
inline fun min(vararg args: Int): Int {
    var result = Int.MAX_VALUE

    for (a in args) {
        if (a < result) {
            result = a
        }
    }

    return result
}

/**
 * Min function with vararg parameter. (Kotlin min has only two arguments.)
 * @return: The minimum value
 */
inline fun min(vararg args: Float): Float {
    var result = Float.MAX_VALUE

    for (a in args) {
        if (a < result) {
            result = a
        }
    }

    return result
}
