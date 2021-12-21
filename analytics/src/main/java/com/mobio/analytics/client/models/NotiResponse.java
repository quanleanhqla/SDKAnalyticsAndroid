package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

public class NotiResponse{
	@SerializedName("source_screen")
	private String sourceScreen;
	@SerializedName("data")
	private String data;
	@SerializedName("des_screen")
	private String desScreen;
	@SerializedName("type")
	private int type;
	@SerializedName("title")
	private String title;
	@SerializedName("content")
	private String content;

	public void setSourceScreen(String sourceScreen){
		this.sourceScreen = sourceScreen;
	}

	public String getSourceScreen(){
		return sourceScreen;
	}

	public void setData(String data){
		this.data = data;
	}

	public String getData(){
		return data;
	}

	public void setDesScreen(String desScreen){
		this.desScreen = desScreen;
	}

	public String getDesScreen(){
		return desScreen;
	}

	public void setType(int type){
		this.type = type;
	}

	public int getType(){
		return type;
	}

	public void setTitle(String title){
		this.title = title;
	}

	public String getTitle(){
		return title;
	}

	public void setContent(String content){
		this.content = content;
	}

	public String getContent(){
		return content;
	}

	@Override
 	public String toString(){
		return 
			"NotiResponse{" + 
			"source_screen = '" + sourceScreen + '\'' + 
			",data = '" + data + '\'' + 
			",des_screen = '" + desScreen + '\'' + 
			",type = '" + type + '\'' + 
			",title = '" + title + '\'' + 
			",content = '" + content + '\'' + 
			"}";
		}
}
