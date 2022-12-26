package ch.digorydoo.kangaroo.audio.filter

/**
 * See: http://shepazu.github.io/Audio-EQ-Cookbook/audio-eq-cookbook.html
 */
abstract class AbstrFilter(protected val sampleRate: Double) {
    // Parameters of the filter function
    protected var a0 = 0.0
    protected var a1 = 0.0
    protected var a2 = 0.0
    protected var b0 = 0.0
    protected var b1 = 0.0
    protected var b2 = 0.0

    // x[n] is the signal. We need the past values x[n-1] and x[n-2] at each point n.
    private var xpast1 = 0.0
    private var xpast2 = 0.0

    // y[n] is the filter output. We also need y[n-1] and y[n-2].
    private var ypast1 = 0.0
    private var ypast2 = 0.0

    fun evaluate(xn: Double): Double {
        val yn = b0 / a0 * xn + b1 / a0 * xpast1 + b2 / a0 * xpast2 - a1 / a0 * ypast1 - a2 / a0 * ypast2

        xpast2 = xpast1
        xpast1 = xn

        ypast2 = ypast1
        ypast1 = yn

        return yn
    }
}
