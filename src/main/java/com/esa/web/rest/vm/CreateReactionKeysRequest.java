package com.esa.web.rest.vm;

public class CreateReactionKeysRequest {

	String contentId;
	Long publishAtInMs;
	
	public String getContentId() {
		return contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	public Long getPublishAtInMs() {
		return publishAtInMs;
	}
	public void setPublishAtInMs(Long publishAtInMs) {
		this.publishAtInMs = publishAtInMs;
	}
	
	
}
