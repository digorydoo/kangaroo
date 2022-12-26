package ch.digorydoo.kangaroo.audio

import ch.digorydoo.kangaroo.param.ParamTag

enum class CCNumber(val value: Int) {
    BankSelectMSB(0),
    ModWheel(1),
    Breath(2),

    Foot(4),
    PortaTime(5),
    DataEntryMSB(6),
    ChannelVolume(7),
    Balance(8), // 64=centre

    Pan(10), // 64=centre
    Expression(11),
    Effect1(12),
    Effect2(13),

    GeneralPurpose1(16),
    GeneralPurpose2(17),
    GeneralPurpose3(18),
    GeneralPurpose4(19),

    // All 32..63 are LSB variants of 0..31
    BankSelectLSB(32),
    DataEntryLSB(38),

    // 64..69 are switches: value <= 63 interpreted as off, value > 63 interpreted as on
    DamperPedal(64),
    PortaSwitch(65),
    SustenutoSwitch(66), // like CC 64, however, it only holds notes that were "on" when the pedal was pressed
    SoftPedalSwitch(67), // lowers the volume
    LegatoFootSwitch(68),
    Hold2Switch(69), // hold note, but fade out according to release time

    // Sound controllers
    SoundVariation(70),
    FilterCutoff(71),
    ReleaseTime(72),
    AttackTime(73),
    FilterResonance(74),
    DecayTime(75),
    VibratoRate(76),
    VibratoDepth(77),
    VibratoDelay(78),
    SoundController10(79),

    GeneralPurpose5(80), // sometimes decay
    GeneralPurpose6(81), // sometimes high pass filter freq
    GeneralPurpose7(82),
    GeneralPurpose8(83),

    PortaAmount(84),

    // Effect parameters
    Eff1Depth(91), // usu. Reverb Send Level
    Eff2Depth(92), // usu. Tremolo Level
    Eff3Depth(93), // usu. Chorus Send Level
    Eff4Depth(94), // usu. Delay/Detune Level
    Eff5Depth(95), // usu. Phaser/Flanger Level

    // RPN and NRPN, (non-) registered parameter number
    DataIncrement(96),
    DataDecrement(97),
    NRPNSelectLSB(98), // select NRPN for DataEntryMSB, DataEntryLSB, DataIncrement, DataDecrement
    NRPNSelectMSB(99),
    RPNSelectLSB(100), // select RPN for DataEntryMSB, DataEntryLSB, DataIncrement, DataDecrement
    RPNSelectMSB(101),

    AllSoundsOff(120), // stops all sound, ignoring release time or damper pedal
    ResetAllControllers(121),
    LocalOnOff(122),
    AllNotesOff(123), // all keys up, but apply release time and damper pedal as usual
    OmniModeOff(124),
    OmniModeOn(125),
    PolyModeOnOff(126),
    PolyModeOn(127),

    // There aren't official CC numbers and will only work with VST3
    AfterTouch(128),
    PitchBend(129),
    ProgramChange(130),
    PolyPressure(131),
    QuarterFrame(132);

    companion object {
        private val values = values()

        fun fromInt(value: Int) =
            values.find { it.value == value }

        fun getParamForCC(cc: Int): ParamTag? = when (cc) {
            ChannelVolume.value -> ParamTag.MASTER_VOLUME
            ModWheel.value -> ParamTag.MOD_WHEEL
            PitchBend.value -> ParamTag.PITCH_BEND
            AfterTouch.value -> ParamTag.AFTER_TOUCH
            DamperPedal.value -> ParamTag.DAMPER_PEDAL
            AllNotesOff.value -> ParamTag.ALL_NOTES_OFF
            else -> null
        }
    }
}
