package com.esa.service;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisServiceService {

    private final Logger log = LoggerFactory.getLogger(RedisServiceService.class);

    private final RedisTemplate<String, String> redisTemplate;
    
    private final JedisConnectionFactory jedisConnectionFactory;
    

	public RedisServiceService(RedisTemplate<String, String> redisTemplate,JedisConnectionFactory jedisConnectionFactory) {
		super();
		this.redisTemplate = redisTemplate;
		this.jedisConnectionFactory = jedisConnectionFactory;
	}
    
	public void add(String key,String value) {
		redisTemplate.opsForValue().set(key, value);
	}
	
	public void addByTtl(String key,String value,Long duration) {
		redisTemplate.opsForValue().set(key, value, duration, TimeUnit.HOURS);
	}
	
	public void remove(String key) {
		redisTemplate.delete(key);
	}
	
	public String get(String key) {
		return redisTemplate.opsForValue().get(key);
	}
	
	public void addToSet(String key,String value) {
		redisTemplate.opsForSet().add(key, value);
	}
	
	public Set<String> getKeyList(String pattern) {
		return redisTemplate.keys(pattern+"*");
	}
	
	public Set<String> retrieveSet(String key) {
		return redisTemplate.opsForSet().members(key);
	}
	
	public void setExpireDate(String key,Date date) {
		redisTemplate.expireAt(key, date);
	}
	
	public void removeFromSet(String key,String value) {
		redisTemplate.opsForSet().remove(key, value);
	}
	
	public Boolean existInSet(String key,String value) {
		return redisTemplate.opsForSet().isMember(key, value);
	}
	
	public Long sizeOfSet(String key) {
		return redisTemplate.opsForSet().size(key);
	}
	
	public void increment(String key) {
		redisTemplate.opsForValue().increment(key, 1);
	}

	public String flushAll() {
		jedisConnectionFactory.getConnection().flushAll();
		return "flushed"	;	
	}
}
