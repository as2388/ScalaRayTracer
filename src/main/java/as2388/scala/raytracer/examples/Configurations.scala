package as2388.scala.raytracer.examples

import as2388.scala.raytracer._
import as2388.scala.raytracer.shapes._


abstract class ConfigurationManager {
    def getConfiguration: Configuration
}

/**
* Renders a red sphere with a singularity at its centre, and a magnified purple sphere behind it
* @param size  Image size
*/
class LenseConfiguration(val size: Size, val frame: Float) extends ConfigurationManager {
    val TAU = (Math.PI / 2).toFloat

    override def getConfiguration = {
        val animated = (for (
            motionOffset <- -16 to 16
        ) yield {
            new StaticConfiguration(
                imageSize = size,

                shapes =
                        //                new Sphere(new Point(22, 5, 0), 3.4f, 0.9.toFloat, 0.1.toFloat, ColorUtils.fromHex("E91E63")) :: //Pink 500
                        //                new Sphere(new Point(28, 5, 0), 2, 0.9f, 0.1f, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                        new CheckeredPlane(new Vector(0, 0, 1), 6, 1, 0, ColorUtils.fromHex("F1F8E9"), ColorUtils.fromHex("9E9E9E")) ::
                                Nil,

                lights =
                        new PointLight(new Point(-10, 50, 80), 0.60.toFloat) ::
                                new PointLight(new Point(-15, 5, 0), 0.60.toFloat) ::
                                new PointLight(new Point(-20, -50, 30), 0.30.toFloat) ::
                                Nil,

                camera = new Camera(
                    screenCentre = new Point(22, 4, 0),
                    distanceFromScreen = 70,
                    screenPixelDimensions = size,
                    //                pointsPerPixel = .115.toFloat,
                    pointsPerPixel =.065.toFloat,
                    yaw = (TAU / -400 * frame + motionOffset.toFloat / 1600),
                    pitch = TAU / 16,
                    roll = 0
                ),

                singularities =
                        new Singularity(new Point(22, 4, 0), -0.037.toFloat) ::
                                Nil,

                            antiAliasingMode = new AntiAliasingFocus,

                singularityDepthLimit = 600
            )
        }).toList

        new AnimatedConfiguration(animated)
    }
}

/**
 * Renders the image on this project's github home page, a checkerboard with spheres and cuboids on it
 * @param size
 * @param iter
 */
class CheckerboardConfiguration(val size: Size, val iter: Float, val frame: Float) extends ConfigurationManager {
    lazy val TAU = 2 * Math.PI

    val XRot = 0
    val YRot = 0
    val ZRot = iter
    private def rotate(vertices: List[Point]) = vertices map (_ rotateX XRot rotateY YRot rotateZ ZRot)

