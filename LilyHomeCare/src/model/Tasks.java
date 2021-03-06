package model;

import java.util.Date;

public class Tasks {
	private String taskName;
	private String taskTagID;
	private String careGiver;
	private String taskDetail;
	
	private Date taskStartingTime=null;
	private Date taskEndingTime=null;
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getTaskTagID() {
		return taskTagID;
	}
	public void setTaskTagID(String taskTagID) {
		this.taskTagID = taskTagID;
	}
	public String getCareGiver() {
		return careGiver;
	}
	public void setCareGiver(String careGiver) {
		this.careGiver = careGiver;
	}
	public String getTaskDetail() {
		return taskDetail;
	}
	public void setTaskDetail(String taskDetail) {
		this.taskDetail = taskDetail;
	}
	public Date getTaskStartingTime() {
		return taskStartingTime;
	}
	public void setTaskStartingTime(Date taskStartingTime) {
		this.taskStartingTime = taskStartingTime;
	}
	public Date getTaskEndingTime() {
		return taskEndingTime;
	}
	public void setTaskEndingTime(Date taskEndingTime) {
		this.taskEndingTime = taskEndingTime;
	}
	
	
}
