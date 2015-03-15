package as2388.scala.raytracer.examples

import as2388.scala.raytracer._
import as2388.scala.raytracer.shapes._


abstract class ConfigurationManager {
    def getConfiguration: Configuration
}

//abstract class AnimatedConfigurationManager {
//    def getConfigurations: List[Configuration]
//}

///**
// * Renders a red sphere with a singularity at its centre, and a magnified purple sphere behind it
// * @param size  Image size
// */
//class LenseConfiguration(val size: Size) extends ConfigurationManager {
//    override def getConfiguration = {
//        new Configuration(
//            imageSize = size,
//
//            shapes =
//                new Sphere(new Point(28, 6, 0), 2, 1.0.toFloat, 0, new Color(255, 193, 7)) :: Nil,
//
//            lights =
//                new PointLight(new Point(-10, 50, 80), 0.30.toFloat) ::
//                new PointLight(new Point(54, 50, 80), 0.30.toFloat) ::
//                new PointLight(new Point(-20, -50, 50), 0.2.toFloat) ::
//                new PointLight(new Point(28, 5, -70), 0.20.toFloat) ::
//                Nil,
//
//            camera = new Camera(
//                screenCentre = new Point(22, 5, 0),
//                distanceFromScreen = 70,
//                screenPixelDimensions = size,
//                pointsPerPixel = .35.toFloat,
//                yaw = 0,
//                pitch = 0,
//                roll = 0
//            ),
//
//            singularities =
//                new Singularity(new Point(22, 5, 0), -0.037.toFloat) ::
//                Nil,
//
//            singularityDepthLimit = 300
//        )
//    }
//}

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
        //        val gr = (1 + Math.sqrt(5)) / 2
        //
        //        val a = new Point(gr, 0, 1 / gr)
        //        val b = new Point(-gr, 0, 1 / gr)
        //        val c = new Point(-gr, 0, -1 / gr)
        //        val d = new Point(gr, 0, -1 / gr)
        //
        //        val e = new Point(1 / gr, gr, 0)
        //        val f = new Point(1 / -gr, gr, 0)
        //        val g = new Point(-1 / -gr, gr, 0)
        //        val h = new Point(-1 / gr, gr, 0)
        //
        //        val i = new Point(0, 1 / gr, gr)
        //        val j = new Point(0, 1 / gr, -gr)
        //        val k = new Point(0, -1 / gr, -gr)
        //        val l = new Point(0, -1 / gr, gr)
        //
        //        val m = new Point(1, 1, 1)
        //        val n = new Point(1, -1, 1)
        //        val o = new Point(-1, -1, 1)
        //        val p = new Point(-1, 1, 1)
        //        val q = new Point(-1, 1, -1)
        //        val r = new Point(1, 1, -1)
        //        val s = new Point(1, -1, -1)
        //        val t = new Point(-1, -1, -1)
        //
        //        val planes = (new FinitePlane(
        //            new Point(-1, 0, 0) ::
        //                    new Point(0, -1, 0) ::
        //                    new Point(0, 0, 1) ::
        //                    new Point(-1, 0, 0) ::
        //                    Nil,
        //            0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
        //        ) ::
        //                new FinitePlane(
        //                    new Point(0, -1, 0) ::
        //                            new Point(1, 0, 0) ::
        //                            new Point(0, 0, 1) ::
        //                            new Point(0, -1, 0) ::
        //                            Nil,
        //                    0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
        //                ) ::
        //                new FinitePlane(
        //                    new Point(1, 0, 0) ::
        //                            new Point(0, 1, 0) ::
        //                            new Point(0, 0, 1) ::
        //                            new Point(1, 0, 0) ::
        //                            Nil,
        //                    0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
        //                ) ::
        //                new FinitePlane(
        //                    new Point(0, 0, 1) ::
        //                            new Point(0, 1, 0) ::
        //                            new Point(-1, 0, 0) ::
        //                            new Point(0, 0, 1) ::
        //                            Nil,
        //                    0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
        //                ) ::
        //
        //                new FinitePlane(
        //                    new Point(-1, 0, 0) ::
        //                            new Point(0, 0, -1) ::
        //                            new Point(0, -1, 0) ::
        //                            new Point(-1, 0, 0) ::
        //                            Nil,
        //                    0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
        //                ) ::
        //                new FinitePlane(
        //                    new Point(0, -1, 0) ::
        //                            new Point(0, 0, -1) ::
        //                            new Point(1, 0, 0) ::
        //                            new Point(0, -1, 0) ::
        //                            Nil,
        //                    0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
        //                ) ::
        //
        //                new FinitePlane(
        //                    new Point(0, 1, 0) ::
        //                            new Point(1, 0, 0) ::
        //                            new Point(0, 0, -1) ::
        //                            new Point(0, 1, 0) ::
        //                            Nil,
        //                    0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
        //                ) ::
        //                new FinitePlane(
        //                    new Point(-1, 0, 0) ::
        //                            new Point(0, 1, 0) ::
        //                            new Point(0, 0, -1) ::
        //                            new Point(-1, 0, 0) ::
        //                            Nil,
        //                    0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
        //                ) :: Nil) map (plane => new FinitePlane(plane.normal rotateX XRot rotateY YRot rotateZ ZRot, plane.distance,
        //            plane.diffusivity, plane.reflectivity, plane.color,
        //            rotate(plane.vertices), new Point(22,5,0)))


        val animated = (for (
            motionoffset <- -10 to 10
        ) yield {
            new StaticConfiguration(
                imageSize = size,
                //                    new FinitePlane(
                //                        new Point(-1, 1, -1) ::
                //                        new Point(-1 / gr, gr, 0) ::
                //                        new Point(-1, 1, 1) ::
                //                        new Point(-gr, 0, 1 / gr) ::
                //                        new Point(-gr, 0, -1 / gr) ::
                //                        new Point(-1, 1, -1) ::
                //                        Nil,
                //                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                //                    ) ::
                //                    new FinitePlane(
                //                        new Point(-gr, 0, -1 / gr) ::
                //                        new Point(-gr, 0, 1 / gr) ::
                //                        new Point(-1 , -1 , 1) ::
                //                        new Point(-1 / gr, -gr, 0) ::
                //                        new Point(-1, -1, -1) ::
                //                        new Point(-gr, 0, -1 / gr) ::
                //                        Nil,
                //                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                //                    ) ::
                //                    new FinitePlane(
                //                        new Point(-gr, 0, -1 / gr) ::
                //                        new Point(-1, -1, -1) ::
                //                        new Point(0, -1 / gr, -gr) ::
                //                        new Point(0, 1 / gr, -gr) ::
                //                        new Point(-1, 1, -1) ::
                //                        new Point(-gr, 0, -1 / gr) ::
                //                        Nil,
                //                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                //                    ) ::
                //                    new FinitePlane(
                //                        new Point(-gr, 0, 1 / gr) ::
                //                        new Point(-1, 1, 1) ::
                //                        new Point(0, 1 / gr, gr) ::
                //                        new Point(0, -1 / gr, gr) ::
                //                        new Point(-1, -1, 1) ::
                //                        new Point(-gr, 0, 1 / gr) ::
                //                        Nil,
                //                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                //                    ) ::
                //                    new FinitePlane(
                //                        new Point(-1, 1, 1) ::
                //                        new Point(- 1/ gr, gr, 0) ::
                //                        new Point(1 / gr, gr, 0) ::
                //                        new Point(1, 1, 1) ::
                //                        new Point(0, 1/ gr, gr) ::
                //                        new Point(-1, 1, 1) ::
                //                        Nil,
                //                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                //                    ) ::
                //                    new FinitePlane(
                //                        new Point(-1, 1, 1) ::
                //                        new Point(0, -1 / gr, gr) ::
                //                        new Point(1, -1, 1) ::
                //                        new Point(1 / gr, -gr, 0) ::
                //                        new Point(-1 / gr, -gr, 0) ::
                //                        new Point(-1, 1, 1) ::
                //                        Nil,
                //                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                //                    ) ::
                //                    new FinitePlane(
                //                        new Point(gr, 0, -1 / gr) ::
                //                        new Point(1, 1, -1) ::
                //                        new Point(0, 1 / gr, - gr) ::
                //                        new Point(0, -1 / gr, -gr) ::
                //                        new Point(1, -1, -1) ::
                //                        new Point(gr, 0, -1 / gr) ::
                //                        Nil,
                //                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                //                    ) ::
                //                    new FinitePlane(
                //                        new Point(1, -1, 1) ::
                //                        new Point(gr, 0, 1 / gr) ::
                //                        new Point(gr, 0, -1 / gr) ::
                //                        new Point(1 / gr, -gr, 0) ::
                //                        new Point(1, -1, 1) ::
                //                        new Point(1, -1, 1) ::
                //                        Nil,
                //                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                //                    ) ::
                //                    new FinitePlane(
                //                        new Point(1, -1, 1) ::
                //                        new Point(gr, 0, 1 / gr) ::
                //                        new Point(gr, 0, -1 / gr) ::
                //                        new Point(1 / gr, -gr, 0) ::
                //                        new Point(1, -1, 1) ::
                //                        new Point(1, -1, 1) ::
                //                        Nil,
                //                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                //                    ) ::

                //                    new FinitePlane(
                //                        new Point(0, -1 / gr, gr) ::
                //                        new Point(0, 1 / gr, gr) ::
                //                        new Point(1, 1, 1) ::
                //                        new Point(gr, 0, 1/gr) ::
                //                        new Point(1, -1, 1) ::
                //                        new Point(0, -1 / gr, gr) ::
                //                        Nil,
                //                        0.90, 0.10, ColorUtils.fromHex("FF5722"), new Point(22, 5, 0)
                //                    ) ::

                //                    new FinitePlane(
                //                        new Point(-0.5, 0.5, ) ::
                //                        new Point(-1 / gr, gr, 0) ::
                //                        new Point(-1, 1, 1) ::
                //                        new Point(-gr, 0, 1 / gr) ::
                //                        Nil,
                //                        0.8, 0.2, new Color(1, 1, 1), new Point(0, 0, 0)
                //                    ) ::

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
                ambientIntensity = if (iter > 1) 0.1.toFloat else 0.6.toFloat,

                antiAliasingMode = new AntiAliasingRegular(3),

                focusMode = if (iter > 7) new FocusSome(10, (Math.PI / 400).toFloat) else new FocusNone
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
                ambientIntensity = if (iter > 1) 0.1.toFloat else 0.6.toFloat,

                antiAliasingMode = if (iter > 5) new AntiAliasingRegular(3) else new AntiAliasingNone,

                focusMode = if (iter > 7) new FocusSome(10, (Math.PI / 400).toFloat) else new FocusNone
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

