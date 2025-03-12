package com.learn.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.learn.daoImpl.UserDaoImpl;
import com.learn.model.PersistantObject;
import com.learn.model.User;
import com.learn.service.UserService;

public class UserServiceImpl extends RemoteServiceServlet implements UserService{
	private static UserDaoImpl userDao;
	private static final Logger logger = Logger.getLogger(UserServiceImpl.class);
	
	public UserDaoImpl getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDaoImpl usersDao) {
		userDao = usersDao;
	}

	@Override
	public List<User> loadUsers() {
		List<User> users = userDao.loadUsers();
		for(User user : users) {
			user.detach(new HashSet<Object>());
		}
		logger.info("Detach done for load users");
		return users;
	}

	@Override
	public void updateStatusOnline(String username) {
		userDao.updateStatusOnline(username);
		logger.info("Updated status for online...");
	}

	@Override
	public void updateStatusOffline(String username) {
		userDao.updateStatusOffline(username);
		logger.info("Updated status for offline...");
	}
	@Override
	public User validateUser(String username, String password) {
		// TODO Auto-generated method stub
		User user = userDao.validateUser(username, password);
		if(user == null || (!(password.equals(user.getPassword())))) return null;
		user.detach(new HashSet<Object>());
		logger.info("Detach done for user in validate user...");
		return user;
	}

	@Override
	public void addFriend(int uid1, int uid2) {
		// TODO Auto-generated method stub
		userDao.addFriend(uid1, uid2);
		logger.info("added friend in userserviceimpl");
	}

	@Override
	public Set<User> getFriends(int id) {
		// TODO Auto-generated method stub
		Set<User> friends = userDao.getFriends(id);
		for(User friend : friends) {
			friend.detach(new HashSet<Object>());
		}
		logger.info("detached users in get friends method");
		return friends;
	}

	@Override
	public User getUserById(int id) {
		// TODO Auto-generated method stub
		User user = userDao.getUserById(id);
		user.detach(new HashSet<Object>());
		logger.info("detached user in get user by id method");
		return user;
	}

	@Override
	public List<User> getUsersForSearch(String name) {
		// TODO Auto-generated method stub
		List<User> results = userDao.getUsersForSearch(name);
		for(User user : results) {
			user.detach(new HashSet<Object>());
		}
		logger.info("detached users in get users for search method");
		return results;
	}

	@Override
	public User saveUser(String username, String password) {
		// TODO Auto-generated method stub
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		User dbUser = userDao.saveUser(user);
		if(dbUser == null) {
			logger.info("user already exists, recieved null, cannot add duplicate user...");
			return null;
		}
		dbUser.detach(new HashSet<Object>());
		logger.info("detached user in save user method");
		return dbUser;
	}

}
