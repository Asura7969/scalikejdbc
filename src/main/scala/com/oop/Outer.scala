package com.oop

class Outer (val name:String){outer =>
  class Inner(val name:String){
    def foo(b:Inner) = println("Outer:" + outer.name + ",Inner:" + b.name)
  }

}
//内部类隶属于外部类的实例本身
object OOpInner{
  def main(args: Array[String]): Unit = {
    val outer1 = new Outer("Spark")
    val outer2 = new Outer("Flink")

    val inner1 = new outer1.Inner("Scala")
    val inner2 = new outer2.Inner("hadoop")

    inner1.foo(inner1)
    inner2.foo(inner2)
  }

}
