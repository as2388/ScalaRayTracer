package as2388.scala.raytracer

import as2388.scala.raytracer.shapes.Shape

class Configuration(
       val imageSize: Size,
       val shapes: List[Shape],
       val lights: List[PointLight],
       val camera: Camera,
       val singularities: List[Singularity] = Nil,
       val singularityDepthLimit: Double = 100,
       val enableShadows: Boolean = true,
       val antiAliasingMode: AntiAliasingMode = new AntiAliasingNone,
       val focusMode: FocusMode = new FocusNone
)

/*
    Size
    Multithread
    Progress interval
    Depth of field = None | Some(angle, count)
    Diffuse illumination
    Reflections
    Adaptive AntiAlias

    TODO:
    Soft shadows
 */

abstract class AntiAliasingMode
/** No anti-aliasing */
case class AntiAliasingNone() extends AntiAliasingMode
/** Anti-alias from samples taken at regular intervals, with count samples taken in each direction x and y */
case class AntiAliasingRegular(count: Int) extends AntiAliasingMode

abstract class FocusMode
/** Do not perform depth-of-field */
case class FocusNone() extends FocusMode
/**
 * Perform depth-of-field
 * @param count Number of samples to take in each of yaw and pitch
 * @param angle Maximum angle to vary yaw and pitch by
 */
case class FocusSome(count: Int, angle: Double) extends FocusMode