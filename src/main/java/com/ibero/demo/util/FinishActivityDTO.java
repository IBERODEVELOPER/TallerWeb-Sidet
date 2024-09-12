package com.ibero.demo.util;

public class FinishActivityDTO {
	
	private String activityId;
	private String endTime; // Fecha y hora de finalización
    private String totalTime; //Tiempo tomado en el evento
    private String detailsevent;
    private boolean isClosed = true;
    
	public boolean isClosed() {
		return isClosed;
	}
	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}
	public String getDetailsevent() {
		return detailsevent;
	}
	public void setDetailsevent(String detailsevent) {
		this.detailsevent = detailsevent;
	}
}
