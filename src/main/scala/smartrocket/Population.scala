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

case class Population(var rockets: ArrayBuffer[Rocket],
  size: Int,
  var matingpool: ArrayBuffer[Rocket] = new ArrayBuffer[Rocket])

object Population {

    def createPopulation(size:Int):Population = {
      var rockets = new ArrayBuffer[Rocket]()
      for (i <- 0 to size) {
        rockets += new Rocket(10, new Main.Vector(), new Main.Vector(),
        new Main.Vector(250, 500), false, false,
        Circle(0, 0, 10), new Dna(), 0, 0.0,
        20.0, System.nanoTime)
      }
      rockets

      val population = new Population(rockets, size)
      population
    }

    def selection(population: Population):Unit = {
      var newRockets = new ArrayBuffer[Rocket]()

      for(i <- 0 to population.size){
        var index = Random.nextInt(population.matingpool.size)
        var parentA = population.matingpool(index).dna

        index = Random.nextInt(population.matingpool.size)
        var parentB = population.matingpool(index).dna

        var child = Dna.crossover(parentA, parentB)
        Dna.mutation(child)

        newRockets += Rocket.createRocket(child)
      }
      population.rockets = newRockets
    }

    def evaluate(population: Population, target: Main.Target):Unit = {
      var maxfitness = 0.0
      var minDuration = 20.0

      for(rocket <- population.rockets) {
        rocket.fitness = Rocket.computeFitness(target, rocket)

        if (rocket.fitness > maxfitness) {
          maxfitness = rocket.fitness
        }
        if (rocket.duration < minDuration) {
          minDuration = rocket.duration
        }
      }

      println("maxfitness", maxfitness)
      println("minDuration", minDuration)

      for(rocket <- population.rockets){
        rocket.fitness /= maxfitness
      }

      for(i <- 0 to population.size){
        population.rockets(i).duration /= minDuration
      }

      population.matingpool = new ArrayBuffer[Rocket]()
      for(i <- 0 to population.size){
        var n = (population.rockets(i).fitness * 100) + ((1/population.rockets(i).duration) * 1)
        for(j <- 0 to (n).toInt){
          population.matingpool+=population.rockets(i)
        }
      }
    }
  }
