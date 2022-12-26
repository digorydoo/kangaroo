package ch.digorydoo.kangaroo.ui.gadget

import ch.digorydoo.kangaroo.geometry.MutablePoint2i
import ch.digorydoo.kangaroo.geometry.MutableRecti
import ch.digorydoo.kangaroo.geometry.Point2i
import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.event.MouseEvent
import kotlin.math.ceil
import kotlin.math.max

class Keyboard(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int,
    height: Int,
    private val numKeys: Int,
    private val directToggle: Boolean,
    private val onKeyPressed: (key: Int) -> Unit,
    private val onKeyReleased: ((key: Int) -> Unit)? = null,
): Gadget(canvas, left, top, width, height) {
    private inner class Key(val idx: Int) {
        val bounds = MutableRecti()
        var isWhite = true
        val isBlack get() = !isWhite
        val isHilited get() = keysHilited.contains(idx)

        fun render(ctx: DrawContext) {
            when {
                isHilited -> {
                    ctx.setFillColour(theme.accent)
                    ctx.fillRect(bounds)

                    ctx.setFrameColour(theme.accentDarker)
                    ctx.drawLine(bounds.left, bounds.top, bounds.left, bounds.bottom - 1)
                    ctx.drawLine(bounds.left, bounds.bottom - 1, bounds.right - 1, bounds.bottom - 1)

                    if (!isWhite) {
                        ctx.drawLine(bounds.right - 1, bounds.top, bounds.right - 1, bounds.bottom - 1)
                    }
                }
                isWhite -> {
                    ctx.setFillColour(theme.blueGrey300)
                    ctx.fillRect(bounds)

                    ctx.setFrameColour(theme.blueGrey500)
                    ctx.drawLine(bounds.left, bounds.top, bounds.left, bounds.bottom - 1)
                    ctx.drawLine(bounds.left + 1, bounds.top, bounds.right - 1, bounds.top)

                    ctx.setFrameColour(theme.blueGrey400)
                    ctx.drawLine(bounds.left + 1, bounds.bottom - 1, bounds.right - 1, bounds.bottom - 1)
                }
                else -> {
                    ctx.setFillColour(theme.blueGrey900)
                    ctx.fillRect(bounds)

                    ctx.setFrameColour(theme.blueGrey850)
                    ctx.drawLine(bounds.left, bounds.top + 1, bounds.left, bounds.bottom - 1)

                    ctx.setFrameColour(theme.blueGrey800)
                    ctx.drawLine(bounds.left + 1, bounds.top + 1, bounds.left + 1, bounds.bottom - 2)

                    ctx.setFrameColour(theme.blueGrey600)
                    ctx.drawLine(bounds.left + 1, bounds.bottom - 1, bounds.right - 2, bounds.bottom - 1)

                    ctx.setFrameColour(theme.blueGrey700)
                    ctx.drawLine(bounds.left + 2, bounds.bottom - 2, bounds.right - 3, bounds.bottom - 2)

                    ctx.setFrameColour(theme.blueGrey750)
                    ctx.drawLine(bounds.right - 2, bounds.top + 1, bounds.right - 2, bounds.bottom - 2)

                    ctx.setFrameColour(theme.blueGrey850)
                    ctx.drawLine(bounds.right - 1, bounds.top + 1, bounds.right - 1, bounds.bottom - 1)
                }
            }
        }
    }

    override val padding = if (numKeys > 12) Recti(8, 0, 8, 4) else Recti()
    override val intrinsicSize = MutablePoint2i(256, 32)
    private val keys = Array(numKeys) { Key(it) }
    private val keysHilited = mutableSetOf<Int>()
    private var keyUpExpected = -1
    private var ignoreNextMouseUp = false

    override fun layout(parentOrigin: Point2i, parentSize: Point2i, ctx: DrawContext) {
        super.layout(parentOrigin, parentSize, ctx)

        val availWidth = currentArea.width - padding.left - padding.right - 1
        val availHeight = currentArea.height - padding.top - padding.bottom - 1
        val numOctaves: Int = numKeys / 12
        val numBeyond = numKeys - numOctaves * 12
        val offset = if (numBeyond >= 6) 1 else 0
        val numWhiteKeys = numOctaves * 7 + ceil((numBeyond.toFloat() + offset) / 2).toInt()
        val whiteKeyWidth = max(1, availWidth / numWhiteKeys)
        val blackKeyWidth = whiteKeyWidth / 2 + 3
        val leftGap = padding.left + max(0, (availWidth - whiteKeyWidth * numWhiteKeys) / 2)
        val blackKeyOffset = whiteKeyWidth - max(1, blackKeyWidth / 2)
        val whiteKeyBottom = currentArea.top + availHeight
        val blackKeyBottom = whiteKeyBottom - (2.0f * availHeight / 5.0f).toInt()
        val octaveWidth = 7 * whiteKeyWidth

        for (key in keys) {
            val octave = key.idx / 12
            val whiteIdx = whiteKeyIndexes.indexOf(key.idx % 12)

            if (whiteIdx >= 0) {
                val x = currentArea.left + leftGap + octave * octaveWidth + whiteIdx * whiteKeyWidth
                key.bounds.set(x, currentArea.top + padding.top, x + whiteKeyWidth, whiteKeyBottom)
                key.isWhite = true
            } else {
                var b = (key.idx % 12) - 1
                if (b > 3) b++
                b /= 2
                val x = currentArea.left + leftGap + octave * octaveWidth + blackKeyOffset + b * whiteKeyWidth
                key.bounds.set(x, currentArea.top + padding.top, x + blackKeyWidth, blackKeyBottom)
                key.isWhite = false
            }
        }
    }

    override fun render(ctx: DrawContext, dirty: Recti) {
        ctx.setAntiAliasing(false) // otherwise hairlines won't be hairlines
        ctx.setLineWidth(1.0f)

        ctx.setFillColour(theme.blueGrey920)
        ctx.fillRect(currentArea)

        keys.forEach {
            if (it.isWhite && it.bounds.overlaps(dirty)) it.render(ctx)
        }

        keys.forEach {
            if (it.isBlack && it.bounds.overlaps(dirty)) it.render(ctx)
        }

        ctx.setFrameColour(theme.blueGrey500)
        val lastKeyBounds = keys[keys.size - 1].bounds
        ctx.drawLine(lastKeyBounds.right, lastKeyBounds.top, lastKeyBounds.right, lastKeyBounds.bottom - 1)
    }

    override fun onMouseDown(evt: MouseEvent): Gadget? {
        for (key in keys) {
            if (key.bounds.contains(evt.pos)) {
                if (directToggle) {
                    if (keysHilited.contains(key.idx)) {
                        keysHilited.remove(key.idx)
                    } else {
                        keysHilited.add(key.idx)
                    }
                    invalidate()
                }

                onKeyPressed(key.idx)
                keyUpExpected = key.idx
                return this // send onMouseUp to this
            }
        }

        return null
    }

    override fun onDblClick(evt: MouseEvent): Boolean {
        if (!directToggle) {
            // We intentionally let the note "hang" on double click.
            ignoreNextMouseUp = true
        }
        return true
    }

    override fun onMouseUp(evt: MouseEvent) {
        if (ignoreNextMouseUp) {
            ignoreNextMouseUp = false
        } else {
            onKeyReleased?.invoke(keyUpExpected)
        }
    }

    companion object {
        private val whiteKeyIndexes = arrayOf(0, 2, 4, 5, 7, 9, 11)
    }
}
