package com.eshop.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eshop.common.RedisShardedPool;
import redis.clients.jedis.ShardedJedis;

/**
 * 
 * @author Paula Lin
 *
 */
public class RedisShardedPoolUtil {

	private static Logger logger = LoggerFactory.getLogger(RedisShardedPoolUtil.class);
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static String set(String key, String value) {
		ShardedJedis shardedRedis = null;
		String result = null;
		
		try {
			shardedRedis = RedisShardedPool.getJedis();
			result = shardedRedis.set(key, value);
		} catch (Exception e) {
			logger.error("set key:{} value:{} error", key, value, e);
			RedisShardedPool.returnBrokenResource(shardedRedis);
			return result;
		}
		RedisShardedPool.returnResource(shardedRedis);
		return result;
	}
	
	/**
	 * 
	 * @param key
	 * @param exTime
	 * @param value
	 * @return
	 */
	/*
	 * @Param int exTime: 单位是秒
	 * 用户首次登陆的时候需要调用并加上exTime, 后期用户再登陆则调用expire来设置用户的有效期即可
	 */
	public static String setEx(String key, int exTime, String value) {
		ShardedJedis shardedJedis = null;
		String result = null;
		
		try {
			shardedJedis = RedisShardedPool.getJedis();
			result = shardedJedis.setex(key, exTime, value);
		} catch (Exception e) {
			logger.error("set key:{} exTime{} value:{} error", key, exTime, value, e);
			RedisShardedPool.returnBrokenResource(shardedJedis);
			return result;
		}
		RedisShardedPool.returnResource(shardedJedis);
		return result;
	}

	/**
	 * 
	 * @param key
	 * @param exTime
	 * @return
	 */
	/*
	 * 重新设置key的有效期,单位是秒 
	 */
	public static Long expire(String key, int exTime) {
		ShardedJedis shardedJedis = null;
		Long result = null;
		
		try {
			shardedJedis = RedisShardedPool.getJedis();
			//如果超时则返回1, 没有超时则返回0
			result = shardedJedis.expire(key, exTime);
		} catch (Exception e) {
			logger.error("expire key:{} exTime:{} error", key, exTime, e);
			RedisShardedPool.returnBrokenResource(shardedJedis);
			return result;
		}
		RedisShardedPool.returnResource(shardedJedis);
		return result;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		ShardedJedis shardedJedis = null;
		String result = null;
		
		try {
			shardedJedis = RedisShardedPool.getJedis();
			result = shardedJedis.get(key);
		} catch (Exception e) {
			logger.error("get key:{} error", key, e);
			RedisShardedPool.returnBrokenResource(shardedJedis);
			return result;
		}
		RedisShardedPool.returnResource(shardedJedis);
		return result;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static Long del(String key) {
		ShardedJedis shardedJedis = null;
		Long result = null;
		
		try {
			shardedJedis = RedisShardedPool.getJedis();
			result = shardedJedis.del(key);
		} catch (Exception e) {
			logger.error("get key:{} error", key, e);
			RedisShardedPool.returnBrokenResource(shardedJedis);
			return result;
		}
		RedisShardedPool.returnResource(shardedJedis);
		return result;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ShardedJedis shardedJedis = RedisShardedPool.getJedis();
		
		RedisShardedPoolUtil.set("keyTest1", "vale1");
		
		String value = RedisShardedPoolUtil.get("keyTest1");
		
		RedisShardedPoolUtil.setEx("keyEx", 60*10, "valueeex");
		
		RedisShardedPoolUtil.expire("keyTest1", 60*20);
		
		System.out.println("end");
	}
}
