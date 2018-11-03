package com.oop

class ApplyOperation {
  def apply() = println("I am into Spark so match")
  def havaATry: Unit ={
    println("Have a try on apply!")
  }
}


object ApplyOperation {
  def apply() = {
    println("I am into Scala so match!!!")
    new ApplyOperation
  }
}

object ApplyTest{
  def main(args: Array[String]): Unit = {
    val array = Array(1,2,3,4,5)
    val a = ApplyOperation()
    a.havaATry
    println(a())
  }
}