    override def getConfiguration = {
        val animated = (for (
            motionOffset <- -10 to 10
        ) yield {
            new StaticConfiguration(
//                singularities =
//                    new Singularity(new Point(40, -2, -2), -0.003.toFloat) ::
//                    new Singularity(new Point(40, 12, -2), -0.003.toFloat) ::
//                    Nil,

                imageSize = size,
                shapes =
                        new Sphere(new Point(22, 5, 0), 3, 0.9.toFloat, 0.1.toFloat, ColorUtils.fromHex("E91E63")) :: //Pink 500
                        new Sphere(new Point(21, 9, -2), 1, 0.5.toFloat, 0.5.toFloat, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                        new Sphere(new Point(36, -8, -2), 1, 0.5.toFloat, 0.5.toFloat, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                        new Sphere(new Point(15, 18, -2), 1, 0.5.toFloat, 0.5.toFloat, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                        new Sphere(new Point(10, -12, -2), 1, 0.5.toFloat, 0.5.toFloat, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                        new Sphere(new Point(40, 12, -2), 1, 0.5.toFloat, 0.5.toFloat, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                        new Sphere(new Point(3, -7, -2), 1, 0.5.toFloat, 0.5.toFloat, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                        new Sphere(new Point(14, 0, -2), 1, 0.5.toFloat, 0.5.toFloat, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                        new Sphere(new Point(80, 19, -2), 1, 0.5.toFloat, 0.5.toFloat, ColorUtils.fromHex("673AB7")) :: //Deep Purple
                        new Cuboid(
                            center = new Point(8, -10, -0.5.toFloat),
                            XScale = 8, YScale = 2, ZScale = 5,
                            XRot = 0, YRot = 0, ZRot = (TAU / 5).toFloat,
                            diffusivity = 0.5.toFloat, reflectivity = 0.5.toFloat,
                            color = ColorUtils.fromHex("4CAF50")
                        ) ::
                        new Cuboid(
                            center = new Point(58, 25, -1.5.toFloat),
                            XScale = 3, YScale = 3, ZScale = 3,
                            XRot = 0, YRot = 0, ZRot = (-TAU / 5).toFloat,
                            diffusivity = 0.35.toFloat, reflectivity = 0.75.toFloat,
                            color = ColorUtils.fromHex("009688")
                        ) ::
                        new CheckeredPlane(new Vector(0, 0, 1), 3, 0.8.toFloat, 0.2.toFloat, ColorUtils.fromHex("F1F8E9"), ColorUtils.fromHex("9E9E9E")) ::
                        Nil,

                lights =
                        if (iter <= 6 || true)
                            new PointLight(new Point(-10, 50, 80), 0.60.toFloat) ::
                                    new PointLight(new Point(-20, -50, 30), 0.30.toFloat, ColorUtils.fromHex(if (iter > 4) "FFAB91" else "FFFFFF")) ::
                                    Nil
                        else
                            new VolumeLight(new Point(-10, 50, 80), 8.0.toFloat, 0.60.toFloat) ::
                                    new VolumeLight(new Point(-20, -50, 30), 18.0.toFloat, 0.30.toFloat, ColorUtils.fromHex("FFAB91")) ::
                                    Nil,

                camera = new Camera(
                    screenCentre = new Point(22, 5, 0), //(22, ...
                    distanceFromScreen = 70, //40
                    screenPixelDimensions = size,
                    pointsPerPixel = 0.02.toFloat,
                    yaw = 0, //(TAU / 400 * frame + motionoffset.toFloat / 400).toFloat,
                    pitch = (TAU / 16).toFloat,
                    roll = 0 //TAU / 400 * frame
                ),

                singularityDepthLimit = 200,

                enableShadows = iter > 2,
                enableDiffuse = iter > 1,
                enableReflections = iter > 3,
                ambientIntensity = if (iter > 1) 0.1.toFloat else 0.6.toFloat,

                antiAliasingMode = new AntiAliasingRegular(2)
//
//                focusMode = if (iter > 7 && false) new FocusSome(10, (Math.PI / 400).toFloat) else new FocusNone
            )
        }).toList

        new AnimatedConfiguration(animated)
    }
}

class RollingConfiguration(val size: Size, val iter: Float, val frame: Float) extends ConfigurationManager {
    val TAU = 2 * Math.PI

    override def getConfiguration = {
        val animated = (for (
            motionoffset <- -10 to 10
        ) yield {
            new StaticConfiguration(
                imageSize = size,
                shapes =
                    new Sphere(new Point(45, 5 + motionoffset.toFloat / 50 + frame / 5, -2), 1, 0.5.toFloat, 0.5.toFloat, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                    new Sphere(new Point(40, 5 + motionoffset.toFloat / 100 + frame / 10, -2), 1, 0.5.toFloat, 0.5.toFloat, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                    new Sphere(new Point(35, 5 + motionoffset.toFloat / 200 + frame / 20, -2), 1, 0.5.toFloat, 0.5.toFloat, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                    new Sphere(new Point(30, 5 + motionoffset.toFloat / 400 + frame / 40, -2), 1, 0.5.toFloat, 0.5.toFloat, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                    new Sphere(new Point(25, 5 + motionoffset.toFloat / 200, -2), 1, 0.5.toFloat, 0.5.toFloat, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                    new Sphere(new Point(20, 5 + motionoffset.toFloat / 100, -2), 1, 0.5.toFloat, 0.5.toFloat, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                    new Sphere(new Point(15, 5 + motionoffset.toFloat / 50, -2), 1, 0.5.toFloat, 0.5.toFloat, ColorUtils.fromHex("673AB7")) :: //Deep Purple 500
                    new CheckeredPlane(new Vector(0, 0, 1), 3, 0.8.toFloat, 0.2.toFloat, ColorUtils.fromHex("F1F8E9"), ColorUtils.fromHex("9E9E9E")) ::
                    Nil,

                lights =
                        if (iter <= 6 && false)
                            new PointLight(new Point(-10, 50, 80), 0.60.toFloat) ::
                                    new PointLight(new Point(-20, -50, 30), 0.30.toFloat, ColorUtils.fromHex(if (iter > 4) "FFAB91" else "FFFFFF")) ::
                                    Nil
                        else
                            new VolumeLight(new Point(-10, 50, 80), 8.0.toFloat, 0.60.toFloat) ::
                                    new VolumeLight(new Point(-20, -50, 30), 18.0.toFloat, 0.30.toFloat, ColorUtils.fromHex("FFAB91")) ::
                                    Nil,

                camera = new Camera(
                    screenCentre = new Point(22, 5, 0), //(22, ...
                    distanceFromScreen = 70, //40
                    screenPixelDimensions = size,
                    pointsPerPixel = 0.015.toFloat,
                    yaw = 0, //(TAU / 400 * frame + motionoffset.toFloat / 400).toFloat,
                    pitch = (TAU / 16).toFloat,
                    roll = 0 //TAU / 400 * frame
                ),

                enableShadows = iter > 2,
                enableDiffuse = iter > 1,
                enableReflections = iter > 3,
                ambientIntensity = if (iter > 1) 0.1.toFloat else 0.6.toFloat

//                antiAliasingMode = new AntiAliasingNone,

//                focusMode = if (iter > 7) new FocusSome(10, (Math.PI / 400).toFloat) else new FocusNone
            )
        }).toList

        new AnimatedConfiguration(animated)
    }
}




//    ----------- CIRCLE RING --------------------
//        val shapes: List[Shape] = new CheckeredPlane(new Vector(0, 0, 1), 3, 0.8, 0.2, Color.rgb(238, 238, 238), Color.rgb(158, 158, 158)) ::
//                buildCircle(new Point(22, 5, -2), 8, 2 * Math.PI / 9, 2 * Math.PI)
//
//
//        def buildCircle(centre: Point, radius: Float, angleSize: Float, currentAngle: Float): List[Shape] =
//            if (currentAngle <= 0) Nil
//            else new Sphere(new Point(radius * Math.sin(currentAngle) + centre.x, radius * Math.cos(currentAngle) + centre.y, centre.z),
//                1.2, 0.6, 0.4, Color.rgb(103, 58, 183)) ::
//                    buildCircle(centre, radius, angleSize, currentAngle - angleSize)
//
//        val lights: List[PointLight] = new PointLight(new Point(22, 5, 5), 0.9) ::
//                Nil
//
//        val camera = new Camera(
//            viewPoint = new Point(-20, 5, 20),
//            viewDirection = new Vector(0.65, 0, -0.35).normalize(),
//            viewUp = new Vector(0.35, 0, 0.65).normalize(),
//            distanceFromScreen = 15,
//            screenPixelDimensions = new Size(2560, 1440),
//            pointsPerPixel = 0.006
//        )
//    ---------------------------------------

