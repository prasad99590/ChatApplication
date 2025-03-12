package com.learn.dao;

import java.util.List;
import java.util.Set;

import com.learn.model.User;

public interface UserDao {
	List<User> loadUsers();
	void updateStatusOnline(String username);
	void updateStatusOffline(String username);
	User getUserByName(String username);
	User validateUser(String username, String password);
	void addFriend(int uid1, int uid2);
	Set<User> getFriends(int uid);
	User getUserById(int id);
	List<User> getUsersForSearch(String name);
	User saveUser(User user);
}
