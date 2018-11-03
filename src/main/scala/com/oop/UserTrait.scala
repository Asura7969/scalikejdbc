package com.oop

class UserTrait {

}

trait Logger{
  def log(msg:String)
}

class ConcreateLogger extends Logger with Cloneable {

  override def log(msg: String): Unit = println("Log:" + msg)
  def concreateLog: Unit ={
    log("It's me !!! ")
  }
}

trait TraitLogger extends Logger{
  override def log(msg: String): Unit = println("TraitLogger Log content is : " + msg)
}

trait TraitLoggered{
  def loged(msg:String): Unit ={
    println("TraitLoggered Log content is :" + msg)
  }
}



class Human{
  println("Human")
}

trait ITeacher extends Human{
  println("TTeacher")
  def teach
}

trait PianoPlayer extends Human{
  println("PianoPlayer")
  def playPiano = println("I'm playing piano.")
}

class PianoTeacher extends Human with ITeacher with PianoPlayer{
  override def teach: Unit = println("I'm training students.")
}


//AOP
trait Action{
  def doAction
}

trait TBeforeAfter extends Action{
  abstract override def doAction: Unit = {
    println("Initialization")
    super.doAction
    println("Destroyed")
  }
}

class Work extends Action{
  override def doAction: Unit = println("Working...")
}

object UserTrait extends App{
  val w = new Work with TBeforeAfter
  w.doAction
}

