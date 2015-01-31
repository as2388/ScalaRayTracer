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
    val size = new Size(320, 160)
    val mimage = new WritableImage(size.width, size.height)
    val pixelWriter = mimage.pixelWrit



    for (iter <- 90 to 270 if iter % 5 == 0 || (iter > 152 && iter < 176)) {
        val time = System.currentTimeMillis()
        println("Intialising RayTracer...")
        val tracer = new RayTracer(size, iter - 180)

        println("Starting trace for image " + iter)

        tracer writeToImage pixelWriter

        println("Tracing complete")

        println("Writing to file " + iter + ".png...")
        val file = new File(iter + ".png")
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