package smartrocket

import scala.util.Random
import scala.collection.mutable.ArrayBuffer

case class Dna(var genes:ArrayBuffer[Main.Vector]=Dna.createGenes(),
lenght:Int=Main.dna_length,
maxforce: Double=0.5)

object Dna {

  def createDna(length:Int=Main.dna_length, maxforce:Double=0.5):Dna = {
    var genes = new ArrayBuffer[Main.Vector]()
    for (i <- 0 to length) {
      var angle = Math.random() * 2 * Math.PI
      genes += Main.Vector(Math.cos(angle)*maxforce, Math.sin(angle)*maxforce)
    }

    val dna = new Dna(genes, length, maxforce)
    dna
  }

  def createGenes(length:Int=Main.dna_length, maxforce:Double=0.5):ArrayBuffer[Main.Vector] = {
    var genes = new ArrayBuffer[Main.Vector]()
    for (i <- 0 to length) {
      var angle = Math.random() * 2 * Math.PI
      genes += Main.Vector(Math.cos(angle)*maxforce, Math.sin(angle)*maxforce)
    }
    genes
  }

  def crossover(dna: Dna,partner: Dna):Dna = {
    val rand = new Random()
    val mid = rand.nextInt(dna.genes.size)

    val left = partner.genes.splitAt(mid)
    val right = dna.genes.splitAt(mid)

    val new_genes = right._1 ++ left._2

    new Dna(new_genes)
  }

  def mutation(dna: Dna):Unit = {
    for (i <- 0 to dna.genes.size -1){
      if(Math.random() < 0.005){
        val angle = Math.random() * 2 * Math.PI
        dna.genes(i) = Main.Vector(Math.cos(angle), Math.sin(angle))
      }
    }
  }
}
