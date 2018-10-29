package com.gwz

import scalikejdbc.{ConnectionPool, DB, SQL, WrappedResultSet}
import scalikejdbc.config.DBsWithEnv

import scala.collection.immutable

object DevConfConnection {
  def main(args: Array[String]): Unit = {

    DBsWithEnv("dev").setupAll()
    case class WordCount(word:String,count:Int)

    val allColums = (rs:WrappedResultSet) => WordCount(word = rs.string("word"),count = rs.int("count"))
    val list = DB readOnly(implicit session=>{
      SQL("select * from wordcount").map(allColums).list().apply()
    })

    for (elem <- list) {
      println(elem.toString)
    }

    println("创建表")
    val isSuccess = DB.autoCommit(implicit session => {
      SQL("create table if not exists dotadata(id int(20) not null AUTO_INCREMENT,name varchar(30),primary key(id))").execute().apply()
    })
    //创建成功为 false
    if(isSuccess) println("创建失败") else println("创建成功")

    //插入数据并返回主键
    val insertResult= DB.autoCommit(implicit session => {
      SQL("insert into dotadata(name) values(?)")
        .bind("nabi")
        .updateAndReturnGeneratedKey("id")
        .apply()
    })
    println(s"插入数据的主键为：$insertResult")

    val selectResult = DB.readOnly(implicit session => {
      SQL("select * from dotadata where id = ?")
        .bind(1).map(rs => (rs.int("id"),rs.string("name"))).list().apply()
    })
    for (elem <- selectResult) {
      println(elem.toString())
    }


    val delResult: Int = DB.autoCommit(implicit session => {
      SQL("delete from dotadata where id = ?")
        .bind(insertResult)
        .update()
        .apply()
    })
    println(s"成功删除${delResult}条数据")

    val ls = List[String]("aa","bb","cc")

    val batchInsertParam: immutable.Seq[List[String]] = for(el <- ls) yield List(el)

    //使用事务插入数据
    val tx= DB.localTx(implicit session => {
      SQL("INSERT INTO dotadata(`name`) VALUES (?)").bind("kaka1").update().apply()
      var l = 1/0
      SQL("INSERT INTO dotadata(`name`) VALUES (?)").bind("kaka2").update().apply()

      //批量操作
      SQL("INSERT INTO dotadata(`name`) VALUES (?)").batch(batchInsertParam:_*).apply()
    })

    println(s"tx:$tx")


    val count = 100
    /*(ConnectionPool.borrow()){
      conn:java.sql.Connection => {
        val db :DB = DB(conn)

        db.autoClose(false)

        db.localTx(implicit session=>{
          SQL(s"update wordcount set count = ${count} where word = 'dota'").update().apply()
        })

        val list = db.localTx(implicit session=>{
          SQL("select * from wordcount").map(allColums).list().apply()
        })

        for (elem <- list) {
          println(elem.toString)
        }
      }
    }*/
    DBsWithEnv("dev").closeAll()
  }
}
