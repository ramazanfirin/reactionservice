package com.esa.web.rest.vm;

public class QuestionSolveRequest {
	
	String contentId;
	String selectedQuestionOptionId;
	
	
	public QuestionSolveRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	public QuestionSolveRequest(String contentId, String selectedQuestionOptionId) {
		super();
		this.contentId = contentId;
		this.selectedQuestionOptionId = selectedQuestionOptionId;
	}
	public String getContentId() {
		return contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	public String getSelectedQuestionOptionId() {
		return selectedQuestionOptionId;
	}
	public void setSelectedQuestionOptionId(String selectedQuestionOptionId) {
		this.selectedQuestionOptionId = selectedQuestionOptionId;
	}
}
