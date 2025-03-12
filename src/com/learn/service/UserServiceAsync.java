package com.learn.service;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.learn.model.User;

public interface UserServiceAsync {
	void validateUser(String username, String password, AsyncCallback<User> callback);
	void loadUsers(AsyncCallback<List<User>> callback);
	void updateStatusOnline(String username, AsyncCallback<Void> callback);
	void updateStatusOffline(String username, AsyncCallback<Void> callback);
	void addFriend(int uid1, int uid2, AsyncCallback<Void> callback);
	void getFriends(int id, AsyncCallback<Set<User>> callback);
	void getUserById(int id, AsyncCallback<User> callback);
	void getUsersForSearch(String name, AsyncCallback<List<User>> callback);
	void saveUser(String username, String password, AsyncCallback<User> callback);
}
