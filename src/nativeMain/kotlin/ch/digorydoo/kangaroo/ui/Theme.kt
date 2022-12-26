package ch.digorydoo.kangaroo.ui

class Theme(viewPtr: Long) {
    val blueGrey950 = Colour(0.05f, 0.05f, 0.10f)
    val blueGrey920 = Colour(0.08f, 0.08f, 0.13f)
    val blueGrey900 = Colour(0.10f, 0.10f, 0.15f)
    val blueGrey850 = Colour(0.15f, 0.15f, 0.20f)
    val blueGrey800 = Colour(0.20f, 0.20f, 0.25f)
    val blueGrey750 = Colour(0.25f, 0.25f, 0.30f)
    val blueGrey700 = Colour(0.30f, 0.30f, 0.35f)
    val blueGrey650 = Colour(0.35f, 0.35f, 0.40f)
    val blueGrey600 = Colour(0.40f, 0.40f, 0.45f)
    val blueGrey500 = Colour(0.50f, 0.50f, 0.55f)
    val blueGrey400 = Colour(0.60f, 0.60f, 0.65f)
    val blueGrey300 = Colour(0.70f, 0.70f, 0.75f)
    val blueGrey200 = Colour(0.80f, 0.80f, 0.85f)
    val blueGrey100 = Colour(0.90f, 0.90f, 0.95f)

    val accent = Colour.fromString("#f80")
    val accentDark = Colour.fromString("#e70")
    val accentDarker = Colour.fromString("#d60")

    val disabledOpacity = 0.25f

    val knobGradient = Gradient.create(
        viewPtr,
        Gradient.Stop(0.0f, blueGrey500),
        Gradient.Stop(1.0f, blueGrey900)
    )
}
