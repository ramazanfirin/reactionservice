package com.esa.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Reactionservice.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

	String redisHost;
	Long redisPort;
	Long expireTimeInHours;
	
	
	public String getRedisHost() {
		return redisHost;
	}
	public void setRedisHost(String redisHost) {
		this.redisHost = redisHost;
	}
	public Long getRedisPort() {
		return redisPort;
	}
	public void setRedisPort(Long redisPort) {
		this.redisPort = redisPort;
	}
	public Long getExpireTimeInHours() {
		return expireTimeInHours;
	}
	public void setExpireTimeInHours(Long expireTimeInHours) {
		this.expireTimeInHours = expireTimeInHours;
	}

	
}
	