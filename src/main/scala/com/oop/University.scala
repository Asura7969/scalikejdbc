package com.oop

class University {
  val id = University.newStudentNo
  private var number = 0
  def aClass(number:Int){this.number += number}

}

object University{
  private var studentNo = 0
  def newStudentNo = {
    studentNo += 1
    studentNo
  }
}

object Ops{
  def main(args: Array[String]): Unit = {
    println(University.newStudentNo)
    println(University.newStudentNo)
  }
}
