package com.gwz

import scalikejdbc.{ConnectionPool, ConnectionPoolSettings, DB, SQL}

object NoLoadConf {
  def main(args: Array[String]): Unit = {
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

    val insertResult: Int = DB.autoCommit(implicit session => {
      SQL("insert into topn_monitor_car_count(task_id,monitor_id,carCount) values(?,?,?)")
        .bind("1", "0010", "7001")
        .update()
        .apply()
    })
    println(s"成功插入${insertResult}条数据")
  }

}
