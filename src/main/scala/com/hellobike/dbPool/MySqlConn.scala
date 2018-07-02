package com.hellobike.dbPool

import java.sql.{Connection, Date, DriverManager}
import java.util

object MySqlConn {

  private val pools = new util.LinkedList[Connection]() //连接池
  private var current_num = 0 //当前连接池已产生的连接数
  private val max_connection = 10 //连接池总数
  private val connection_num = 5 //产生连接数


  // for test env
  val mysqlConfTest = collection.mutable.Map(
    "driver" -> "org.postgresql.Driver",
    "url" -> "jdbc:postgresql://node01:5432/exampledb",
    "username" -> "dbuser",
    "password" -> "123456"
  )

  // for prod env
  val mysqlConfProd = collection.mutable.Map(
    "driver" -> "org.postgresql.Driver",
    "url" -> "jdbc:postgresql://node01:5432/exampledb",
    "username" -> "dbuser",
    "password" -> "123456"
  )

  /**
    * 获得连接
    */
  private def initConn(): Connection = {
    DriverManager.getConnection(mysqlConfTest("url"), mysqlConfTest("username"), mysqlConfTest("password"))
  }

  /**
    * 初始化连接池
    */
  def initConnectionPool(): util.LinkedList[Connection] = {
    AnyRef.synchronized({
      if (pools.isEmpty()) {
        getMysqlConn()
        for (i <- 1 to connection_num.toInt) {
          pools.push(initConn())
          current_num += 1
        }
      }
      pools
    })
  }

  /**
    * 加载驱动
    */
  private def getMysqlConn() {
    if (current_num > max_connection.toInt && pools.isEmpty()) {
      print("busyness")
      Thread.sleep(2000)
      getMysqlConn()
    } else {
      Class.forName(mysqlConfTest("driver"))
    }
  }

  /**
    * 获得连接
    */
  def getConn():Connection={
    //initConnectionPool()
    pools.poll()
  }
  /**
    * 释放连接
    */
  def releaseCon(con:Connection){
    pools.push(con)
  }

  /**
    * 查询所有漏斗
    * @return
    */
  def getUser(dateStr: String): collection.mutable.Map[String, Date] = {
    // connect to the database named "mysql" on the localhost
    val conn = getConn

    val user = collection.mutable.Map[String, Date]()

    try {
      // create the statement, and run the select query
      val statement = conn.createStatement()
      val sql = s"""select * from user_tbl""".stripMargin
      println(sql)
      val resultSet = statement.executeQuery(sql)
      while (resultSet.next()) {
        val name = resultSet.getString("name")
        val signup_date = resultSet.getDate(2)

        user += (name -> signup_date)
      }
    } catch {
      case e:Throwable => {
        println("UserGO failed! Coursed by getFunnels error")
        e.printStackTrace()
        System.exit(1)
      }
    } finally {
      releaseCon(conn)
    }
    user
  }

  def getByName(tableName:String,name: String): util.ArrayList[Double] = {
    // connect to the database named "mysql" on the localhost
    val conn = getConn

    val list:util.ArrayList[Double] = new util.ArrayList[Double]()

    try {
      // create the statement, and run the select query
      val statement = conn.createStatement()
      val sql = s"""SELECT * FROM $tableName WHERE name='$name'""".stripMargin
      println(sql)
      val resultSet = statement.executeQuery(sql)
      while (resultSet.next()) {
        for(i <- 3 to 6){//去除id和name
          val data = resultSet.getDouble(1)
          list.add(data)
        }
      }

    } catch {
      case e:Throwable => {
        e.printStackTrace()
        System.exit(1)
      }
    } finally {
      releaseCon(conn)
    }
    list
  }


  def getByColumName(tableName:String,columName: String): util.ArrayList[Double] = {
    // connect to the database named "mysql" on the localhost
    val conn = getConn

    val list:util.ArrayList[Double] = new util.ArrayList[Double]()

    try {
      // create the statement, and run the select query
      val statement = conn.createStatement()
      val sql = s"""SELECT "$columName" FROM $tableName """.stripMargin
      println(sql)
      val resultSet = statement.executeQuery(sql)
      while (resultSet.next()) {
        val data = resultSet.getDouble(1)
        list.add(data)
      }

    } catch {
      case e:Throwable => {
        e.printStackTrace()
        System.exit(1)
      }
    } finally {
      releaseCon(conn)
    }
    list
  }


  def insert(tableName:String,name:String,cloumnName:String,value:Double)={

    val sql = s"""insert into $tableName(name,"${cloumnName}") values($name,$value)"""
    println(sql)
    val conn = getConn
    val statement = conn.createStatement()
    statement.execute(sql)
    releaseCon(conn)
  }

  def update(tableName:String,name:String,cloumnName:String,value:Double)={

    val sql = s"""update $tableName set "${cloumnName}"=$value where name='$name'"""
    println(sql)
    val conn = getConn
    val statement = conn.createStatement()
    statement.execute(sql)
    releaseCon(conn)
  }

  /**
    * 更新运行状态至rpt_funnel_manage表的done字段
    * 0 未执行 1 执行中 2 执行完成 3 sql执行失败 4 dump执行失败
    * @param userId
    * @param name
    */
  def updateUser(userId: Int, name: String): Unit = {
    // create database connection
    val conn = getConn

    try {
      val ps = conn.prepareStatement("update user_tbl set name=? where name=?")

      // set the preparedstatement parameters
      ps.setInt(1, userId)
      ps.setString(2, name)

      // call executeUpdate to execute our sql update statement
      val res = ps.executeUpdate()
      ps.close()
    } catch {
      case e:Throwable => e.printStackTrace()
    } finally {
      releaseCon(conn)
    }
  }

  private def deleteByName(name: String): Int = {
    var status = 0
    val conn = getConn
    try {
      val sql = s"delete from user_tbl where name = '$name'"
      val st = conn.createStatement()
      status = st.executeUpdate(sql)

    } catch {
      case e:Throwable => e.printStackTrace()
    } finally {
      releaseCon(conn)
    }
    status
  }

  def main(args: Array[String]): Unit = {
    MySqlConn.deleteByName("张二")
  }
}