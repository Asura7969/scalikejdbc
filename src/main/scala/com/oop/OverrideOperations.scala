package com.oop

class OverrideOperations {

}

class Person(val name:String,var age:Int){
  println("The primary constructor of Person")

  val school = "BJU"

  def sleep = "8 hours"

  override def toString: String = "I am a Person1"
}

class Worker(name:String,age:Int,val salary:Long) extends Person(name,age){
  println("This is subClass of Person,Primary constructor of Worker")

  override val school: String = "Spark"

  override def toString: String = "I am a Worker," + super.sleep
}

object OverrideOperations{
  def main(args: Array[String]): Unit = {
    val w = new Worker("Spark",5,10000)
    println("School:" + w.school)
    println("salary:" + w.salary)
    println(w.toString)
  }
}
