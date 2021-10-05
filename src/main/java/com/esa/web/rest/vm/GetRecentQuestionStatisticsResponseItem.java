package com.esa.web.rest.vm;

import java.util.ArrayList;
import java.util.List;

public class GetRecentQuestionStatisticsResponseItem {

	String contentId;
	
	Long totalNumberOfSelections;
	
	List<QuestionOptionStatistics> questionOptionStatistics = new ArrayList<QuestionOptionStatistics>();

	
	public String getContentId() {
		return contentId;
	}

	public void setContentId(String contentId) {
		this.contentId = contentId;
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
