package com.hellobike

import scalikejdbc._
import scalikejdbc.config.DBs

/**
 * Hello world!
 *
 */
object LoadConf {
  def main(args: Array[String]): Unit = {

    //**********使用默认配置*****************************************************************************
    DBs.setup()
    val list:List[String] = DB readOnly(implicit session =>{
      sql"""SELECT car FROM top10_speed_detail""".map(rs =>rs.string("car")).list().apply()
    })

    /*for (elem <- list) {
      println(elem)
    }*/

    //**********使用自定义配置***************************************************************************
    //实体类
    case class TopNMonitor(task_id:String,monitor_id:String,carCount:Int)

    DBs.setup('sheep)
    val listTopN = NamedDB('sheep) readOnly(implicit session=>{
      sql"SELECT * FROM topn_monitor_car_count".map(
        rs => TopNMonitor(rs.string("task_id"),rs.string("monitor_id"),rs.int("carCount"))
      ).list().apply()
    })

    for (elem <- listTopN) {
      println(elem.toString)
    }
    println(listTopN.size)

    //**********插入数据*****************************************************************************
    val insertResult: Int = DB.autoCommit(implicit session => {
      SQL("insert into topn_monitor_car_count(task_id,monitor_id,carCount) values(?,?,?)")
        .bind("1", "0009", "7000")
        .update()
        .apply()
    })
    println(s"成功插入${insertResult}条数据")

    //**********创建表,并插入数据返回主键(演示另一种加载方式,不使用配置文件)********************************************************************
    Class.forName("com.mysql.jdbc.Driver")
    val url = "jdbc:mysql://192.168.217.11/traffic?characterEncoding=utf-8"
    val username = "root"
    val password = "root"
    val settings = ConnectionPoolSettings(
      initialSize = 5,maxSize = 20,
      connectionTimeoutMillis = 3000L,
      validationQuery = "select 1 from dual"
    )

    ConnectionPool.singleton(url,username,password,settings)



  }
}

