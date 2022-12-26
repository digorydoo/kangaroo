package ch.digorydoo.kangaroo.ui.panel

import ch.digorydoo.kangaroo.geometry.Recti
import ch.digorydoo.kangaroo.ui.CanvasView
import ch.digorydoo.kangaroo.ui.DrawContext
import ch.digorydoo.kangaroo.ui.gadget.Button
import ch.digorydoo.kangaroo.ui.gadget.Gadget
import ch.digorydoo.kangaroo.ui.icon.SyncIcon

class MainPanel(
    canvas: CanvasView,
    left: Int,
    top: Int,
    width: Int,
    height: Int,
): Gadget(canvas, left, top, width, height) {
    override val padding = Recti(24, 24, 24, 0)

    override val children: List<Gadget> = listOf(
        Osc1Panel(canvas, OSC1_PANEL_LEFT, 0, OSC1_PANEL_WIDTH, OSC_GROUP_HEIGHT),
        Osc2Panel(canvas, OSC2_PANEL_LEFT, 0, OSC2_PANEL_WIDTH, OSC_GROUP_HEIGHT),

        Button(
            canvas,
            left = OSC1_PANEL_LEFT + 150,
            top = 42,
            width = 24,
            height = 24,
            toggle = true,
            disabled = true,
            shape = Button.Shape.CIRCULAR,
            icon = SyncIcon(theme),
            onClick = { println("Button: onClick!") },
        ),

        Osc3Panel(canvas, OSC3_PANEL_LEFT, 0, OSC3_PANEL_WIDTH, OSC_GROUP_HEIGHT),
        NoisePanel(canvas, NOISE_PANEL_LEFT, NOISE_PANEL_TOP, NOISE_PANEL_WIDTH, NOISE_PANEL_HEIGHT),

        Lfo1Panel(canvas, LFO1_PANEL_LEFT, LFO1_PANEL_TOP, LFO1_PANEL_WIDTH, LFO1_PANEL_HEIGHT),
        Lfo2Panel(canvas, LFO2_PANEL_LEFT, LFO2_PANEL_TOP, LFO2_PANEL_WIDTH, LFO2_PANEL_HEIGHT),
        Lfo3Panel(canvas, LFO3_PANEL_LEFT, LFO3_PANEL_TOP, LFO3_PANEL_WIDTH, LFO3_PANEL_HEIGHT),

        Env1Panel(canvas, ENV_GROUP_LEFT, ENV_GROUP_TOP, ENV_GROUP_WIDTH, ENV_PANEL_HEIGHT),
        Env2Panel(canvas, ENV_GROUP_LEFT, ENV2_PANEL_TOP, ENV_GROUP_WIDTH, ENV_PANEL_HEIGHT),
        Env3Panel(canvas, ENV_GROUP_LEFT, ENV3_PANEL_TOP, ENV_GROUP_WIDTH, ENV_PANEL_HEIGHT),

        FilterPanel(canvas, FILTER_PANEL_LEFT, FILTER_PANEL_TOP, FILTER_PANEL_WIDTH, FILTER_PANEL_HEIGHT),
        EQPanel(canvas, EQ_PANEL_LEFT, EQ_PANEL_TOP, PARENT_SIZE, EQ_PANEL_HEIGHT),
        EffectPanel(canvas, EFFECT_PANEL_LEFT, EFFECT_PANEL_TOP, PARENT_SIZE, EFFECT_PANEL_HEIGHT),

        LayerPanel(canvas, LAYER_PANEL_LEFT, LAYER_PANEL_TOP, PARENT_SIZE, LAYER_PANEL_HEIGHT),
        PitchPanel(canvas, PITCH_PANEL_LEFT, PITCH_PANEL_TOP, PITCH_PANEL_WIDTH, PITCH_PANEL_HEIGHT),

        TransformPanel(canvas, 0, TRF_PANEL_TOP, PARENT_SIZE, PARENT_SIZE),
    )

    override fun render(ctx: DrawContext, dirty: Recti) {
        ctx.setAntiAliasing(true)
        ctx.setFillColour(theme.blueGrey850)
        ctx.fillRoundRect(
            currentArea,
            corner = CORNER_SIZE,
            hasTopLeftCorner = false,
            hasTopRightCorner = true,
            hasBottomLeftCorner = false,
            hasBottomRightCorner = false
        )
        renderChildren(ctx, dirty)
    }

    companion object {
        private const val CORNER_SIZE = 16
        private const val OSC1_PANEL_LEFT = 0
        private const val OSC1_PANEL_WIDTH = 160
        private const val OSC2_PANEL_LEFT = OSC1_PANEL_LEFT + OSC1_PANEL_WIDTH + 4
        private const val OSC2_PANEL_WIDTH = 160
        private const val OSC3_PANEL_LEFT = OSC2_PANEL_LEFT + OSC2_PANEL_WIDTH + 4
        private const val OSC3_PANEL_WIDTH = 160
        private const val OSC_GROUP_HEIGHT = 145
        private const val NOISE_PANEL_LEFT = OSC3_PANEL_LEFT + OSC3_PANEL_WIDTH + 4
        private const val NOISE_PANEL_TOP = 0
        private const val NOISE_PANEL_WIDTH = 112
        private const val NOISE_PANEL_HEIGHT = 80
        private const val PITCH_PANEL_LEFT = NOISE_PANEL_LEFT + NOISE_PANEL_WIDTH + 4
        private const val PITCH_PANEL_TOP = 0
        private const val PITCH_PANEL_WIDTH = 112
        private const val PITCH_PANEL_HEIGHT = 72
        private const val LAYER_PANEL_TOP = 0
        private const val LAYER_PANEL_HEIGHT = 92
        private const val LAYER_PANEL_LEFT = PITCH_PANEL_LEFT + PITCH_PANEL_WIDTH + 4
        private const val ENV_GROUP_LEFT = 30
        private const val ENV_GROUP_TOP = OSC_GROUP_HEIGHT + 4
        private const val ENV_GROUP_WIDTH = 294
        private const val ENV_PANEL_HEIGHT = 64
        private const val ENV2_PANEL_TOP = ENV_GROUP_TOP + ENV_PANEL_HEIGHT + 4
        private const val ENV3_PANEL_TOP = ENV2_PANEL_TOP + ENV_PANEL_HEIGHT + 4
        private const val LFO1_PANEL_TOP = OSC_GROUP_HEIGHT + 4
        private const val LFO1_PANEL_LEFT = OSC3_PANEL_LEFT
        private const val LFO1_PANEL_WIDTH = 160
        private const val LFO1_PANEL_HEIGHT = 120
        private const val LFO2_PANEL_LEFT = NOISE_PANEL_LEFT
        private const val LFO2_PANEL_TOP = NOISE_PANEL_TOP + NOISE_PANEL_HEIGHT + 4
        private const val LFO2_PANEL_WIDTH = 112
        private const val LFO2_PANEL_HEIGHT = 120
        private const val LFO3_PANEL_LEFT = NOISE_PANEL_LEFT
        private const val LFO3_PANEL_TOP = LFO2_PANEL_TOP + LFO2_PANEL_HEIGHT + 4
        private const val LFO3_PANEL_WIDTH = 112
        private const val LFO3_PANEL_HEIGHT = 96
        private const val FILTER_PANEL_LEFT = PITCH_PANEL_LEFT
        private const val FILTER_PANEL_TOP = PITCH_PANEL_TOP + PITCH_PANEL_HEIGHT + 4
        private const val FILTER_PANEL_WIDTH = 112
        private const val FILTER_PANEL_HEIGHT = 192
        private const val EQ_PANEL_LEFT = LAYER_PANEL_LEFT
        private const val EQ_PANEL_TOP = LAYER_PANEL_TOP + LAYER_PANEL_HEIGHT + 4
        private const val EQ_PANEL_HEIGHT = 80
        private const val EFFECT_PANEL_LEFT = EQ_PANEL_LEFT
        private const val EFFECT_PANEL_TOP = EQ_PANEL_TOP + EQ_PANEL_HEIGHT + 4
        private const val EFFECT_PANEL_HEIGHT = 118
        private const val TRF_PANEL_TOP = ENV3_PANEL_TOP + ENV_PANEL_HEIGHT + 4
    }
}
