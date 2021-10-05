package com.esa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserAuthorizationService {

    private final Logger log = LoggerFactory.getLogger(UserAuthorizationService.class);

    private final RedisTemplate<String, String> redisTemplate;
    
    private final JedisConnectionFactory jedisConnectionFactory;
    

	public UserAuthorizationService(RedisTemplate<String, String> redisTemplate,JedisConnectionFactory jedisConnectionFactory) {
		super();
		this.redisTemplate = redisTemplate;
		this.jedisConnectionFactory = jedisConnectionFactory;
	}
    
	public String getUserId() {
		return "1";
	}
}
