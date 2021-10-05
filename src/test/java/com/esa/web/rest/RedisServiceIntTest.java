package com.esa.web.rest;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.esa.ReactionserviceApp;
import com.esa.config.ApplicationProperties;
import com.esa.service.RedisServiceService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReactionserviceApp.class)
public class RedisServiceIntTest {

	private  redis.embedded.RedisServer redisServer;
	
	private final String KEY = "NAME";
    
	private final String SET_KEY = "SET_NAME";
	
	private final String INCREMENT_KEY = "INCREMENT_NAME";
	
    @Autowired
    private ApplicationProperties applicationProperties;
    
    @Autowired
    RedisServiceService redisService;
	
    @Before
    public void flushRedis() {
    	redisService.flushAll();
    }
    
    
//    @PostConstruct
//    public void postConstruct() throws IOException {
//    	redisServer = new redis.embedded.RedisServer(applicationProperties.getRedisPort().intValue());
//        redisServer.start();
//    }
//
//    @PreDestroy
//    public void stopRedis() {
//      System.out.println("stop redis");
//      redisServer.stop();
//      System.out.println("stop redis btti");
//    }
    
    @Test
    public void add() {
    	redisService.add(KEY, "ramazan");
    	String name = redisService.get(KEY);
    	assertThat(name).isEqualTo("ramazan");
    }
    
    @Test
    public void delete() {
    	redisService.add(KEY, "ramazan");
    	redisService.remove(KEY);
    	String value = redisService.get(KEY);
    	assertThat(value).isNull();
    }
    
    @Test
    public void deleteNotExist() {
    	redisService.add(KEY, "ramazan");
    	redisService.remove("sdsdsdsd");
    	String name = redisService.get(KEY);
    	assertThat(name).isEqualTo("ramazan");
    }
    
    @Test
    public void addToSet() {
    	redisService.addToSet(SET_KEY, "ramazan");
    	redisService.addToSet(SET_KEY, "firin");
    	Set<String> result = redisService.retrieveSet(SET_KEY);
    	assertThat(result.size()).isEqualTo(2);
    	assertThat(result.toArray()[0]).isEqualTo("firin");
    	assertThat(result.toArray()[1]).isEqualTo("ramazan");
    }
    
    @Test
    public void addToSet_dublicate() {
    	redisService.addToSet(SET_KEY, "ramazan");
    	redisService.addToSet(SET_KEY, "ramazan");
    	Set<String> result = redisService.retrieveSet(SET_KEY);
    	assertThat(result.size()).isEqualTo(1);
    	assertThat(result.toArray()[0]).isEqualTo("ramazan");
    }
    
    @Test
    public void removeFromSet() {
    	redisService.addToSet(SET_KEY, "ramazan");
    	redisService.addToSet(SET_KEY, "firin");
    	Set<String> result = redisService.retrieveSet(SET_KEY);
    	assertThat(result.size()).isEqualTo(2);
    	
    	redisService.removeFromSet(SET_KEY, "firin");
    	result = redisService.retrieveSet(SET_KEY);
    	
    	assertThat(result.size()).isEqualTo(1);
    	assertThat(result.toArray()[0]).isEqualTo("ramazan");
    }
    
    @Test
    public void removeFromSetWithoutExist() {
    	redisService.addToSet(SET_KEY, "ramazan");
    	redisService.addToSet(SET_KEY, "firin");
    	Set<String> result = redisService.retrieveSet(SET_KEY);
    	assertThat(result.size()).isEqualTo(2);
    	
    	redisService.removeFromSet(SET_KEY, "aaaaaaa");
    	result = redisService.retrieveSet(SET_KEY);
    	
    	assertThat(result.size()).isEqualTo(2);
    	assertThat(result.toArray()[0]).isEqualTo("firin");
    	assertThat(result.toArray()[1]).isEqualTo("ramazan");
    }
    
    @Test
    public void existInSet() {
    	redisService.addToSet(SET_KEY, "ramazan");
    	Boolean isExist = redisService.existInSet(SET_KEY, "ramazan");
    	assertThat(isExist).isTrue();
    	
    	isExist = redisService.existInSet(SET_KEY, "firin");
    	assertThat(isExist).isFalse();
    }
    
    @Test
    public void sizeOfSet() {
    	Long size = redisService.sizeOfSet(SET_KEY);
    	assertThat(size).isEqualTo(0);
    	
    	redisService.addToSet(SET_KEY, "ramazan");
    	size = redisService.sizeOfSet(SET_KEY);
    	assertThat(size).isEqualTo(1);

    }
    
    @Test
    public void getKeys() {
    	redisService.addToSet(SET_KEY, "ramazan");
    	redisService.addToSet(SET_KEY+1, "ramazan123");
    	Set<String> result = redisService.getKeyList(SET_KEY);
    	assertThat(result.size()).isEqualTo(2);
    	assertThat(result.contains(SET_KEY)).isTrue();
    	assertThat(result.contains(SET_KEY+1)).isTrue();
    }
    
    @Test
    public void increment() {
    	String value = redisService.get(INCREMENT_KEY);
     	assertThat(value).isNull();;
        
    	redisService.increment(INCREMENT_KEY);
    	
    	value = redisService.get(INCREMENT_KEY);
    	assertThat(value).isEqualTo("1");
    	
    	redisService.increment(INCREMENT_KEY);
    	
    	value = redisService.get(INCREMENT_KEY);
    	assertThat(value).isEqualTo("2");
    }
}
