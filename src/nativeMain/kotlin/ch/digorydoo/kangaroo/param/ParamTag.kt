package ch.digorydoo.kangaroo.param

enum class ParamTag(val value: Int) {
    // Global parameters
    MASTER_VOLUME(1),
    MOD_WHEEL(2),
    PITCH_BEND(3),
    AFTER_TOUCH(4),
    DAMPER_PEDAL(5),
    ALL_NOTES_OFF(6),

    // Parameters written by AudioProcessor
    OUTPUT_LEFT_PEAK(100),
    OUTPUT_RIGHT_PEAK(101),

    // Layer 1 parameters
    OSC1_SHAPE(200),
    OSC1_VOLUME(201),
    OSC1_PITCH(202),
    OSC1_DETUNE(203),
    OSC2_SHAPE(204),
    OSC2_VOLUME(205),
    OSC2_PITCH(206),
    OSC2_DETUNE(207),
    OSC3_SHAPE(208),
    OSC3_VOLUME(209),
    OSC3_DETUNE(210),
    OSC3_FOLD_OSC1(211),
    NOISE_SHAPE(212),
    NOISE_VOLUME(213),
    LOW_PASS_FREQ(214),
    HIGH_PASS_FREQ(215),
    BAND_PASS_FREQ(216),
    BAND_PASS_AMOUNT(217),
    VOWEL_FORMANT(218),
    VOWEL_AMOUNT(219),
    LFO1_SHAPE(220),
    LFO2_SHAPE(221),
    ENV1_ATTACK(222),
    ENV1_DECAY(223),
    ENV1_SUSTAIN(224),
    ENV1_RELEASE(225);

    companion object {
        val values = values()

        fun fromInt(value: Int) =
            values.find { it.value == value }
    }
}
