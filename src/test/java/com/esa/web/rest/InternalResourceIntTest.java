package com.esa.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.esa.ReactionserviceApp;
import com.esa.config.ApplicationProperties;
import com.esa.service.RedisServiceService;
import com.esa.service.UserAuthorizationService;
import com.esa.web.rest.util.Util;
import com.esa.web.rest.vm.CreateReactionKeysRequest;
import com.esa.web.rest.vm.GetReactionsRequest;
import com.esa.web.rest.vm.GetReactionsResponse;
import com.esa.web.rest.vm.GetRecentQuestionStatisticsResponseItem;
import com.esa.web.rest.vm.GetRecentReactionResponseItem;
import com.esa.web.rest.vm.GetSmallReactionItem;
import com.esa.web.rest.vm.QuestionOptionStatistics;
import com.esa.web.rest.vm.SmallReactionContentType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test class for the LogsResource REST controller.
 *
 * @see LogsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReactionserviceApp.class)
public class InternalResourceIntTest {

	private final String CONTENT_ID = "contentId";
	
	private final String CONTENT_ID_2 = "contentId_2";
	
	private final String OPTION_ID = "optionId";
	
	private final String OPTION_ID_2 = "optionId_2";
	
	private final String USER_ID = "userId";
	
    private MockMvc restLogsMockMvc;
    
    private MockMvc restInternalMockMvc;

    private redis.embedded.RedisServer redisServer;
    
    @Autowired
    private ApplicationProperties applicationProperties;
    
    @Autowired
    RedisServiceService redisService;
    
    @Mock
    UserAuthorizationService userAuthorizationService;
    
    @Autowired
    ObjectMapper objectMapper;
    
    @Before
    public void setup() throws IOException {
    	
    	redisService.flushAll();
    	MockitoAnnotations.initMocks(this);
    	Mockito.when(userAuthorizationService.getUserId()).thenReturn(USER_ID);
    	  

        LogsResource logsResource = new LogsResource();
        this.restLogsMockMvc = MockMvcBuilders
            .standaloneSetup(logsResource)
            .build();
        
        InternalResource contentResource = new InternalResource(redisService,userAuthorizationService,applicationProperties);
        this.restInternalMockMvc = MockMvcBuilders
            .standaloneSetup(contentResource)
            .build();
    }
    

    @Test
    public void initialiseReactions() throws Exception {
    	
    	CreateReactionKeysRequest createReactionKeysRequest = new CreateReactionKeysRequest();
    	createReactionKeysRequest.setContentId(CONTENT_ID);
    	createReactionKeysRequest.setPublishAtInMs((new Date().getTime()));
    	
    	restInternalMockMvc.perform(put("/api/reaction-service/internal/initialise-reactions")
	      .contentType(TestUtil.APPLICATION_JSON_UTF8)
	      .content(TestUtil.convertObjectToJsonBytes(createReactionKeysRequest)))
	      .andExpect(status().isOk());
    	

    	
    	Set<String> result = redisService.retrieveSet(Util.getContentViewKey(CONTENT_ID));
    	assertThat(result.size()).isEqualTo(1);
    	assertThat(result.toArray()[0]).isEqualTo(Util.getDummyKey());
    	
    	Set<String> resultLike = redisService.retrieveSet(Util.getContentLikeKey(CONTENT_ID));
    	assertThat(resultLike.size()).isEqualTo(1);
    	assertThat(resultLike.toArray()[0]).isEqualTo(Util.getDummyKey());
    	
    	Thread.sleep(1000*60);
    
    	result = redisService.retrieveSet(Util.getContentViewKey(CONTENT_ID));
    	assertThat(result.size()).isEqualTo(0);
    	
    	resultLike = redisService.retrieveSet(Util.getContentLikeKey(CONTENT_ID));
    	assertThat(resultLike.size()).isEqualTo(0);
    	
    }
    
