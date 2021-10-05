package com.esa.web.rest.vm;

public class GetRecentReactionResponseItem {
	
	String contentId;
	Long viewCount;
	Long likeCount;
	
	public GetRecentReactionResponseItem() {
		super();
		// TODO Auto-generated constructor stub
	}
	public GetRecentReactionResponseItem(String contentId, Long viewCount, Long likeCount) {
		super();
		this.contentId = contentId;
		this.viewCount = viewCount;
		this.likeCount = likeCount;
	}
	public String getContentId() {
		return contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	public Long getViewCount() {
		return viewCount;
	}
	public void setViewCount(Long viewCount) {
		this.viewCount = viewCount;
	}
	public Long getLikeCount() {
		return likeCount;
	}
	public void setLikeCount(Long likeCount) {
		this.likeCount = likeCount;
	}

	
}
