package com.learn.dao;

import java.util.List;

import com.learn.model.Group;
import com.learn.model.User;

public interface GroupDao {
	Group createGroup(String groupName, int creatorId);
	void addMembers(int gid, List<Integer> members);
	void removeMember(int gid, int uid);
	Group getGroupById(int gid);
	void removeGroup(int gid);
}
