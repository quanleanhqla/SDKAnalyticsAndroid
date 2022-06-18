package com.mobio.analytics.client.model.old;

import com.google.gson.annotations.SerializedName;

public class EventData{
	@SerializedName("time_visit")
	private int timeVisit;
	@SerializedName("action_time")
	private String actionTime;
	@SerializedName("screen_name")
	private String screenName;

	public void setTimeVisit(int timeVisit){
		this.timeVisit = timeVisit;
	}

	public int getTimeVisit(){
		return timeVisit;
	}

	public void setActionTime(String actionTime){
		this.actionTime = actionTime;
	}

	public String getActionTime(){
		return actionTime;
	}

	public void setScreenName(String screenName){
		this.screenName = screenName;
	}

	public String getScreenName(){
		return screenName;
	}

	@Override
 	public String toString(){
		return 
			"EventData{" + 
			"time_visit = '" + timeVisit + '\'' + 
			",action_time = '" + actionTime + '\'' + 
			",screen_name = '" + screenName + '\'' + 
			"}";
		}
}
