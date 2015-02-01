package as2388.scala.raytracer

class PointLight (val location: Point, val intensity: Double, val color: Color = ColorUtils.fromHex("FFFFFF"))

class VolumeLight (location: Point, val size: Double, intensity: Double,
                 color: Color = ColorUtils.fromHex("FFFFFF")) extends PointLight(location, intensity, color) {
    def getPointLights: List[PointLight] =
        (for (
            x <- -size / 2 to size / 2 by size / 2;
            y <- -size / 2 to size / 2 by size / 2;
            z <- -size / 2 to size / 2 by size / 2
        ) yield {
            println(x,y,z)
            new PointLight(location + new Point(x, y, z), intensity / 27.0, color)
        }).toList
}
