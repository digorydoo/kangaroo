package ch.digorydoo.kangaroo.ui.gadget

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.param.Param
import ch.digorydoo.kangaroo.param.ParamTag
import ch.digorydoo.kangaroo.param.ParamsList
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import kotlin.math.floor
import kotlin.random.Random

class SurpriseMeBtn(canvas: CanvasView, left: Int, top: Int): Gadget(canvas, left, top, FRAME_WIDTH, CHILD_SIZE) {
    override val children: List<Gadget> = listOf(
        ConfirmableBtn(canvas, 0, 0, FRAME_WIDTH, "Surprise Me", ::onClick)
    )

    override fun render(ctx: DrawContext, dirty: Recti) {
        renderChildren(ctx, dirty)
    }

    private fun onClick() {
        var osc1Volume = Random.nextFloat()
        var osc2Volume = Random.nextFloat()
        var osc3Volume = Random.nextFloat()
        val oscVolSum = osc1Volume + osc2Volume + osc3Volume

        if (oscVolSum < 1.0f) {
            osc1Volume = 1.0f - osc2Volume - osc3Volume
        } else {
            osc1Volume /= oscVolSum
            osc2Volume /= oscVolSum
            osc3Volume /= oscVolSum
        }

        ParamTag.values.forEach { tag ->
            when (tag) {
                ParamTag.MASTER_VOLUME -> Unit
                ParamTag.MOD_WHEEL -> Unit
                ParamTag.PITCH_BEND -> Unit
                ParamTag.AFTER_TOUCH -> Unit
                ParamTag.DAMPER_PEDAL -> Unit
                ParamTag.ALL_NOTES_OFF -> Unit
                ParamTag.OUTPUT_LEFT_PEAK -> Unit
                ParamTag.OUTPUT_RIGHT_PEAK -> Unit
                ParamTag.OSC1_SHAPE -> setParamToRandomValue(tag)
                ParamTag.OSC1_PITCH -> setParamToDefault(tag)
                ParamTag.OSC1_DETUNE -> setParamToDefault(tag)
                ParamTag.OSC1_VOLUME -> controller?.setParamNormalized(tag, osc1Volume)
                ParamTag.OSC2_SHAPE -> setParamToRandomValue(tag)
                ParamTag.OSC2_VOLUME -> controller?.setParamNormalized(tag, osc2Volume)
                ParamTag.OSC2_PITCH -> setParamToDefaultOrRandomValue(tag, 0.8f)
                ParamTag.OSC2_DETUNE -> setParamToDefaultOrRandomValue(tag, 0.8f)
                ParamTag.OSC3_SHAPE -> setParamToRandomValue(tag)
                ParamTag.OSC3_VOLUME -> controller?.setParamNormalized(tag, osc3Volume)
                ParamTag.OSC3_DETUNE -> setParamToDefaultOrRandomValue(tag, 0.9f)
                ParamTag.OSC3_FOLD_OSC1 -> setParamToRandomValue(tag)
                ParamTag.LFO1_SHAPE -> setParamToRandomValue(tag)
                ParamTag.LFO2_SHAPE -> setParamToRandomValue(tag)
                ParamTag.NOISE_SHAPE -> setParamToRandomValue(tag)
                ParamTag.NOISE_VOLUME -> setParamToDefaultOrRandomValue(tag, 0.5f, 0.42f)
                ParamTag.LOW_PASS_FREQ -> setParamToDefault(tag)
                ParamTag.HIGH_PASS_FREQ -> setParamToDefault(tag)
                ParamTag.BAND_PASS_FREQ -> setParamToRandomValue(tag)
                ParamTag.BAND_PASS_AMOUNT -> setParamToRandomValue(tag)
                ParamTag.VOWEL_FORMANT -> setParamToRandomValue(tag)
                ParamTag.VOWEL_AMOUNT -> setParamToRandomValue(tag)
                ParamTag.ENV1_ATTACK -> setParamToRandomValue(tag, 0.1f)
                ParamTag.ENV1_DECAY -> setParamToRandomValue(tag, 0.1f)
                ParamTag.ENV1_SUSTAIN -> setParamToRandomValue(tag)
                ParamTag.ENV1_RELEASE -> setParamToRandomValue(tag, 0.2f)
            }
        }
    }

    private fun setParamToDefault(tag: ParamTag) {
        ParamsList.get(tag)?.let { param ->
            if (!param.readOnly) {
                controller?.setParamNormalized(tag, param.normDefaultValue)
            }
        }
    }

    private fun setParamToDefaultOrRandomValue(tag: ParamTag, toss: Float, maxNormValue: Float = 1.0f) {
        if (Random.nextFloat() <= toss) {
            setParamToDefault(tag)
        } else {
            setParamToRandomValue(tag, maxNormValue)
        }
    }

    private fun setParamToRandomValue(tag: ParamTag, maxNormValue: Float = 1.0f) {
        ParamsList.get(tag)?.let { param ->
            (param as? Param.RangeParam)?.let { rparam ->
                val normValue = if (rparam.stepCount > 0) {
                    floor(Random.nextFloat() * rparam.stepCount) / rparam.stepCount
                } else {
                    Random.nextFloat()
                }
                controller?.setParamNormalized(tag, normValue * maxNormValue)
            }
        }
    }

    companion object {
        private const val FRAME_WIDTH = 80
    }
}
