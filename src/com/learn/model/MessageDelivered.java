package com.learn.model;

import java.util.Date;
import java.util.Set;

public class MessageDelivered extends PersistantObject{
	private int id;
	private User seenBy;
	private Date seenAt;
	private Message message;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getSeenBy() {
		return seenBy;
	}
	public void setSeenBy(User seenBy) {
		this.seenBy = seenBy;
	}
	public Date getSeenAt() {
		return seenAt;
	}
	public void setSeenAt(Date seenAt) {
		this.seenAt = seenAt;
	}
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	
	@Override
	public void detach(Set<Object> detachedValues) {
		if(seenBy != null) seenBy.detach(detachedValues);
		if(message != null) message.detach(detachedValues);
	}
}
