package com.learn.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.learn.model.Group;
import com.learn.model.User;

public interface GroupServiceAsync {
	void createGroup(String groupName, int creatorId, AsyncCallback<Group> callback);
	void addMembers(int gid, List<Integer> members, AsyncCallback<Void> callback);
	void removeMember(int gid, int uid, AsyncCallback<Void> callback);
	void getGroupById(int gid, AsyncCallback<Group> callback);
	void removeGroup(int gid, AsyncCallback<Void> callback);
}
