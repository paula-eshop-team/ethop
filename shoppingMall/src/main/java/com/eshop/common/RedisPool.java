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
	
	//Jedis连接池
	private static JedisPool pool;//申明为staic是为了保证JedisPool在tomcat启动时就加载出来
	//maxTotal控制Redis连接池中与Jedis的最大连接数
	private static Integer maxTotal = PropertiesUtil.getIntegerProperty("redis.max.total", 20);
	//在Jedis连接池中最多有多少状态为Idle的Jedis实例
	//Jedis连接池中就是Jedis的实例, Idle就是空闲,,即Jedis连接池中的空闲Jedis实例, 如果需要的话就可以使用
    //maxIdle为Jedis连接池中最大的空闲(Idle状态)的Jedis实例个数
    private static Integer maxIdle = PropertiesUtil.getIntegerProperty("redis.max.idle", 10);
    //minIdle为Jedis连接池中最小的空闲(Idle状态)的Jedis实例个数
    private static Integer minIdle = PropertiesUtil.getIntegerProperty("redis.min.idle", 2);
    //程序从JedisPool中borrow一个Jedis实例时, 是否需要进行测试, 如果Jedis实例或连接是ok的, 则验证结果会返回true;否则, 则验证结果返回 
    private static Boolean testOnBorrow = PropertiesUtil.getBooleanProperty("redis.test.borrow", true);//申明为true, 表示每次获取的Jedis实例(即Redis连接)肯定是可用的
    //程序在还Jedis实例时是否需要验证操作, 如果赋值为true, 代表每次还的肯定是可用的Redis连接(即可用的Jedis实例), 对于broken的Jedis实例进行丢弃
    //为了明确return时是放回正常resource还是brokenResource,可以将testOnReturn设置为false, 比如调用jedis时发生了异常, 那么异常处理就放到brokenResource中, 即返回时不需要验证了, 这样提高连接的效率
    private static Boolean testOnReturn = PropertiesUtil.getBooleanProperty("redis.test.return", true);
    
    private static String redisIp = PropertiesUtil.getStringProperty("redis.ip");
    private static Integer redisPort = PropertiesUtil.getIntegerProperty("redis.port");
    
    /**
	 * 
	 */
    //初始化连接池, 只能调用一次,而且只能类内部调用
    private static void initPool() {
    	JedisPoolConfig config = new JedisPoolConfig();
    	config.setMaxTotal(maxTotal);
    	config.setMaxIdle(maxIdle);
    	config.setMinIdle(minIdle);
    	
    	config.setTestOnBorrow(testOnBorrow);
    	config.setTestOnReturn(testOnReturn);
    	
    	//Redis连接耗尽时是否阻塞等待, false会抛出异常, true会阻塞直到超时报TimeoutException, 默认为true
    	config.setBlockWhenExhausted(true);
    	
    	//超时时间是1000*2毫秒
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
