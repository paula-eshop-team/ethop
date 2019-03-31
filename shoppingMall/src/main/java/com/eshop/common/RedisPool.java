package com.eshop.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eshop.utilities.PropertiesUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @author Paula Lin
 *
 */
public class RedisPool {
	
	private static Logger logger = LoggerFactory.getLogger(RedisPool.class);
	private static JedisPool pool;//申明为staic是为了保证JedisPool在tomcat启动时就加载出来
	private static Integer maxTotal = PropertiesUtil.getIntegerProperty("redis.max.total", 20);
    private static Integer maxIdle = PropertiesUtil.getIntegerProperty("redis.max.idle", 10);
    private static Integer minIdle = PropertiesUtil.getIntegerProperty("redis.min.idle", 2);
    private static Boolean testOnBorrow = PropertiesUtil.getBooleanProperty("redis.test.borrow", true);//申明为true, 表示每次获取的Jedis实例(即Redis连接)肯定是可用的
    private static Boolean testOnReturn = PropertiesUtil.getBooleanProperty("redis.test.return", true);
    private static String redisIp = PropertiesUtil.getStringProperty("redis.ip");
    private static Integer redisPort = PropertiesUtil.getIntegerProperty("redis.port");
    
    private static void initPool() {
    	JedisPoolConfig config = new JedisPoolConfig();
    	config.setMaxTotal(maxTotal);
    	config.setMaxIdle(maxIdle);
    	config.setMinIdle(minIdle);
    	
    	config.setTestOnBorrow(testOnBorrow);
    	config.setTestOnReturn(testOnReturn);
    	config.setBlockWhenExhausted(true);
    	pool = new JedisPool(config, redisIp, redisPort, 1000*2);
    }

    static {
    	initPool();
    }
    
    public static JedisPool getJedisPool() {
    	return pool;
    }
    /**
     * @return
     */
    public static Jedis getJedis() {
    	return pool.getResource();
    }
    
    /**
	 * @param httpServletRequest
     * @param httpServletResponse
     * @param object
     * @param ex 
     * @return
	 */
    public static void returnResource(Jedis jedis) {
    	pool.returnResource(jedis);
    }
    
    /**
     * @param jedis
     */
    public static void returnBrokenResource(Jedis jedis) {
    	pool.returnBrokenResource(jedis);
    }
}
