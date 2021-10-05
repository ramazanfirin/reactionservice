package com.esa.web.rest.vm;

import java.util.ArrayList;
import java.util.List;

public class GetReactionsResponse {

	String contentId;
	SmallReactionContentType contentType;
	Long numberOfViews;
	Long numberOfLikes;
	Boolean liked;
	Boolean viewed;
	Boolean answered;
	
	Long totalNumberOfSelections;
	
	List<QuestionOptionStatistics> questionOptionStatistics = new ArrayList<QuestionOptionStatistics>();

	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
	}

	public SmallReactionContentType getContentType() {
		return contentType;
	}

	public void setContentType(SmallReactionContentType contentType) {
		this.contentType = contentType;
	}

	public Long getNumberOfViews() {
		return numberOfViews;
	}

	public void setNumberOfViews(Long numberOfViews) {
		this.numberOfViews = numberOfViews;
	}

	public Long getNumberOfLikes() {
		return numberOfLikes;
	}

	public void setNumberOfLikes(Long numberOfLikes) {
		this.numberOfLikes = numberOfLikes;
	}

	public Boolean getLiked() {
		return liked;
	}

	public void setLiked(Boolean liked) {
		this.liked = liked;
	}

	public Boolean getViewed() {
		return viewed;
	}

	public void setViewed(Boolean viewed) {
		this.viewed = viewed;
	}

	public Boolean getAnswered() {
		return answered;
	}

	public void setAnswered(Boolean answered) {
		this.answered = answered;
	}

	public Long getTotalNumberOfSelections() {
		return totalNumberOfSelections;
	}

	public void setTotalNumberOfSelections(Long totalNumberOfSelections) {
		this.totalNumberOfSelections = totalNumberOfSelections;
	}

	public List<QuestionOptionStatistics> getQuestionOptionStatistics() {
		return questionOptionStatistics;
	}

	public void setQuestionOptionStatistics(List<QuestionOptionStatistics> questionOptionStatistics) {
		this.questionOptionStatistics = questionOptionStatistics;
	}
	
	
	
	
}
