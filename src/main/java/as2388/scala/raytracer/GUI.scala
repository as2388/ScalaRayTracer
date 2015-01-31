package as2388.scala.raytracer

import java.io.File
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO

import scalafx.application.JFXApp
import scalafx.embed.swing.SwingFXUtils
import scalafx.scene.Scene
import scalafx.scene.image.{ImageView, WritableImage}
import scalafx.scene.paint.Color

object GUI extends JFXApp {
    val size = new Size(3200, 1600)
    val mimage = new WritableImage(size.width, size.height)
    val pixelWriter = mimage.pixelWrit



    for (iter <- -200 to 200 if iter % 2 == 0 && (iter < -150 || iter > 150)) {
        val time = System.currentTimeMillis()
        println("Intialising RayTracer...")
        val tracer = new RayTracer(size, iter)

        println("Starting trace for image " + (200 - iter))

        tracer writeToImage pixelWriter

        println("Tracing complete")

        println("Writing to file " + (200 - iter) + ".png...")
        val file = new File("img/" + (200 - iter) + ".png")
        ImageIO.write(SwingFXUtils fromFXImage(mimage, null), "png", file)

        val millis = System.currentTimeMillis() - time
        println("Total time: " +
                TimeUnit.MILLISECONDS.toHours(millis) + ":" +
                TimeUnit.MILLISECONDS.toMinutes(millis) % 60 + ":" +
                TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        )
    }

    stage = new JFXApp.PrimaryStage {
        title.value = "Scala Ray Tracer"
        width = size.width
        height = size.height
        scene = new Scene {
            fill = Color.Red
            content = new ImageView() {
                image = mimage

            }
        }
    }
}