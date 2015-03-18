package as2388.scala.raytracer

import as2388.scala.raytracer.shapes.Shape

trait Configuration

class StaticConfiguration(
       val imageSize: Size,
       val shapes: List[Shape],
       val lights: List[PointLight],
       val ambientIntensity: Float = 0.1f,
       val camera: Camera,
       val singularities: List[Singularity] = Nil,
       val singularityDepthLimit: Float = 100,
       val enableShadows: Boolean = true,
       val enableDiffuse: Boolean = true,
       val enableReflections: Boolean = true,
       val antiAliasingMode: AntiAliasingMode = new AntiAliasingNone,
       val focusMode: FocusMode = new FocusNone
) extends Configuration

class AnimatedConfiguration(val configurations: List[StaticConfiguration]) extends Configuration

abstract class AntiAliasingMode
/** No anti-aliasing */
case class AntiAliasingNone() extends AntiAliasingMode
/** Anti-alias from samples taken at regular intervals, with count samples taken in each direction x and y */
case class AntiAliasingRegular(count: Int) extends AntiAliasingMode
/** Anti-aliasing mode designed for use with focus: takes one random sample, so anti-aliasing relies on the focus
  * routine taking many samples in order to work */
case class AntiAliasingFocus() extends AntiAliasingMode

abstract class FocusMode
/** Do not perform depth-of-field */
case class FocusNone() extends FocusMode
/**
 * Perform depth-of-field
 * @param count Number of samples to take in each of yaw and pitch
 * @param angle Maximum angle to vary yaw and pitch by
 */
case class FocusSome(count: Int, angle: Float) extends FocusMode
