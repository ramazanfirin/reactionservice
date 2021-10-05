package com.esa.web.rest.vm;

public class GetReactionsRequest {

	String studentId;
	String contentId;
	SmallReactionContentType contentType;
	
	
	
	public String getStudentId() {
		return studentId;
	}
	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}
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
	
}
