package as2388.scala.raytracer

import as2388.scala.raytracer.shapes.Shape

import scalafx.scene.image.PixelWriter

class RayTracer(val configuration: Configuration) {
    val size = configuration.imageSize
    val shapes = configuration.shapes
    val lights = configuration.lights
    val ambientIntensity = configuration.ambientIntensity
    val camera = configuration.camera
    val singularities = configuration.singularities
    val singularityDepthLimit = configuration.singularityDepthLimit
    val enableShadows = configuration.enableShadows
    val enableDiffuse = configuration.enableDiffuse
    val enableReflections = configuration.enableReflections
    val antiAliasingMode = configuration.antiAliasingMode
    val focusMode = configuration.focusMode

    /**
     * Produces intersection data for the closest shape to intersect a line. If no intersection is found, returns null.
     * If singularities are enabled, recursively simulates space-time warping.
     * @param line          The line to find the closest intersection with
     * @param distanceSoFar If using singularities, the distance travelled so far. If this reaches the
     *                      singularityDepthLimit, recursion is terminated.
     * @return              IntersectionData for closest intersecting shape if there is one, else null
     */
    def closestShape(line: Line, distanceSoFar: Double): IntersectionData = {
        lazy val closest = (shapes map (_ closestIntersection line)).foldLeft(null: IntersectionData)((b, a) =>
            if (b == null && a != null && a.distance > 0) a
            else
            if (a != null && b != null && a.distance > 0 && a.distance < b.distance) a
            else b)

        singularities match {
            case Nil => closest
            case _ =>
                if (distanceSoFar > singularityDepthLimit) null
                else if (closest == null || closest.distance > 1) {
                    val endPoint = line.point add line.vector.scalarMultiply(1).asPoint()

                    val gravitationalForces: List[Vector] = singularities map (singularity => new Vector(endPoint, singularity.location)
                            scalarMultiply (singularity.strength / Math.pow(endPoint distanceTo singularity.location, 1)))

                    val newVector = gravitationalForces.foldLeft(line.vector)((b: Vector, a: Vector) => a add b) normalize()

                    closestShape(new Line(endPoint, newVector), distanceSoFar + 1)
                }
                else closest
        }
    }

    def diffuseColor(intersectionData: IntersectionData): Color =
        lights.map(light =>
            if (inShadow(shapes, new Line(
                intersectionData.intersectionPoint,
                new Vector(light.location, intersectionData.intersectionPoint)), intersectionData.shape)
            ) new Color(0, 0, 0)
            else {
                val normal = intersectionData.normal dot (new Vector(
                    light.location, intersectionData.intersectionPoint) normalize())

                if (normal > 0) light.color * (normal * light.intensity)
                else new Color(0, 0, 0)
            }
        ).foldLeft(new Color(0, 0, 0))((b, a) => a + b)

    /**
     * Returns true if the line from an intersection point intersects another shape; false otherwise
     * @param shapes            List of shapes to check against
     * @param line              Line
     * @param ignoreShape       Shape to exclude from check (should probably be shape we're checking if is in shadow
     * @return
     */
    def inShadow(shapes: List[Shape], line: Line, ignoreShape: Shape) = enableShadows match {
        case true => ((shapes filter (_ != ignoreShape)) map (_ closestIntersection line) count (x => x != null && x.distance > 0)) != 0
        case _ => false
    }

    def colorRay(line: Line, impact: Double): Color = {
        val closestIntersection: IntersectionData = closestShape(line, 0)

        if (closestIntersection == null || impact < 0.05) new Color(0, 0, 0)
        else {
            val diffuse: Color = enableDiffuse match {
                case true => diffuseColor (closestIntersection) * closestIntersection.shape.diffusivity *
                    closestIntersection.shape.color (closestIntersection.intersectionPoint)
                case _    => new Color(0, 0, 0)
            }
            val ambient: Color = closestIntersection.shape.color(closestIntersection.intersectionPoint) * ambientIntensity
            val diffuseAmbientColor = diffuse + ambient

            if (closestIntersection.shape.reflectivity == 0 || !enableReflections) diffuseAmbientColor
            else {
                val reflectedVector: Vector = line.vector subtract (closestIntersection.normal scalarMultiply (line.vector dot closestIntersection.normal) * 2) //don't need to normalize
                val reflectedRay = new Line(closestIntersection.intersectionPoint, reflectedVector)
                val specularColor = colorRay(reflectedRay, impact * closestIntersection.shape.reflectivity)

                val reflectivity = closestIntersection.shape.reflectivity

                diffuseAmbientColor + (specularColor * reflectivity)
            }
        }
    }

    def colorPixel(pixelPoint: PixelPoint, yawChange: Double, pitchChange: Double) = {
        val lineToPixel = camera lineToPixel (pixelPoint, yawChange, pitchChange)
        colorRay(lineToPixel, 1.0)
    }

    def focus(pixelPoint: PixelPoint, count: Int, angle: Double) = {
        val sampledSubPixels = for {
            yawChange <- -angle to angle by angle * 2 / count
            pitchChange <- -angle to angle by angle * 2 / count
        } yield {
            antiAliasingFunction(pixelPoint, yawChange, pitchChange)
        }

        averageColors(sampledSubPixels.toList)
    }

    def antiAlias(pixelPoint: PixelPoint, count: Int, yawChange: Double, pitchChange: Double) = {
        val sampledSubPixels = for {
            x <- -count / 2 to count / 2
            y <- -count / 2 to count / 2
        } yield {
            colorPixel(new PixelPoint(pixelPoint.x + x.toDouble / 4, pixelPoint.y + y.toDouble / 4), yawChange, pitchChange)
        }

        averageColors(sampledSubPixels.toList)
    }

//    def adaptiveAntiAlias(pixelPoint: PixelPoint) = {
//        if (pixelPoint.x < 0.35 * size.width || pixelPoint.y > 0.65 * size.width ||
//            pixelPoint.y < 0.35 * size.height || pixelPoint.y > 0.65 * size.height) focus(pixelPoint)
//        else antiAlias(pixelPoint)
//    }

    /**
     * Given a list of colors, returns a new color with the list's (r,g,b) values averaged
     * @param colors    List of colors to average
     * @return Average color of list
     */
    def averageColors(colors: List[Color]) =
        new Color(
            colors.map(_.r).sum / colors.length,
            colors.map(_.g).sum / colors.length,
            colors.map(_.b).sum / colors.length
        )

    def antiAliasingFunction(pixelPoint: PixelPoint, yawChange: Double = 0, pitchChange: Double = 0) = antiAliasingMode match {
        case AntiAliasingRegular(count) => antiAlias(pixelPoint, count, yawChange, pitchChange)
        case AntiAliasingNone()         => colorPixel(pixelPoint, yawChange, pitchChange)
    }

    def focusFunction(pixelPoint: PixelPoint) = focusMode match {
        case FocusSome(count, angle)    => focus(pixelPoint, count, angle)
        case FocusNone()                => antiAliasingFunction(pixelPoint)
    }

    def writeToImage(writer: PixelWriter) = {
        var remaining = size.width

        (0 to size.width - 1).par foreach (x => {
            (0 to size.height - 1) foreach (y => writer setColor(x, y, focusFunction(new PixelPoint(x, y)).toScalaFXColor))
            remaining -= 1
            if (remaining % 20 == 0)
                println((((size.width - remaining).toDouble / size.width.toDouble) * 10000).floor / 100 + "% done (" + remaining + " columns remain)")
        })
    }
}