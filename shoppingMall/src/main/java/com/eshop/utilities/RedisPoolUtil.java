package com.eshop.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eshop.common.RedisPool;
import redis.clients.jedis.Jedis;

/**
 * 
 * @author Paula Lin
 *
 */
public class RedisPoolUtil {
	
	private static Logger logger = LoggerFactory.getLogger(RedisPoolUtil.class);
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public static String set(String key, String value) {
		Jedis jedis = null;
		String result = null;
		
		try {
			jedis = RedisPool.getJedis();
			result = jedis.set(key, value);
		} catch (Exception e) {
			logger.error("set key:{} value:{} error", key, value, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
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
		Jedis jedis = null;
		String result = null;
		
		try {
			jedis = RedisPool.getJedis();
			result = jedis.setex(key, exTime, value);
		} catch (Exception e) {
			logger.error("set key:{} exTime{} value:{} error", key, exTime, value, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
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
		Jedis jedis = null;
		Long result = null;
		
		try {
			jedis = RedisPool.getJedis();
			//如果超时则返回1, 没有超时则返回0
			result = jedis.expire(key, exTime);
		} catch (Exception e) {
			logger.error("expire key:{} exTime:{} error", key, exTime, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		Jedis jedis = null;
		String result = null;
		
		try {
			jedis = RedisPool.getJedis();
			result = jedis.get(key);
		} catch (Exception e) {
			logger.error("get key:{} error", key, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static Long del(String key) {
		Jedis jedis = null;
		Long result = null;
		
		try {
			jedis = RedisPool.getJedis();
			result = jedis.del(key);
		} catch (Exception e) {
			logger.error("get key:{} error", key, e);
			RedisPool.returnBrokenResource(jedis);
			return result;
		}
		RedisPool.returnResource(jedis);
		return result;
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Jedis jedis = RedisPool.getJedis();
		
		RedisPoolUtil.set("keyTest1", "vale1");
		
		String value = RedisPoolUtil.get("keyTest1");
		
		RedisPoolUtil.setEx("keyEx", 60*10, "valueeex");
		
		RedisPoolUtil.expire("keyTest1", 60*20);
		
		System.out.println("end");
	}
}
