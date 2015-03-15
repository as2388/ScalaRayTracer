package as2388.scala.raytracer

class PointLight (val location: Point, val intensity: Float, val color: Color = ColorUtils.fromHex("FFFFFF")) {
    def randomPointLight: PointLight = this
}

class VolumeLight (location: Point, val size: Float, intensity: Float,
                 color: Color = ColorUtils.fromHex("FFFFFF")) extends PointLight(location, intensity, color) {
    def getPointLights: List[PointLight] =
        (for (
            x <- -size / 2 to size / 2 by size / 2;
            y <- -size / 2 to size / 2 by size / 2;
            z <- -size / 2 to size / 2 by size / 2
        ) yield {
            new PointLight(location + new Point(x, y, z), intensity / 27.0.toFloat, color)
        }).toList

    override def randomPointLight: PointLight =
        new PointLight(
            new Point(
                Math.random.toFloat * size - size / 2,
                Math.random.toFloat * size - size / 2,
                Math.random.toFloat * size - size / 2
            ) + location,
            intensity, color)
}
