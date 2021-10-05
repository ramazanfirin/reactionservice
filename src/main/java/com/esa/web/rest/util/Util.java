package com.esa.web.rest.util;

import java.util.Calendar;
import java.util.Date;

public class Util {

	public static String getContentViewKey(String contentId) {
		return "content:"+contentId+":view";
	}
	
	public static String getContentLikeKey(String contentId) {
		return "content:"+contentId+":like";
	}
	
	public static String getRecentLikeKey() {
		return "content:recent:like";
	}
	
	public static String getAnsweredQuestionKey(String contentId) {
		return "question:"+contentId+":answer";
	}
	
	public static String getAnsweredOptionKey(String contentId,String selectedQuestionOptionId) {
		return "question:"+contentId+":"+selectedQuestionOptionId;
	}
	
	public static String getRecentQuestionStatisticKey() {
		return "question:recent:statistic";
	}
	
	public static String getDummyKey() {
		return "dummy";
	}
	
	public static String getOptionIdFromAnsweredOptionKey(String allKey,String contentId) {
		String temp = "question:"+contentId+":";
		return allKey.replace(temp, "");
	}
	
	
	public static Date calculateExpireDate(Long dateValue,Long hours) {
		Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(dateValue);
	    if(hours==-1)
	    	calendar.add(Calendar.MINUTE, 1);
	    else
	    	calendar.add(Calendar.HOUR_OF_DAY, hours.intValue());
	    return calendar.getTime();
	}
}
