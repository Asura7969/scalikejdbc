package com.gwz.dbPool

object Test {
  def main(args: Array[String]): Unit = {
    MySqlConn.initConnectionPool()

//    MySqlConn.insert("reportBike_zhouqi","20180705","1-1",1.0)
//    MySqlConn.update("reportBike_zhouqi","20180702","1-2",1.0)

    val list = MySqlConn.getByName("reportBike_zhouqi","20180702")
    //println(list.toString)
    val arrayList = MySqlConn.getByColumName("reportBike_zhouqi","1-1")
    println(arrayList.toString)
  }

}
