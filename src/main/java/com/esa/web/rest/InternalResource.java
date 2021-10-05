package com.esa.web.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
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

/**
 * Controller for view and managing Log Level at runtime.
 */
@RestController
@RequestMapping("/api/reaction-service/internal")
public class InternalResource {

	RedisServiceService redisService;
	
	UserAuthorizationService userAuthorizationService;
	
	ApplicationProperties applicationProperties;
	
	public InternalResource(RedisServiceService redisService,UserAuthorizationService userAuthorizationService, ApplicationProperties applicationProperties) {
		super();
		this.redisService = redisService;
		this.userAuthorizationService = userAuthorizationService;
		this.applicationProperties = applicationProperties;
	}

	@PutMapping("/initialise-reactions")
    @Timed
    public String createReactionKeys(@Valid @RequestBody CreateReactionKeysRequest createReactionKeysRequest) {
		
		Date date = Util.calculateExpireDate(createReactionKeysRequest.getPublishAtInMs(), applicationProperties.getExpireTimeInHours());
		
		redisService.remove(Util.getContentViewKey(createReactionKeysRequest.getContentId()));
		redisService.addToSet(Util.getContentViewKey(createReactionKeysRequest.getContentId()), Util.getDummyKey());
		redisService.setExpireDate(Util.getContentViewKey(createReactionKeysRequest.getContentId()), date);
		
		redisService.remove(Util.getContentLikeKey(createReactionKeysRequest.getContentId()));
		redisService.addToSet(Util.getContentLikeKey(createReactionKeysRequest.getContentId()), Util.getDummyKey());
		redisService.setExpireDate(Util.getContentLikeKey(createReactionKeysRequest.getContentId()), date);
		
		return "success";
	}
	
	@GetMapping("/get-small-reactions")
    @Timed
    public GetSmallReactionItem getSmallReactions(@RequestParam String studentId,@RequestParam String contentId,SmallReactionContentType contentType,HttpServletRequest request) {
		GetSmallReactionItem result = new GetSmallReactionItem();
		result.setContentType(contentType);
		result.setViewed(redisService.existInSet(Util.getContentViewKey(contentId),studentId));
		result.setLiked(redisService.existInSet(Util.getContentLikeKey(contentId),studentId));
		if(contentType==SmallReactionContentType.QUESTION)
			result.setAnswered(redisService.existInSet(Util.getAnsweredQuestionKey(contentId),studentId));
		return result;
	}
	
	@PutMapping("/get-reactions")
    @Timed
    public List<GetReactionsResponse> getReactions(@Valid @RequestBody List<GetReactionsRequest> getReactionsRequests) {
		List<GetReactionsResponse> result = new ArrayList<GetReactionsResponse>();
		for (Iterator iterator = getReactionsRequests.iterator(); iterator.hasNext();) {
			GetReactionsRequest item = (GetReactionsRequest) iterator.next();
			
			GetReactionsResponse response = new GetReactionsResponse();
			response.setContentId(item.getContentId());
			response.setContentType(item.getContentType());
			response.setViewed(redisService.existInSet(Util.getContentViewKey(item.getContentId()),item.getStudentId()));
			response.setLiked(redisService.existInSet(Util.getContentLikeKey(item.getContentId()),item.getStudentId()));
			
			if(item.getContentType()==SmallReactionContentType.QUESTION) {
				response.setAnswered(redisService.existInSet(Util.getAnsweredQuestionKey(item.getContentId()),item.getStudentId()));
				response.setNumberOfLikes(redisService.sizeOfSet(Util.getContentViewKey(item.getContentId())));
				response.setNumberOfViews(redisService.sizeOfSet(Util.getContentLikeKey(item.getContentId())));
				response.setTotalNumberOfSelections(redisService.sizeOfSet(Util.getAnsweredQuestionKey(item.getContentId())));
				response.setQuestionOptionStatistics(getOptionStatisticsList(item.getContentId()));
		        
			}
			
			result.add(response);
		}
		return result;
	}
	
	@GetMapping("/get-recent-reactions")
    @Timed
    public List<GetRecentReactionResponseItem> getRecentReactions() {
		
		List<GetRecentReactionResponseItem> result = new ArrayList<GetRecentReactionResponseItem>();
		
		Set<String> likeList =redisService.retrieveSet(Util.getRecentLikeKey());
		for (Iterator iterator = likeList.iterator(); iterator.hasNext();) {
			String contentId = (String) iterator.next();
			Long likeCount = redisService.sizeOfSet(Util.getContentLikeKey(contentId));
			Long viewCount = redisService.sizeOfSet(Util.getContentViewKey(contentId));
			
			GetRecentReactionResponseItem item = new GetRecentReactionResponseItem(contentId, viewCount, likeCount);
			result.add(item);
		}
		
		
		return result;
	}
	
	@GetMapping("/get-recent-question-statistics")
    @Timed
    public List<GetRecentQuestionStatisticsResponseItem> getRecentQuestionStatistics() {
		
		List<GetRecentQuestionStatisticsResponseItem> result = new ArrayList<GetRecentQuestionStatisticsResponseItem>();
		
		Set<String> contents =redisService.retrieveSet(Util.getRecentQuestionStatisticKey());
		for (Iterator iterator = contents.iterator(); iterator.hasNext();) {
			String contentId = (String) iterator.next();
			
			GetRecentQuestionStatisticsResponseItem item = new GetRecentQuestionStatisticsResponseItem();
			Long totalNumberOfSelections = redisService.sizeOfSet(Util.getAnsweredQuestionKey(contentId));
			item.setTotalNumberOfSelections(totalNumberOfSelections);
			item.setContentId(contentId);
			
			item.setQuestionOptionStatistics(getOptionStatisticsList(contentId));

			result.add(item);
		}
		
		
		return result;
	}
	
	private List<QuestionOptionStatistics> getOptionStatisticsList(String contentId){
		
		List<QuestionOptionStatistics> result = new ArrayList<QuestionOptionStatistics>();
		
		Set<String> questionOptionKeys = redisService.getKeyList(Util.getAnsweredOptionKey(contentId, "*"));
		for (Iterator iterator2 = questionOptionKeys.iterator(); iterator2.hasNext();) {
			String questionOptionKey = (String) iterator2.next();
			String optionId = Util.getOptionIdFromAnsweredOptionKey(questionOptionKey, contentId);
			if(optionId.equals("answer"))
				continue;
			
			QuestionOptionStatistics questionOptionStatistics = new QuestionOptionStatistics();;
			questionOptionStatistics.setQuestionOptionId(optionId);
			questionOptionStatistics.setNumberOfSelection(redisService.get(Util.getAnsweredOptionKey(contentId, optionId)));
			
			result.add(questionOptionStatistics);
		}
		
		return result;
	}
	
	
	
	public RedisServiceService getRedisService() {
		return redisService;
	}

	public void setRedisService(RedisServiceService redisService) {
		this.redisService = redisService;
	}
}
