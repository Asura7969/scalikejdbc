package com.gwz.redis

import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.JedisPool

object InternalRedisClient extends Serializable {
            
  @transient
  private var pool: JedisPool = null

  def makePool(redisHost: String, redisPort: Int, redisTimeout: Int,
      maxTotal: Int, maxIdle: Int, minIdle: Int): Unit = {
    makePool(redisHost, redisPort, redisTimeout, maxTotal, maxIdle, minIdle, true, false, 10000)
  }

  def makePool(redisHost: String, redisPort: Int, redisTimeout: Int,
      maxTotal: Int, maxIdle: Int, minIdle: Int, testOnBorrow: Boolean,
      testOnReturn: Boolean, maxWaitMillis: Long): Unit = {
    if(pool == null) {
         val poolConfig = new GenericObjectPoolConfig()
         poolConfig.setMaxTotal(maxTotal)
         poolConfig.setMaxIdle(maxIdle)
         poolConfig.setMinIdle(minIdle)
         poolConfig.setTestOnBorrow(testOnBorrow)
         poolConfig.setTestOnReturn(testOnReturn)
         poolConfig.setMaxWaitMillis(maxWaitMillis)
         pool = new JedisPool(poolConfig, redisHost, redisPort, redisTimeout)

         val hook = new Thread{
           override def run = pool.destroy()
         }
         sys.addShutdownHook(hook.run)
    }
  }

  def getPool: JedisPool = {
    assert(pool != null)
    pool
  }

  // Redis configurations
  val maxTotal = 10
  val maxIdle = 10
  val minIdle = 1
  val redisHost = "10.10.4.130"
  val redisPort = 6379
  val redisTimeout = 30000
  val dbIndex = 1
  InternalRedisClient.makePool(redisHost, redisPort, redisTimeout, maxTotal, maxIdle, minIdle)

  val uid = "1"
  val clickCount = 2L
  val clickHashKey = "app::users::click"
  val jedis =InternalRedisClient.getPool.getResource
  jedis.select(dbIndex)
  jedis.hincrBy(clickHashKey, uid, clickCount)
  InternalRedisClient.getPool.returnResource(jedis)
}