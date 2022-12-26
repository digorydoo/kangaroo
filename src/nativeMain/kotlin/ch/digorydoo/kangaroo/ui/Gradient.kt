package ch.digorydoo.kangaroo.ui

import interop.cppAddGradientStop
import interop.cppCreateGradient

class Gradient private constructor(val ptr: Long) {
    class Stop(val value: Float, val colour: Colour)

    companion object {
        /**
         * Creates a new gradient. NOTE: The C++ CanvasView keeps all gradients and destroys them in its d'tor, so in
         * order not to leak them, you should create Gradients from Kotlin CanvasView only!
         */
        fun create(viewPtr: Long, vararg stops: Stop): Gradient {
            if (stops.size < 2) {
                throw Exception("There cannot be less than two gradient stops!")
            }

            val gradient = Gradient(
                cppCreateGradient(
                    viewPtr,
                    stops[0].value,
                    stops[0].colour.red,
                    stops[0].colour.green,
                    stops[0].colour.blue,
                    stops[1].value,
                    stops[1].colour.red,
                    stops[1].colour.green,
                    stops[1].colour.blue,
                )
            )

            for (i in 2 until stops.size) {
                val stop = stops[i]
                cppAddGradientStop(
                    gradient.ptr,
                    stop.value,
                    stop.colour.red,
                    stop.colour.green,
                    stop.colour.blue,
                )
            }

            return gradient
        }
    }
}
