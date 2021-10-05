package com.esa.web.rest.vm;

public class GetSmallReactionResponse {
	
	String contentId;
	SmallReactionContentType contentType;
	
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
