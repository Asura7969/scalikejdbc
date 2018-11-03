package com.oop

class PackageOps {}

package spark.navigation{
  abstract class Navigator{
    def act
  }

  package tests{
    //在spark.navigation.tests包里
    class NavigatorSuite
  }

  package impls{
    class Action extends Navigator{
      override def act: Unit = println("Action method:act")
    }
  }
}


package hadoop{
  package navigation{
    class Navigator
  }

  package launch{
    class Booster{
      val nav = new navigation.Navigator
    }
  }
}


object PackageOps{
  def main(args: Array[String]): Unit = {

  }
}




