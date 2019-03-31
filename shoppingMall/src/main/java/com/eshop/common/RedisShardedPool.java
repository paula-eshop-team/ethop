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
/*
 * 二期
 * Redis分片连接池
 */
public class RedisShardedPool {
	
	private static Logger logger = LoggerFactory.getLogger(RedisShardedPool.class);
	
    //JedisSharded连接池
	private static ShardedJedisPool shardedJedisPool;
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
    
    private static String redis1Ip = PropertiesUtil.getStringProperty("redis_1.ip");
    private static Integer redis1Port = PropertiesUtil.getIntegerProperty("redis_1.port");
    
    private static String redis2Ip = PropertiesUtil.getStringProperty("redis_2.ip");
    private static Integer redis2Port = PropertiesUtil.getIntegerProperty("redis_2.port");
    
    //初始化连接池, 只能调用一次,而且只能类内部调用
    private static void initPool() {
    	JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    	jedisPoolConfig.setMaxTotal(maxTotal);
    	jedisPoolConfig.setMaxIdle(maxIdle);
    	jedisPoolConfig.setMinIdle(minIdle);
    	
    	jedisPoolConfig.setTestOnBorrow(testOnBorrow);
    	jedisPoolConfig.setTestOnReturn(testOnReturn);
    	
    	//Redis连接耗尽时是否阻塞等待, false会抛出异常, true会阻塞直到超时报TimeoutException, 默认为true
    	jedisPoolConfig.setBlockWhenExhausted(true);
    	
    	JedisShardInfo jedisShardInfo1 = new JedisShardInfo(redis1Ip, redis1Port, 1000*2);//1000*2是超时时间, 2秒
    	//如果redis中有密码, 可以调用如下方法
    	//info1.setPassword(auth);
    	JedisShardInfo jedisShardInfo2 = new JedisShardInfo(redis2Ip, redis2Port, 1000*2);//1000*2是超时时间, 2秒
    	
    	List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>(2);
    	jedisShardInfoList.add(jedisShardInfo1);
    	jedisShardInfoList.add(jedisShardInfo2);
    	
    	//MURMUR_HASH对应的就是一致性哈希算法
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
    	//从ShardedJedisPool获取SharddeJedis
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
    //测试redis连接
    public static void main(String[] args) {
		ShardedJedis shardedJedis = shardedJedisPool.getResource();
		
		for(int i=0; i<10; i++) {
			shardedJedis.set("key"+i, "value"+i);
		}
		
		//jedis使用完后放回连接池中
		returnResource(shardedJedis);
		
		//不需要调用shardedJedisPool.destroy();因为main函数执行完, 内存中的东西就没有了, 包括ShardedJedisPool
		//shardedJedisPool.destroy();
		
		System.out.println("Program is ended.");
	}
}
