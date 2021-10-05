package com.esa.web.rest.vm;

public class GetSmallReactionItem {
	
	SmallReactionContentType contentType;
	Boolean liked;
	Boolean viewed;
	Boolean answered;
	
	
	
	public GetSmallReactionItem() {
		super();
	}
	public GetSmallReactionItem(SmallReactionContentType contentType, Boolean liked, Boolean viewed, Boolean answered) {
		super();
		this.contentType = contentType;
		this.liked = liked;
		this.viewed = viewed;
		this.answered = answered;
	}
	public SmallReactionContentType getContentType() {
		return contentType;
	}
	public void setContentType(SmallReactionContentType contentType) {
		this.contentType = contentType;
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


}
