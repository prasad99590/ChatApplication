package com.learn.model;

import java.util.Date;
import java.util.Set;

public class Message extends PersistantObject{
	private int mid;
	private String content;
	private User sender;
	private Date createdAt;
	private User recieverUser;
	private Group recieverGroup;
	public int getMid() {
		return mid;
	}
	public void setMid(int mid) {
		this.mid = mid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public User getSender() {
		return sender;
	}
	public void setSender(User sender) {
		this.sender = sender;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public User getRecieverUser() {
		return recieverUser;
	}
	public void setRecieverUser(User recieverUser) {
		this.recieverUser = recieverUser;
	}
	public Group getRecieverGroup() {
		return recieverGroup;
	}
	public void setRecieverGroup(Group recieverGroup) {
		this.recieverGroup = recieverGroup;
	}
	
	@Override
	public void detach(Set<Object> detachedValues) {
		if(sender != null) sender.detach(detachedValues);
		if(recieverUser != null) recieverUser.detach(detachedValues);
		if(recieverGroup != null) recieverGroup.detach(detachedValues);
	}
}
