package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class JourneyObject{
	public static final int TYPE_TODO_RUNNING = 2;
	public static final int TYPE_TODO_ACTIVE = 0;
	public static final int TYPE_TODO_DISACTIVE = 1;

	public static final int TYPE_FLOW = 0;
	public static final int TYPE_JB = 1;

	@SerializedName("data")
	private List<DataItem> data;
	@SerializedName("repeat")
	private String repeat;
	@SerializedName("type_repeat")
	private int typeRepeat;
	@SerializedName("concrete_time")
	private String concreteTime;
	@SerializedName("type_todo")
	private int typeTodo;
	@SerializedName("id")
	private int id;
	@SerializedName("type")
	private int type;

	public void setData(List<DataItem> data){
		this.data = data;
	}

	public List<DataItem> getData(){
		return data;
	}

	public void setRepeat(String repeat){
		this.repeat = repeat;
	}

	public String getRepeat(){
		return repeat;
	}

	public void setTypeRepeat(int typeRepeat){
		this.typeRepeat = typeRepeat;
	}

	public int getTypeRepeat(){
		return typeRepeat;
	}

	public void setConcreteTime(String concreteTime){
		this.concreteTime = concreteTime;
	}

	public String getConcreteTime(){
		return concreteTime;
	}

	public void setTypeTodo(int typeTodo){
		this.typeTodo = typeTodo;
	}

	public int getTypeTodo(){
		return typeTodo;
	}

	@Override
	public String toString() {
		return "JourneyObject{" +
				"data=" + data +
				", repeat='" + repeat + '\'' +
				", typeRepeat=" + typeRepeat +
				", concreteTime='" + concreteTime + '\'' +
				", typeTodo=" + typeTodo +
				'}';
	}
}