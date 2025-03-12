package com.learn.service;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.learn.model.User;

@RemoteServiceRelativePath("user")
public interface UserService extends RemoteService{
	User validateUser(String username, String password);
	List<User> loadUsers();
	void updateStatusOnline(String username);
	void updateStatusOffline(String username);
	void addFriend(int uid1, int uid2);
	Set<User> getFriends(int id);
	User getUserById(int id);
	List<User> getUsersForSearch(String name);
	User saveUser(String username, String password);
}
