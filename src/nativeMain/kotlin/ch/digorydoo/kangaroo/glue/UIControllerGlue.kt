@file:Suppress("unused")

package ch.digorydoo.kangaroo.glue

import ch.digorydoo.kangaroo.param.ParamReadStream
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.param.ParamWriteStream
import ch.digorydoo.kangaroo.ui.UIController

private val controllers = mutableMapOf<Long, UIController>()

fun createUIController(ptr: Long) {
    if (controllers.containsKey(ptr)) {
        println("Error: UIControllerGlue: The map already contains a controller with ptr=$ptr")
    } else {
        controllers[ptr] = UIController(ptr)
    }
}

fun destroyUIController(ptr: Long) {
    if (controllers.containsKey(ptr)) {
        controllers.remove(ptr)
    } else {
        println("Error: UIControllerGlue: The map does not contain any controller with ptr=$ptr")
    }
}

fun linkViewToController(viewPtr: Long, controllerPtr: Long) {
    val canvas = getCanvasView(viewPtr) ?: return
    val ctrl = controllers[controllerPtr] ?: return
    canvas.link(ctrl)
    ctrl.link(canvas)
}

fun uiParamChanged(ptr: Long, tagValue: Int, value: Float) {
    val tag = ParamTag.fromInt(tagValue) ?: return
    controllers[ptr]?.onParamChanged(tag, value)
}

fun uiReadState(controllerPtr: Long, streamPtr: Long): Boolean {
    val ctrl = controllers[controllerPtr] ?: return false
    val stream = ParamReadStream(streamPtr)
    return ctrl.onReadState(stream)
}

fun uiWriteState(controllerPtr: Long, streamPtr: Long): Boolean {
    val ctrl = controllers[controllerPtr] ?: return false
    val stream = ParamWriteStream(streamPtr)
    return ctrl.onWriteState(stream)
}

fun getMidiControllerAssignment(ptr: Long, busIndex: Int, channel: Int, ccNumber: Int): Int {
    val ctrl = controllers[ptr] ?: return -1
    return ctrl.getMidiControllerAssignment(busIndex, channel, ccNumber)
}
