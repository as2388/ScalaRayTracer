package as2388.scala.raytracer

class PointLight (val location: Point, val intensity: Double, val color: Color = ColorUtils.fromHex("FFFFFF"))

class VolumeLight (location: Point, val size: Double, intensity: Double,
                 color: Color = ColorUtils.fromHex("FFFFFF")) extends PointLight(location, intensity, color) {
    def getPointLights: List[PointLight] =
        (for (
            x <- -size / 2 to size / 2 by size / 3;
            y <- -size / 2 to size / 2 by size / 3;
            z <- -size / 2 to size / 2 by size / 3
        ) yield {
            new PointLight(location + new Point(x, y, z), intensity / 64.0, color)
        }).toList
}
