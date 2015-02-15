package as2388.scala.raytracer

import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

import as2388.scala.raytracer.examples.CheckerboardConfiguration

import scalafx.application.JFXApp
import scalafx.embed.swing.SwingFXUtils
import scalafx.scene.Scene
import scalafx.scene.image.{ImageView, WritableImage}

object CommandLine {
    def main(args: Array[String]) = {
        println("Starting RayTracer...")

        val time = System.currentTimeMillis()

        val size = new Size(3200, 1800)

        val bi: BufferedImage = new BufferedImage(3200, 1800, 1)

        val tracer = new RayTracer(new CheckerboardConfiguration(size, 0).getConfiguration)
        tracer writeToImage bi

        val file = new File("test.png")

        println("Writing to file 'test.png'")

        ImageIO.write(bi, "png", file)

        val millis = System.currentTimeMillis() - time
        println("Total time: " +
                TimeUnit.MILLISECONDS.toHours(millis) + ":" +
                TimeUnit.MILLISECONDS.toMinutes(millis) % 60 + ":" +
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        )

        println("Done")
    }
}

object GUI extends JFXApp {
    val size = new Size(3200, 1800)
    val mimage = new WritableImage(size.width, size.height)
    val pixelWriter = mimage.pixelWrit

    println("Initialising RayTracer...")

    val TAU = 2 * Math.PI

    for (iter <- 1 to 8) {
        val tracer = new RayTracer(new CheckerboardConfiguration(size, iter).getConfiguration)

        val time = System.currentTimeMillis()
        tracer writeToImage pixelWriter
        val millis = System.currentTimeMillis() - time
        println("Total time: " +
                TimeUnit.MILLISECONDS.toHours(millis) + ":" +
                TimeUnit.MILLISECONDS.toMinutes(millis) % 60 + ":" +
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        )

        val file = new File(iter + ".png")
        ImageIO.write(SwingFXUtils fromFXImage(mimage, null), "png", file)

    }
    stage = new JFXApp.PrimaryStage {
        title.value = "Scala Ray Tracer"
        width = size.width
        height = size.height
        scene = new Scene {
            content = new ImageView() {
                image = mimage

            }
        }
    }
}