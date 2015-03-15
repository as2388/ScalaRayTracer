package as2388.scala.raytracer

class Color(val r: Float, val g: Float, val b: Float) {
    def *(d: Float) = new Color(r * d, g * d, b * d)

    def *(c: Color) = new Color(r * c.r, g * c.g, b * c.b)

    def +(c: Color) = new Color(r + c.r, g + c.g, b + c.b)

    def -(c: Color) = new Color(r - c.r, g - c.g, b - c.b)

    def toScalaFXColor =
        scalafx.scene.paint.Color.color(if (r > 1) 1.0 else r, if (g > 1) 1.0 else g, if (b > 1) 1.0 else b)

    def getSafeColor = new Color(if (r > 1) 1.0.toFloat else r, if (g > 1) 1.0.toFloat else g, if (b > 1) 1.0.toFloat else b)
}

object ColorUtils {
    def fromHex(hex: String) = {
        def hexToFloat(hex: String) = Integer.parseInt(hex, 16) / 255.0.toFloat

        val Floats = hex.sliding(2, 2).toList.map(hexToFloat)

        new Color(Floats(0), Floats(1), Floats(2))
    }
}
