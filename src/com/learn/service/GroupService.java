package com.learn.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.learn.model.Group;
import com.learn.model.User;

@RemoteServiceRelativePath("group")
public interface GroupService extends RemoteService {
	Group createGroup(String groupName, int creatorId);
	void addMembers(int gid, List<Integer> members);
	void removeMember(int gid, int uid);
	Group getGroupById(int gid);
	void removeGroup(int gid);
}
