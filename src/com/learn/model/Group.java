package com.learn.model;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class Group extends PersistantObject{
	private int gid;
	private String gname;
	private User createdBy;
	private Set<User> members;
	public int getGid() {
		return gid;
	}
	public void setGid(int gid) {
		this.gid = gid;
	}
	public String getGname() {
		return gname;
	}
	public void setGname(String gname) {
		this.gname = gname;
	}
	public User getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}
	
	
	public Set<User> getMembers() {
		return members;
	}
	public void setMembers(Set<User> members) {
		this.members = members;
	}
	@Override
	public void detach(Set<Object> detachedValues) {
		members = detachSet(members, detachedValues);
	}
}
