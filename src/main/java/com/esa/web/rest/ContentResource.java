package com.esa.web.rest;

import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.esa.service.RedisServiceService;
import com.esa.service.UserAuthorizationService;
import com.esa.web.rest.util.Util;
import com.esa.web.rest.vm.QuestionSolveRequest;

/**
 * Controller for view and managing Log Level at runtime.
 */
@RestController
@RequestMapping("/api/reaction-service/contents")
public class ContentResource {

	RedisServiceService redisService;
	
	UserAuthorizationService userAuthorizationService;
	
	public ContentResource(RedisServiceService redisService, UserAuthorizationService userAuthorizationService) {
		super();
		this.redisService = redisService;
		this.userAuthorizationService = userAuthorizationService;
	}
	
	@PutMapping("/{contentId}/view")
    @Timed
    public String viewContent(@PathVariable String contentId) {
		redisService.addToSet(Util.getContentViewKey(contentId), userAuthorizationService.getUserId());
		return "success";
	}
	
	@PutMapping("/view/batch")
    @Timed
    public String viewContentBatch(@Valid @RequestBody List<String> contentIds) {
		for (Iterator iterator = contentIds.iterator(); iterator.hasNext();) {
			String contentId = (String) iterator.next();
			redisService.addToSet(Util.getContentViewKey(contentId), userAuthorizationService.getUserId());
			
		}
		return "success";
	}

	
	
	@PutMapping("/{contentId}/like")
    @Timed
    public String likeContent(@PathVariable String contentId) {
		redisService.addToSet(Util.getContentLikeKey(contentId), userAuthorizationService.getUserId());
		redisService.addToSet(Util.getRecentLikeKey(), contentId);
		return "success";
	}
	
	@PutMapping("/like/batch")
    @Timed
    public String likeContentBatch(@Valid @RequestBody List<String> contentIds) {
		for (Iterator iterator = contentIds.iterator(); iterator.hasNext();) {
			String contentId = (String) iterator.next();
			redisService.addToSet(Util.getContentLikeKey(contentId), userAuthorizationService.getUserId());
			redisService.addToSet(Util.getRecentLikeKey(), contentId);
		}
		return "success";
	}

	
	
	@DeleteMapping("/{contentId}/like")
    @Timed
    public String removeLike(@PathVariable String contentId) {
		redisService.removeFromSet(Util.getContentLikeKey(contentId), userAuthorizationService.getUserId());
		redisService.removeFromSet(Util.getRecentLikeKey(), contentId);
		return "success";
	}
	
	@DeleteMapping("/like/batch")
    @Timed
    public String removeLikeBatch(@Valid @RequestBody List<String> contentIds) {
		for (Iterator iterator = contentIds.iterator(); iterator.hasNext();) {
			String contentId = (String) iterator.next();
			redisService.removeFromSet(Util.getContentLikeKey(contentId), userAuthorizationService.getUserId());
			redisService.removeFromSet(Util.getRecentLikeKey(), contentId);
		}
		return "success";
	}
	
	@PutMapping("/{contentId}/options/{selectedQuestionOptionId}/answer-question")
    @Timed
    public String answerQuestion(@PathVariable String contentId,@PathVariable String selectedQuestionOptionId) {
		Boolean isExit = redisService.existInSet(Util.getAnsweredQuestionKey(contentId), userAuthorizationService.getUserId());
		if(!isExit) {
			redisService.addToSet(Util.getAnsweredQuestionKey(contentId), userAuthorizationService.getUserId());
			redisService.increment(Util.getAnsweredOptionKey(contentId, selectedQuestionOptionId));
			redisService.addToSet(Util.getRecentQuestionStatisticKey(),contentId);
		}
		return "success";
	}
	
	@PutMapping("/answer-questions")
    @Timed
    public String answerQuestions(@Valid @RequestBody List<QuestionSolveRequest> questionSolveRequests) {
		for (Iterator iterator = questionSolveRequests.iterator(); iterator.hasNext();) {
			QuestionSolveRequest questionSolveRequest = (QuestionSolveRequest) iterator.next();
			answerQuestion(questionSolveRequest.getContentId(), questionSolveRequest.getSelectedQuestionOptionId());
		}
		return "success";
	}
	
		
	@GetMapping("/flushall")
    @Timed
    public String flushall() {
		return redisService.flushAll();
	}
	
	public RedisServiceService getRedisService() {
		return redisService;
	}

	public void setRedisService(RedisServiceService redisService) {
		this.redisService = redisService;
	}
}
