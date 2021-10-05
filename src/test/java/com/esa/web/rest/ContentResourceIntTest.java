package com.esa.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.esa.ReactionserviceApp;
import com.esa.config.ApplicationProperties;
import com.esa.service.RedisServiceService;
import com.esa.service.UserAuthorizationService;
import com.esa.web.rest.util.Util;
import com.esa.web.rest.vm.QuestionSolveRequest;

/**
 * Test class for the LogsResource REST controller.
 *
 * @see LogsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReactionserviceApp.class)
public class ContentResourceIntTest {

	private final String CONTENT_ID = "contentId";
	
	private final String CONTENT_ID_2 = "contentId_2";
	
	private final String OPTION_ID = "optionId";
	
	private final String USER_ID = "userId";
	
    private MockMvc restLogsMockMvc;
    
    private MockMvc restContentMockMvc;

    private redis.embedded.RedisServer redisServer;
    
    @Autowired
    private ApplicationProperties applicationProperties;
    
    @Autowired
    RedisServiceService redisService;
    
    @Mock
    UserAuthorizationService userAuthorizationService;
    
    @Before
    public void setup() throws IOException {
    	
    	redisService.flushAll();
    	MockitoAnnotations.initMocks(this);
    	Mockito.when(userAuthorizationService.getUserId()).thenReturn(USER_ID);
    	  

        LogsResource logsResource = new LogsResource();
        this.restLogsMockMvc = MockMvcBuilders
            .standaloneSetup(logsResource)
            .build();
        
        ContentResource contentResource = new ContentResource(redisService,userAuthorizationService);
        this.restContentMockMvc = MockMvcBuilders
            .standaloneSetup(contentResource)
            .build();
    }
    

    @Test
    public void viewContent() throws Exception {
    	
    	restContentMockMvc.perform(put("/api/reaction-service/contents/{contentId}/view", CONTENT_ID))
        	.andExpect(status().isOk());
    	
    	Set<String> result = redisService.retrieveSet(Util.getContentViewKey(CONTENT_ID));
    	assertThat(result.size()).isEqualTo(1);
    	assertThat(result.toArray()[0]).isEqualTo(USER_ID);
    }
    
    @Test
    public void likeContent() throws Exception {
    	restContentMockMvc.perform(put("/api/reaction-service/contents/{contentId}/like", CONTENT_ID))
    		.andExpect(status().isOk());
	
		Set<String> result = redisService.retrieveSet(Util.getContentLikeKey(CONTENT_ID));
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.toArray()[0]).isEqualTo(USER_ID);
		
		Set<String> recentLiked = redisService.retrieveSet(Util.getRecentLikeKey());
		assertThat(recentLiked.size()).isEqualTo(1);
		assertThat(recentLiked.toArray()[0]).isEqualTo(CONTENT_ID);
    }

    @Test
    public void removelikeContent() throws Exception {
    	
    	redisService.addToSet(Util.getContentLikeKey(CONTENT_ID), userAuthorizationService.getUserId());
    	redisService.addToSet(Util.getRecentLikeKey(), CONTENT_ID);
    	
    	Set<String> result = redisService.retrieveSet(Util.getContentLikeKey(CONTENT_ID));
		assertThat(result.toArray()[0]).isEqualTo(USER_ID);
    	
    	restContentMockMvc.perform(delete("/api/reaction-service/contents/{contentId}/like", CONTENT_ID))
    		.andExpect(status().isOk());
	
		result = redisService.retrieveSet(Util.getContentLikeKey(CONTENT_ID));
		assertThat(result.size()).isEqualTo(0);
		
		result = redisService.retrieveSet(Util.getRecentLikeKey());
		assertThat(result.size()).isEqualTo(0);
    }
    
    @Test
    public void answerQuestion_NotExist() throws Exception {
    	restContentMockMvc.perform(put("/api/reaction-service/contents/{contentId}/options/{selectedQuestionOptionId}/answer-question", CONTENT_ID,OPTION_ID))
    		.andExpect(status().isOk());
	
		Set<String> result = redisService.retrieveSet(Util.getAnsweredQuestionKey(CONTENT_ID));
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.toArray()[0]).isEqualTo(USER_ID);
		
		String incrementResult = redisService.get(Util.getAnsweredOptionKey(CONTENT_ID,OPTION_ID));
		assertThat(incrementResult).isEqualTo("1");
		
		Set<String> recentStatistic = redisService.retrieveSet(Util.getRecentQuestionStatisticKey());
		assertThat(recentStatistic.size()).isEqualTo(1);
		assertThat(recentStatistic.toArray()[0]).isEqualTo(CONTENT_ID);
    }
    
    @Test
    public void answerQuestion_Exist() throws Exception {
    	restContentMockMvc.perform(put("/api/reaction-service/contents/{contentId}/options/{selectedQuestionOptionId}/answer-question", CONTENT_ID,OPTION_ID))
    		.andExpect(status().isOk());
	
    	restContentMockMvc.perform(put("/api/reaction-service/contents/{contentId}/options/{selectedQuestionOptionId}/answer-question", CONTENT_ID,OPTION_ID))
		.andExpect(status().isOk());

    	
		Set<String> result = redisService.retrieveSet(Util.getAnsweredQuestionKey(CONTENT_ID));
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.toArray()[0]).isEqualTo(USER_ID);
		
		String incrementResult = redisService.get(Util.getAnsweredOptionKey(CONTENT_ID,OPTION_ID));
		assertThat(incrementResult).isEqualTo("1");
		
		Set<String> recentStatistic = redisService.retrieveSet(Util.getRecentQuestionStatisticKey());
		assertThat(recentStatistic.size()).isEqualTo(1);
		assertThat(recentStatistic.toArray()[0]).isEqualTo(CONTENT_ID);
    }
    
    @Test
    public void viewContentBatch() throws Exception {
    	
    	List<String> list = new ArrayList<String>();
    	list.add(CONTENT_ID);
    	list.add(CONTENT_ID_2);
    	
        restContentMockMvc.perform(put("/api/reaction-service/contents/view/batch")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(list)))
            .andExpect(status().isOk());
    	
    	Set<String> result = redisService.retrieveSet(Util.getContentViewKey(CONTENT_ID));
    	assertThat(result.size()).isEqualTo(1);
    	assertThat(result.toArray()[0]).isEqualTo(USER_ID);
    	
    	result = redisService.retrieveSet(Util.getContentViewKey(CONTENT_ID_2));
    	assertThat(result.toArray()[0]).isEqualTo(USER_ID);
    }
    
    @Test
    public void likeContentBatch() throws Exception {
    	
    	List<String> list = new ArrayList<String>();
    	list.add(CONTENT_ID);
    	list.add(CONTENT_ID_2);
    	
        restContentMockMvc.perform(put("/api/reaction-service/contents/like/batch")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(list)))
            .andExpect(status().isOk());
    	
    	Set<String> result = redisService.retrieveSet(Util.getContentLikeKey(CONTENT_ID));
    	assertThat(result.size()).isEqualTo(1);
    	assertThat(result.toArray()[0]).isEqualTo(USER_ID);
    	
    	result = redisService.retrieveSet(Util.getContentLikeKey(CONTENT_ID_2));
    	assertThat(result.toArray()[0]).isEqualTo(USER_ID);
    }
    
    @Test
    public void removeLikeBatch() throws Exception {
    	
    	redisService.addToSet(Util.getContentLikeKey(CONTENT_ID), userAuthorizationService.getUserId());
    	redisService.addToSet(Util.getContentLikeKey(CONTENT_ID_2), userAuthorizationService.getUserId());
    	
    	List<String> list = new ArrayList<String>();
    	list.add(CONTENT_ID);
    	list.add(CONTENT_ID_2);
    	
        restContentMockMvc.perform(delete("/api/reaction-service/contents/like/batch")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(list)))
            .andExpect(status().isOk());
    	
    	Set<String> result = redisService.retrieveSet(Util.getContentLikeKey(CONTENT_ID));
    	assertThat(result.size()).isEqualTo(0);
    	
    	result = redisService.retrieveSet(Util.getContentLikeKey(CONTENT_ID_2));
    	assertThat(result.size()).isEqualTo(0);
    }
    
    @Test
    public void answerQuestionBatch() throws Exception {
    	
    	List<QuestionSolveRequest> list = new ArrayList<QuestionSolveRequest>();
    	list.add(new QuestionSolveRequest(CONTENT_ID, OPTION_ID));
    	list.add(new QuestionSolveRequest(CONTENT_ID_2, OPTION_ID));
	
    	restContentMockMvc.perform(put("/api/reaction-service/contents/answer-questions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(list)))
                .andExpect(status().isOk());
        	
    	
    	
		Set<String> result = redisService.retrieveSet(Util.getAnsweredQuestionKey(CONTENT_ID));
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.toArray()[0]).isEqualTo(USER_ID);
		
		result = redisService.retrieveSet(Util.getAnsweredQuestionKey(CONTENT_ID_2));
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.toArray()[0]).isEqualTo(USER_ID);
		
		String incrementResult = redisService.get(Util.getAnsweredOptionKey(CONTENT_ID,OPTION_ID));
		assertThat(incrementResult).isEqualTo("1");
		
		incrementResult = redisService.get(Util.getAnsweredOptionKey(CONTENT_ID_2,OPTION_ID));
		assertThat(incrementResult).isEqualTo("1");
		
		Set<String> recentStatistic = redisService.retrieveSet(Util.getRecentQuestionStatisticKey());
		assertThat(recentStatistic.size()).isEqualTo(2);
		assertThat(recentStatistic.toArray()[0]).isEqualTo(CONTENT_ID_2);
		assertThat(recentStatistic.toArray()[1]).isEqualTo(CONTENT_ID);
    }
}
