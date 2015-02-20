package as2388.scala.raytracer

class Color(val r: Double, val g: Double, val b: Double) {
    def *(d: Double) = new Color(r * d, g * d, b * d)

    def *(c: Color) = new Color(r * c.r, g * c.g, b * c.b)

    def +(c: Color) = new Color(r + c.r, g + c.g, b + c.b)

    def -(c: Color) = new Color(r - c.r, g - c.g, b - c.b)

    def toScalaFXColor =
        scalafx.scene.paint.Color.color(if (r > 1) 1.0 else r, if (g > 1) 1.0 else g, if (b > 1) 1.0 else b)

    def getSafeColor = new Color(if (r > 1) 1.0 else r, if (g > 1) 1.0 else g, if (b > 1) 1.0 else b)
}

object ColorUtils {
    def fromHex(hex: String) = {
        def hexToDouble(hex: String) = Integer.parseInt(hex, 16) / 255.0

        val doubles = hex.sliding(2, 2).toList.map(hexToDouble)

        new Color(doubles(0), doubles(1), doubles(2))
    }
}