    @Test
    public void getSmallReactions() throws Exception {
    	
    	MvcResult resultMvc= restInternalMockMvc.perform(get("/api/reaction-service/internal/get-small-reactions?studentId="+USER_ID+"&contentId="+CONTENT_ID+"&contentType="+SmallReactionContentType.QUESTION.toString()))
  		      .andExpect(status().isOk()).andReturn();
	
    	ObjectMapper objectMapper = new ObjectMapper();
    	GetSmallReactionItem getSmallReactionItem = objectMapper.readValue(resultMvc.getResponse().getContentAsString(), GetSmallReactionItem.class);
    	
    	assertThat(getSmallReactionItem.getViewed()).isFalse();
		assertThat(getSmallReactionItem.getLiked()).isFalse();
		assertThat(getSmallReactionItem.getAnswered()).isFalse();
		
    }
    
    @Test
    public void getSmallReactionsInclude() throws Exception {
    	
    	redisService.addToSet(Util.getContentViewKey(CONTENT_ID), USER_ID);
    	redisService.addToSet(Util.getContentLikeKey(CONTENT_ID), USER_ID);
    	redisService.addToSet(Util.getAnsweredQuestionKey(CONTENT_ID), USER_ID);
    	
    	MvcResult resultMvc= restInternalMockMvc.perform(get("/api/reaction-service/internal/get-small-reactions"
    			+ "?studentId="+USER_ID+"&contentId="+CONTENT_ID+"&contentType="+SmallReactionContentType.QUESTION.toString()))
  		      .andExpect(status().isOk()).andReturn();
	
    	ObjectMapper objectMapper = new ObjectMapper();
    	GetSmallReactionItem getSmallReactionItem = objectMapper.readValue(resultMvc.getResponse().getContentAsString(), GetSmallReactionItem.class);
    	
    	assertThat(getSmallReactionItem.getViewed()).isTrue();
		assertThat(getSmallReactionItem.getLiked()).isTrue();
		assertThat(getSmallReactionItem.getAnswered()).isTrue();
		
    }
    
    @Test
    public void getReactions() throws Exception {
    	
    	redisService.addToSet(Util.getContentViewKey(CONTENT_ID), USER_ID);
    	redisService.addToSet(Util.getContentLikeKey(CONTENT_ID), USER_ID);
    	
    	redisService.addToSet(Util.getAnsweredQuestionKey(CONTENT_ID), USER_ID);
    	redisService.addToSet(Util.getAnsweredQuestionKey(CONTENT_ID), userAuthorizationService.getUserId());
    	
    	redisService.increment(Util.getAnsweredOptionKey(CONTENT_ID, OPTION_ID));
		redisService.increment(Util.getAnsweredOptionKey(CONTENT_ID, OPTION_ID_2));
		redisService.increment(Util.getAnsweredOptionKey(CONTENT_ID, OPTION_ID_2));
    	
    	List<GetReactionsRequest> inputList = new ArrayList<GetReactionsRequest>();
    	GetReactionsRequest reactionsRequest = new GetReactionsRequest();
    	reactionsRequest.setContentId(CONTENT_ID);
    	reactionsRequest.setStudentId(USER_ID);
    	reactionsRequest.setContentType(SmallReactionContentType.QUESTION);
    	inputList.add(reactionsRequest);
    	
    	MvcResult resultMvc = restInternalMockMvc.perform(put("/api/reaction-service/internal/get-reactions")
	      .contentType(TestUtil.APPLICATION_JSON_UTF8)
	      .content(TestUtil.convertObjectToJsonBytes(inputList)))
	      .andExpect(status().isOk()).andReturn();
	
    	ObjectMapper objectMapper = new ObjectMapper();
    	List<GetReactionsResponse> result = objectMapper.readValue(resultMvc.getResponse().getContentAsString(), new TypeReference<List<GetReactionsResponse>>() { });
    	
    	
    	assertThat(result.size()).isEqualTo(1);
    	assertThat(result.get(0).getAnswered()).isTrue();
    	assertThat(result.get(0).getContentId()).isEqualTo(CONTENT_ID);
    	assertThat(result.get(0).getContentType()).isEqualTo(SmallReactionContentType.QUESTION);
    	assertThat(result.get(0).getLiked()).isTrue();
    	assertThat(result.get(0).getNumberOfLikes()).isEqualTo(1);
    	assertThat(result.get(0).getNumberOfViews()).isEqualTo(1);
    	assertThat(result.get(0).getTotalNumberOfSelections()).isEqualTo(1);
    	assertThat(result.get(0).getViewed()).isTrue();
    
    	List<QuestionOptionStatistics> optionList = result.get(0).getQuestionOptionStatistics();
    	assertThat(optionList.size()).isEqualTo(2);
    	
    	assertThat(optionList.get(0).getNumberOfSelection()).isEqualTo("2");
    	assertThat(optionList.get(0).getQuestionOptionId()).isEqualTo(OPTION_ID_2);
    	
    	assertThat(optionList.get(1).getNumberOfSelection()).isEqualTo("1");
    	assertThat(optionList.get(1).getQuestionOptionId()).isEqualTo(OPTION_ID);
    }
    
