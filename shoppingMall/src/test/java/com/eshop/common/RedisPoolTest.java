/**
 * 
 */
/**
 * @author Paula Lin
 *
 */
package com.eshop.common;

import org.junit.Test;

import redis.clients.jedis.Jedis;

public class RedisPoolTest {

	/**
	 *  
	 */
	//测试redis连接
	@Test
    public void redisPoolConnectionTest() {
		Jedis jedis = RedisPool.getJedis();
		//jedis.setnx判定设置的key是否存在
		jedis.set("paulakey", "paulavalue");
		//jedis使用完后放回连接池中
		RedisPool.returnResource(jedis);
		
		//销毁连接池中的所有连接
		RedisPool.getJedisPool().destroy();//临时调用, 在实际生产环境中不会销毁, 因为业务代码会不断获取redis连接
		
	}

}