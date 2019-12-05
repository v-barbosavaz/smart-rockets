package smartrocket

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.effect.DropShadow
import scalafx.scene.layout.HBox
import scalafx.scene.paint.Color._
import scalafx.scene.paint._
import scalafx.scene.text.Text
import scalafx.scene.shape._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import scalafx.animation._
import scala.util.Random
import scalafx.collections.ObservableBuffer
import javafx.collections.{ListChangeListener, ObservableList}
import scala.util.Try
import scalafx.scene.layout.{BorderPane, Pane}

object Main extends JFXApp {

  var count = 0
  var lifespan = 300
  val dna_length = 200

  val width = 500 // width of the screen
  val height = 500 // height of the screen

  var gen_num = 0

  sealed trait Element

  case class Obstacle(x: Int, y: Int, width: Int, height: Int) extends Element
  val obstacle = Obstacle(80, 350, 250, 10)

  case class Target(x: Int, y: Int, radius: Int) extends Element
  val target = Target(250, 200, 10)

  def createObject(el: Element): Shape = el match {
    case Target(x, y, radius) => Circle(x, y, radius)
    case Obstacle(x, y, width, height) => Rectangle(x, y, width, height)
  }

  case class Vector(var x: Double=0, var y: Double=0)

  val rectangle = createObject(obstacle)
  rectangle.fill = Color.rgb(238,101,90)

  val circle = createObject(target)
  circle.fill = Color.rgb(115,190,71)

  stage = new PrimaryStage {
    title = "Smart Rockets"
    scene = new Scene(500, 500) {
      fill = Color.rgb(50, 50, 50)

      content.add(rectangle)
      content.add(circle)

      var population = Population.createPopulation(50)

      for (el <- population.rockets) {
        content.add(el.circle)
        el.circle.fill = Color.rgb(226, 191, 76)
      }

      var lastTime = 0L
      val speed = 2
      val direction = -1

      var ended = 0

      val timer = AnimationTimer(t =>{
        if(lastTime>0){

          val delta = (t-lastTime) / 1e9

          if(count > lifespan){
            Population.evaluate(population, Main.target)
            Population.selection(population)

            content = None
            content.add(rectangle)
            content.add(circle)

            for(el <- population.rockets){
              content.add(el.circle)
              el.circle.fill = Color.rgb(226, 191, 76)
            }
            count = 0

            gen_num += 1
            println("Generation number : ", gen_num)
            println("---------------------")
          }

          count += 1

          for(rocket <- population.rockets){
            rocket.circle.centerX = rocket.pos.x
            rocket.circle.centerY = rocket.pos.y
            Rocket.update(rocket)

            if(rocket.completed == true || rocket.crashed == true){
              ended += 1
            }
            else{
              ended = 0
            }
            if(ended == population.size){
              count = lifespan
            }
          }
        }
        lastTime = t
      })
      timer.start
    }
  }
}
