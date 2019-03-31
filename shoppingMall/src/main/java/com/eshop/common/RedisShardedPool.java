package com.eshop.common;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eshop.utilities.PropertiesUtil;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

/**
 * 
 * @author Paula Lin
 *
 */
public class RedisShardedPool {
	
	private static Logger logger = LoggerFactory.getLogger(RedisShardedPool.class);
	private static ShardedJedisPool shardedJedisPool;
	private static Integer maxTotal = PropertiesUtil.getIntegerProperty("redis.max.total", 20);
    private static Integer maxIdle = PropertiesUtil.getIntegerProperty("redis.max.idle", 10);
    private static Integer minIdle = PropertiesUtil.getIntegerProperty("redis.min.idle", 2);
    private static Boolean testOnBorrow = PropertiesUtil.getBooleanProperty("redis.test.borrow", true);//申明为true, 表示每次获取的Jedis实例(即Redis连接)肯定是可用的
    private static Boolean testOnReturn = PropertiesUtil.getBooleanProperty("redis.test.return", true);
    private static String redis1Ip = PropertiesUtil.getStringProperty("redis_1.ip");
    private static Integer redis1Port = PropertiesUtil.getIntegerProperty("redis_1.port");
    private static String redis2Ip = PropertiesUtil.getStringProperty("redis_2.ip");
    private static Integer redis2Port = PropertiesUtil.getIntegerProperty("redis_2.port");
    
    private static void initPool() {
    	JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    	jedisPoolConfig.setMaxTotal(maxTotal);
    	jedisPoolConfig.setMaxIdle(maxIdle);
    	jedisPoolConfig.setMinIdle(minIdle);
    	
    	jedisPoolConfig.setTestOnBorrow(testOnBorrow);
    	jedisPoolConfig.setTestOnReturn(testOnReturn);
    	
    	jedisPoolConfig.setBlockWhenExhausted(true);
    	
    	JedisShardInfo jedisShardInfo1 = new JedisShardInfo(redis1Ip, redis1Port, 1000*2);//1000*2是超时时间, 2秒
    	JedisShardInfo jedisShardInfo2 = new JedisShardInfo(redis2Ip, redis2Port, 1000*2);//1000*2是超时时间, 2秒
    	List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>(2);
    	jedisShardInfoList.add(jedisShardInfo1);
    	jedisShardInfoList.add(jedisShardInfo2);
    	shardedJedisPool = new ShardedJedisPool(jedisPoolConfig, jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static {
    	initPool();
    }

    /**
     * 
     * @return
     */
    public static ShardedJedis getJedis() {
    	return shardedJedisPool.getResource();
    }
    
    /**
     * 
     * @param shardedJedis
     */
    public static void returnResource(ShardedJedis shardedJedis) {
    	shardedJedisPool.returnResource(shardedJedis);
    }
    
    /**
     * 
     * @param shardedJedis
     */
    public static void returnBrokenResource(ShardedJedis shardedJedis) {
    	shardedJedisPool.returnBrokenResource(shardedJedis);
    }
    
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		
		for(int i=0; i<10; i++) {
			shardedJedis.set("key"+i, "value"+i);
		}
		returnResource(shardedJedis);
		System.out.println("Program is ended.");
	}
}