    @Test
    public void getRecentReactions() throws Exception {
    	
    	redisService.addToSet(Util.getContentViewKey(CONTENT_ID), USER_ID);
    	redisService.addToSet(Util.getContentLikeKey(CONTENT_ID), USER_ID);
    	redisService.addToSet(Util.getRecentLikeKey(), CONTENT_ID);
    	
    	MvcResult resultMvc= restInternalMockMvc.perform(get("/api/reaction-service/internal/get-recent-reactions"))
  		      .andExpect(status().isOk()).andReturn();
	
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	List<GetRecentReactionResponseItem> result = objectMapper.readValue(resultMvc.getResponse().getContentAsString(), new TypeReference<List<GetRecentReactionResponseItem>>() { });
    	
    	assertThat(result.size()).isEqualTo(1);
    	assertThat(result.get(0).getContentId()).isEqualTo(CONTENT_ID);
    	assertThat(result.get(0).getViewCount()).isEqualTo(1);
    	assertThat(result.get(0).getLikeCount()).isEqualTo(1);
		
    }
    
    @Test
    public void getRecentQuestionStatistics() throws Exception {
    	
    	redisService.addToSet(Util.getAnsweredQuestionKey(CONTENT_ID), userAuthorizationService.getUserId());
		redisService.increment(Util.getAnsweredOptionKey(CONTENT_ID, OPTION_ID));
		redisService.increment(Util.getAnsweredOptionKey(CONTENT_ID, OPTION_ID_2));
		redisService.addToSet(Util.getRecentQuestionStatisticKey(),CONTENT_ID);
    	
    	MvcResult resultMvc= restInternalMockMvc.perform(get("/api/reaction-service/internal/get-recent-question-statistics"))
  		      .andExpect(status().isOk()).andReturn();
	
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	List<GetRecentQuestionStatisticsResponseItem> result = objectMapper.readValue(resultMvc.getResponse().getContentAsString(), new TypeReference<List<GetRecentQuestionStatisticsResponseItem>>() { });
    	
    	assertThat(result.size()).isEqualTo(1);
    	assertThat(result.get(0).getContentId()).isEqualTo(CONTENT_ID);
    	assertThat(result.get(0).getTotalNumberOfSelections()).isEqualTo(1);
    	
    	List<QuestionOptionStatistics> optionList = result.get(0).getQuestionOptionStatistics();
    	assertThat(optionList.size()).isEqualTo(2);
    	assertThat(optionList.get(0).getQuestionOptionId()).isEqualTo(OPTION_ID_2);
    	assertThat(optionList.get(0).getNumberOfSelection()).isEqualTo("1");
		
    	assertThat(optionList.get(1).getQuestionOptionId()).isEqualTo(OPTION_ID);
    	assertThat(optionList.get(1).getNumberOfSelection()).isEqualTo("1");
		
    }
//
//    @Test
//    public void removelikeContent() throws Exception {
//    	
//    	redisService.addToSet(Util.getContentLikeKey(CONTENT_ID), userAuthorizationService.getUserId());
//    	redisService.addToSet(Util.getRecentLikeKey(), CONTENT_ID);
//    	
//    	Set<String> result = redisService.retrieveSet(Util.getContentLikeKey(CONTENT_ID));
//		assertThat(result.toArray()[0]).isEqualTo(USER_ID);
//    	
//    	restContentMockMvc.perform(delete("/api/reaction-service/contents/{contentId}/like", CONTENT_ID))
//    		.andExpect(status().isOk());
//	
//		result = redisService.retrieveSet(Util.getContentLikeKey(CONTENT_ID));
//		assertThat(result.size()).isEqualTo(0);
//		
//		result = redisService.retrieveSet(Util.getRecentLikeKey());
//		assertThat(result.size()).isEqualTo(0);
//    }
//    
//    @Test
//    public void answerQuestion_NotExist() throws Exception {
//    	restContentMockMvc.perform(put("/api/reaction-service/contents/{contentId}/options/{selectedQuestionOptionId}/answer-question", CONTENT_ID,OPTION_ID))
//    		.andExpect(status().isOk());
//	
//		Set<String> result = redisService.retrieveSet(Util.getAnsweredQuestionKey(CONTENT_ID));
//		assertThat(result.size()).isEqualTo(1);
//		assertThat(result.toArray()[0]).isEqualTo(USER_ID);
//		
//		String incrementResult = redisService.get(Util.getAnsweredOptionKey(CONTENT_ID,OPTION_ID));
//		assertThat(incrementResult).isEqualTo("1");
//		
//		Set<String> recentStatistic = redisService.retrieveSet(Util.getQuestionRecentStatisticKey());
//		assertThat(recentStatistic.size()).isEqualTo(1);
//		assertThat(recentStatistic.toArray()[0]).isEqualTo(CONTENT_ID);
//    }
//    
//    @Test
//    public void answerQuestion_Exist() throws Exception {
//    	restContentMockMvc.perform(put("/api/reaction-service/contents/{contentId}/options/{selectedQuestionOptionId}/answer-question", CONTENT_ID,OPTION_ID))
//    		.andExpect(status().isOk());
//	
//    	restContentMockMvc.perform(put("/api/reaction-service/contents/{contentId}/options/{selectedQuestionOptionId}/answer-question", CONTENT_ID,OPTION_ID))
//		.andExpect(status().isOk());
//
//    	
//		Set<String> result = redisService.retrieveSet(Util.getAnsweredQuestionKey(CONTENT_ID));
//		assertThat(result.size()).isEqualTo(1);
//		assertThat(result.toArray()[0]).isEqualTo(USER_ID);
//		
//		String incrementResult = redisService.get(Util.getAnsweredOptionKey(CONTENT_ID,OPTION_ID));
//		assertThat(incrementResult).isEqualTo("1");
//		
//		Set<String> recentStatistic = redisService.retrieveSet(Util.getQuestionRecentStatisticKey());
//		assertThat(recentStatistic.size()).isEqualTo(1);
//		assertThat(recentStatistic.toArray()[0]).isEqualTo(CONTENT_ID);
//    }
//    
//    @Test
//    public void viewContentBatch() throws Exception {
//    	
//    	List<String> list = new ArrayList<String>();
//    	list.add(CONTENT_ID);
//    	list.add(CONTENT_ID_2);
//    	
//        restContentMockMvc.perform(put("/api/reaction-service/contents/view/batch")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(list)))
//            .andExpect(status().isOk());
//    	
//    	Set<String> result = redisService.retrieveSet(Util.getContentViewKey(CONTENT_ID));
//    	assertThat(result.size()).isEqualTo(1);
//    	assertThat(result.toArray()[0]).isEqualTo(USER_ID);
//    	
//    	result = redisService.retrieveSet(Util.getContentViewKey(CONTENT_ID_2));
//    	assertThat(result.toArray()[0]).isEqualTo(USER_ID);
//    }
//    
//    @Test
//    public void likeContentBatch() throws Exception {
//    	
//    	List<String> list = new ArrayList<String>();
//    	list.add(CONTENT_ID);
//    	list.add(CONTENT_ID_2);
//    	
//        restContentMockMvc.perform(put("/api/reaction-service/contents/like/batch")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(list)))
//            .andExpect(status().isOk());
//    	
//    	Set<String> result = redisService.retrieveSet(Util.getContentLikeKey(CONTENT_ID));
//    	assertThat(result.size()).isEqualTo(1);
//    	assertThat(result.toArray()[0]).isEqualTo(USER_ID);
//    	
//    	result = redisService.retrieveSet(Util.getContentLikeKey(CONTENT_ID_2));
//    	assertThat(result.toArray()[0]).isEqualTo(USER_ID);
//    }
//    
//    @Test
//    public void removeLikeBatch() throws Exception {
//    	
//    	redisService.addToSet(Util.getContentLikeKey(CONTENT_ID), userAuthorizationService.getUserId());
//    	redisService.addToSet(Util.getContentLikeKey(CONTENT_ID_2), userAuthorizationService.getUserId());
//    	
//    	List<String> list = new ArrayList<String>();
//    	list.add(CONTENT_ID);
//    	list.add(CONTENT_ID_2);
//    	
//        restContentMockMvc.perform(delete("/api/reaction-service/contents/like/batch")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(list)))
//            .andExpect(status().isOk());
//    	
//    	Set<String> result = redisService.retrieveSet(Util.getContentLikeKey(CONTENT_ID));
//    	assertThat(result.size()).isEqualTo(0);
//    	
//    	result = redisService.retrieveSet(Util.getContentLikeKey(CONTENT_ID_2));
//    	assertThat(result.size()).isEqualTo(0);
//    }
//    
//    @Test
//    public void answerQuestionBatch() throws Exception {
//    	
//    	List<QuestionSolveRequest> list = new ArrayList<QuestionSolveRequest>();
//    	list.add(new QuestionSolveRequest(CONTENT_ID, OPTION_ID));
//    	list.add(new QuestionSolveRequest(CONTENT_ID_2, OPTION_ID));
//	
//    	restContentMockMvc.perform(put("/api/reaction-service/contents/answer-questions")
//                .contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(list)))
//                .andExpect(status().isOk());
//        	
//    	
//    	
//		Set<String> result = redisService.retrieveSet(Util.getAnsweredQuestionKey(CONTENT_ID));
//		assertThat(result.size()).isEqualTo(1);
//		assertThat(result.toArray()[0]).isEqualTo(USER_ID);
//		
//		result = redisService.retrieveSet(Util.getAnsweredQuestionKey(CONTENT_ID_2));
//		assertThat(result.size()).isEqualTo(1);
//		assertThat(result.toArray()[0]).isEqualTo(USER_ID);
//		
//		String incrementResult = redisService.get(Util.getAnsweredOptionKey(CONTENT_ID,OPTION_ID));
//		assertThat(incrementResult).isEqualTo("1");
//		
//		incrementResult = redisService.get(Util.getAnsweredOptionKey(CONTENT_ID_2,OPTION_ID));
//		assertThat(incrementResult).isEqualTo("1");
//		
//		Set<String> recentStatistic = redisService.retrieveSet(Util.getQuestionRecentStatisticKey());
//		assertThat(recentStatistic.size()).isEqualTo(2);
//		assertThat(recentStatistic.toArray()[0]).isEqualTo(CONTENT_ID_2);
//		assertThat(recentStatistic.toArray()[1]).isEqualTo(CONTENT_ID);
//    }
}
