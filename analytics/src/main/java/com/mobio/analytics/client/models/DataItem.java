package com.mobio.analytics.client.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataItem {
	public static final String NODE_CODE_EVENT = "EVENT";
	public static final String NODE_CODE_PUSH_IN_APP = "PUSH_IN_APP";
	public static final String NODE_CODE_CONDITION = "CONDITION";

	@SerializedName("data")
	private List<DataItem> data;
	@SerializedName("node_code")
	private String nodeCode;
	@SerializedName("node_name")
	private String nodeName;
	@SerializedName("node_id")
	private String nodeId;
	@SerializedName("enable")
	private boolean enable;
	@SerializedName("length")
	private int length;
	@SerializedName("event_data")
	private EventData eventData;
	@SerializedName("event_key")
	private String eventKey;
	@SerializedName("noti_response")
	private NotiResponseObject notiResponse;

	public void setData(List<DataItem> data){
		this.data = data;
	}

	public List<DataItem> getData(){
		return data;
	}

	public void setNodeCode(String nodeCode){
		this.nodeCode = nodeCode;
	}

	public String getNodeCode(){
		return nodeCode;
	}

	public void setNodeName(String nodeName){
		this.nodeName = nodeName;
	}

	public String getNodeName(){
		return nodeName;
	}

	public void setNodeId(String nodeId){
		this.nodeId = nodeId;
	}

	public String getNodeId(){
		return nodeId;
	}

	public void setEnable(boolean enable){
		this.enable = enable;
	}

	public boolean isEnable(){
		return enable;
	}

	public void setLength(int length){
		this.length = length;
	}

	public int getLength(){
		return length;
	}

	public void setEventData(EventData eventData){
		this.eventData = eventData;
	}

	public EventData getEventData(){
		return eventData;
	}

	public void setNotiResponse(NotiResponseObject notiResponse){
		this.notiResponse = notiResponse;
	}

	public NotiResponseObject getNotiResponse(){
		return notiResponse;
	}

	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	@Override
	public String toString() {
		return "DataItem{" +
				"data=" + data +
				", nodeCode='" + nodeCode + '\'' +
				", nodeName='" + nodeName + '\'' +
				", nodeId='" + nodeId + '\'' +
				", enable=" + enable +
				", length=" + length +
				", eventData=" + eventData +
				", notiResponse=" + notiResponse +
				'}';
	}
}