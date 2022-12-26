@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package ch.digorydoo.kangaroo.audio.filter

import ch.digorydoo.kangaroo.math.lerp
import kotlin.math.floor
import kotlin.math.ln

class FormantFilter(samplingFreq: Double) {
    class Formant(
        val freq1: Double,
        val freq2: Double,
        val freq3: Double,
        val gain1: Double,
        val gain2: Double,
        val gain3: Double,
    ) {
        constructor(f1: Int, f2: Int, f3: Int, g1: Double, g2: Double, g3: Double):
            this(f1.toDouble(), f2.toDouble(), f3.toDouble(), g1, g2, g3)

        constructor(f1: Double, f2: Double, f3: Double):
            this(f1, f2, f3, 1.0, 1.0, 1.0)

        constructor(f1: Int, f2: Int, f3: Int):
            this(f1.toDouble(), f2.toDouble(), f3.toDouble(), 1.0, 1.0, 1.0)
    }

    private val band1 = BandPassFilter(samplingFreq)
    private val band2 = BandPassFilter(samplingFreq)
    private val band3 = BandPassFilter(samplingFreq)

    private var gain1 = 0.0
    private var gain2 = 0.0
    private var gain3 = 0.0

    /**
     * @param formant
     * @param q: 1..+x
     */
    fun set(formant: Formant, q: Double = 8.0) {
        band1.set(formant.freq1, q)
        band2.set(formant.freq2, q)
        band3.set(formant.freq3, q)

        // We're going to sum the three bands, so divide by 3.
        // Also increase gain with q, because high q sounds thinner.
        val g = 1 / 3.0f + ln(q) * 0.42f // q >= 1, ln(1) is 0

        gain1 = formant.gain1 * g
        gain2 = formant.gain2 * g
        gain3 = formant.gain3 * g
    }

    fun evaluate(x: Double) =
        gain1 * band1.evaluate(x) + gain2 * band2.evaluate(x) + gain3 * band3.evaluate(x)

    companion object {
        // https://linguistics.ucla.edu/people/hayes/103/Charts/VChart/#FormantMeasurements
        // https://cnx.org/contents/nTOPtfsM@2.5:e8NdvtVS@2/Formant-Vowel-Synthesis
        // https://www.researchgate.net/publication/280042703_Phonological_Grammar_and_Frequency_An_Integrated_Approach_Evidence_from_German_Indonesian_and_Japanese
        // https://www.jstage.jst.go.jp/article/onseikenkyu/9/1/9_KJ00007631513/_pdf

        val formantAe = Formant(806, 1632, 2684) // Ä, lower low, front unrounded, Ä as in Määh

        val formantA = Formant(730, 1090, 2440) // A
        val formantAee = Formant(690, 1735, 2655) // Ä as in Wäsche
        val formant35 = Formant(688, 1446, 2314) // ɐ, upper low, central unrounded, A as in first

        val formantEAe = Formant(650, 2280, 2870) // between E/Ä
        val formantEae = Formant(470, 2025, 2775) // E, nuance of ä
        val formantE = Formant(434, 2148, 2763) // e, upper mid, front unrounded, nice E
        val formantEoe = Formant(520, 1850, 2490) // E, close to Ö

        val formant23 = Formant(462, 1659, 2127) // ø, upper mid, front rounded, between E/Ö
        val formantOe = Formant(471, 1429, 2273) // Ö as in Döner
        val formantAoe = Formant(560, 1240, 2410) // ö as in mercy
        val formantO = Formant(570, 840, 2410, 1.0, 0.9, 1.0) // O

        val formantU = Formant(300, 870, 2240, 0.9, 0.9, 1.0) // U

        val formantUe = Formant(292, 2100, 4100) // Ü

        val formantI = Formant(270, 2290, 3010) // I
        val formant11 = Formant(294, 2343, 3251) // i, upper high, front unrounded, nuance of E
        val formant12 = Formant(360, 2187, 2830) // I, lower high, front unrounded, between E/I
        val formantEy = Formant(395, 2300, 2800) // E, nuance of I

        val formantOea = Formant(480, 1200, 2420) // between Öa

        val formant14 = Formant(581, 1840, 2429) // ɛ, lower mid, front unrounded, between E/Ä
        val formant15 = Formant(766, 1782, 2398) // æ, upper low, front unrounded, Ä/E
        val formant21 = Formant(283, 2170, 2417) // y, upper high, front rounded, I, nuance of Ü, not good
        val formant22 = Formant(401, 1833, 2241) // Y, lower high, front rounded, nuance of Ö, not good
        val formant24 = Formant(546, 1604, 2032) // œ, lower mid, front rounded, not good
        val formant26 = Formant(572, 1537, 1802) // Œ, lower low, front rounded, between E/Ö, not good
        val formant31 = Formant(293, 2186, 2507) // ɨ, upper high, central unrounded, I, nuance of Ü, not good
        val formant33 = Formant(415, 1955, 2421) // ɘ, upper mid, central unrounded, slight nuance of Ä
        val formant34 = Formant(557, 1696, 2423) // ɜ, lower mid, central unrounded, between E/Ä
        val formant36 = Formant(784, 1211, 2702) // A, lower low, central unrounded, A, nuance of Ä
        val formant41 = Formant(333, 1482, 2232) // ʉ, upper high, central rounded, nuance of A, not good
        val formant43 = Formant(519, 1593, 2187) // ə, upper mid, central rounded, nuance of Ä
        val formant44 = Formant(581, 1439, 2186) // ɞ, lower mid, central rounded, Ö as in early
        val formant51 = Formant(329, 1806, 2723) // ɯ, upper high, back unrounded, nuance of E, not good
        val formant53 = Formant(605, 1657, 2596) // ɤ, upper mid, back unrounded, Ä, not good
        val formant54 = Formant(707, 1354, 2289) // ʌ, lower mid, back unrounded, between A/Ä
        val formant56 = Formant(781, 1065, 2158) // ɑ, lower low, back unrounded, not good
        val formant61 = Formant(295, 750, 2342) //  u, upper high, back rounded, nuance of O, not good
        val formant62 = Formant(334, 910, 2300) //  ʊ, lower high, back rounded, between A/O, not good
        val formant63 = Formant(406, 727, 2090) //  o, upper mid, back rounded, not good
        val formant64 = Formant(541, 830, 2221) //  ɔ, lower mid, back rounded, between A/O, not good
        val formant66 = Formant(652, 843, 2011) //  ɒ, lower low, back rounded, not good

        val vowels = arrayOf(formantA, formantO, formantU, formantE, formantI, formantAe, formantOe, formantUe)

        /**
         * @param pos: 0..1
         */
        fun continuous(pos: Double) =
            continuous(pos, vowels)

        /**
         * @param pos: 0..1
         * @param arr: array of formants to pick from
         */
        fun continuous(pos: Double, arr: Array<Formant>) = when {
            pos <= 0.0f -> arr[0]
            pos >= 1.0f -> arr[arr.size - 1]
            else -> {
                val p = pos * (arr.size - 1)
                val rel = p - floor(p)
                val m = arr[p.toInt()]
                val n = arr[p.toInt() + 1]
                lerp(m, n, rel)
            }
        }

        fun lerp(from: Formant, to: Formant, rel: Double) =
            Formant(
                lerp(from.freq1, to.freq1, rel),
                lerp(from.freq2, to.freq2, rel),
                lerp(from.freq3, to.freq3, rel),
                lerp(from.gain1, to.gain1, rel),
                lerp(from.gain2, to.gain2, rel),
                lerp(from.gain3, to.gain3, rel),
            )
    }
}
