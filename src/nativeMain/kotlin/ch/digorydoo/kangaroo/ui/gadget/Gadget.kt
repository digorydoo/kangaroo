package ch.digorydoo.kangaroo.ui.gadget

import ch.digorydoo.kangaroo.geometry.MutablePoint2i
import ch.digorydoo.kangaroo.geometry.MutableRecti
import ch.digorydoo.kangaroo.geometry.Point2i
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.event.MouseEvent

abstract class Gadget(
    private val canvas: CanvasView,
    private val origin: MutablePoint2i, // relative to parent
    private val size: Point2i,
) {
    constructor(canvas: CanvasView, left: Int, top: Int, width: Int, height: Int):
        this(canvas, MutablePoint2i(left, top), Point2i(width, height))

    protected open val padding = Recti()
    protected open val intrinsicSize = MutablePoint2i()
    protected open val children = listOf<Gadget?>()
    protected val currentArea = MutableRecti()

    open var hidden = false
    var isMouseInside = false

    val controller get() = canvas.controller
    val theme get() = canvas.theme

    open fun layout(parentOrigin: Point2i, parentSize: Point2i, ctx: DrawContext) {
        val innerOrigin = MutablePoint2i(parentOrigin).add(origin)

        val innerSize = MutablePoint2i(
            when (size.x) {
                PARENT_SIZE -> parentSize.x - origin.x
                CHILD_SIZE -> intrinsicSize.x
                else -> size.x
            },
            when (size.y) {
                PARENT_SIZE -> parentSize.y - origin.y
                CHILD_SIZE -> intrinsicSize.y
                else -> size.y
            }
        )

        innerOrigin.add(padding.left, padding.top)
        innerSize.subtract(padding.left + padding.right, padding.top + padding.bottom)

        var didExtend = false
        var pass = 0

        do {
            pass++

            children.forEach { child ->
                child?.layout(innerOrigin, innerSize, ctx)
            }

            children.forEach { child ->
                if (child != null) {
                    if (size.x == CHILD_SIZE) {
                        if (child.currentArea.right > innerOrigin.x + innerSize.x) {
                            innerSize.x = child.currentArea.right - innerOrigin.x
                            didExtend = true
                        }
                    }
                    if (size.y == CHILD_SIZE) {
                        if (child.currentArea.bottom > innerOrigin.y + innerSize.y) {
                            innerSize.y = child.currentArea.bottom - innerOrigin.y
                            didExtend = true
                        }
                    }
                }
            }
        } while (didExtend && pass < 2)

        innerOrigin.subtract(padding.left, padding.top)
        innerSize.add(padding.left + padding.right, padding.top + padding.bottom)
        currentArea.set(innerOrigin, innerSize)
    }

    abstract fun render(ctx: DrawContext, dirty: Recti)

    protected fun renderChildren(ctx: DrawContext, dirty: Recti) {
        if (hidden) {
            return
        }

        children.forEach { child ->
            if (child != null && !child.hidden && dirty.overlaps(child.currentArea)) {
                ctx.setDefaults()
                child.render(ctx, dirty)
            }
        }
    }

    open fun onMouseDown(evt: MouseEvent): Gadget? {
        if (hidden) {
            return null
        }

        for (i in children.indices) {
            val child = children[i]

            if (child != null && child.currentArea.contains(evt.pos)) {
                val gadget = child.onMouseDown(evt)

                if (gadget != null) {
                    return gadget // event was consumed, mouse up event must go here
                }
            }
        }

        return null
    }

    open fun onDblClick(evt: MouseEvent): Boolean {
        if (hidden) {
            return false
        }

        for (i in children.indices) {
            val child = children[i]

            if (child != null && child.currentArea.contains(evt.pos)) {
                if (child.onDblClick(evt)) {
                    return true // event was consumed
                }
            }
        }

        return false
    }

    fun gatherMouseOver(pos: Point2i, set: MutableSet<Gadget>) {
        if (hidden) {
            return
        }

        if (currentArea.contains(pos)) {
            set.add(this)
        }

        children.forEach { it?.gatherMouseOver(pos, set) }
    }

    open fun onMouseMove(evt: MouseEvent) {
    }

    open fun onMouseEnter(evt: MouseEvent) {
    }

    open fun onMouseLeave(evt: MouseEvent) {
    }

    open fun onMouseUp(evt: MouseEvent) {
    }

    open fun onParamChanged(tag: ParamTag, newNormValue: Float) {
        children.forEach { it?.onParamChanged(tag, newNormValue) }
    }

    fun invalidate() {
        canvas.invalidate(currentArea)
    }

    fun getBitmap(name: String) =
        canvas.getBitmap(name)

    companion object {
        const val PARENT_SIZE = -1
        const val CHILD_SIZE = -2
    }
}
