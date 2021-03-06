package model;

import java.util.Vector;
import java.util.Date;

public class Customer {
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String tagId;
	private String address;
	private Vector<Tasks>tasks=new Vector<Tasks>();
	private Date startTime=null;
	private Date finishingTime=null;
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Vector<Tasks> getTasks() {
		return tasks;
	}
	public void add(Tasks task){
		this.tasks.add(task);
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getFinishingTime() {
		return finishingTime;
	}

	public void setFinishingTime(Date finishingTime) {
		this.finishingTime = finishingTime;
	}
	
	

}
