package ch.digorydoo.kangaroo.param

abstract class ParamStream {
    companion object {
        const val END_MARKER = 32767 // There must not be any ParamTag with this value!
    }
}
