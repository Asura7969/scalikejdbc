package com.oop

//class Teacher {
//  var name:String = _
//  private var age = 23
//  private [this] val gender = "male" //只属于单个实例
//
//  def this(name:String){
//    //默认构造器
//    this
//    this.name = name
//  }
//
//  def sayHello(): Unit ={
//    println(this.name + ":" + this.age + ":" + this.gender)
//  }
//
//}

//加 private 关键字后，不能使用 主构造器
class Teacher private (val name:String,val age:Int) {
//class Teacher(val name:String,val age:Int) {
  println("This is primary constructor!")
  var gender:String = _
  println(gender)
  def this(name:String,age:Int,gender:String){
    this(name,age)
    this.gender = gender
  }
}

object OopMain{

  def main(args: Array[String]): Unit = {
//    val techer = new Teacher
//    techer.name = "Spark"
//    techer.sayHello()

//    val techer = new Teacher("Spark",5)
    val p = new Teacher("Spark",5,"male")
    println(p.age)
  }
}

