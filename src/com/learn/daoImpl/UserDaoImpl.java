package com.learn.daoImpl;

import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import com.learn.dao.UserDao;
import com.learn.model.User;
import com.learn.serviceImpl.UserServiceImpl;

public class UserDaoImpl implements UserDao {
	private SessionFactory sessionFactory;
	private static final Logger logger = Logger.getLogger(UserDaoImpl.class);

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public List<User> loadUsers() {
		Session session = sessionFactory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.select(root);
		List<User> users = session.createQuery(query).getResultList();
		session.close();
		logger.info("Loaded users from userdaoimpl");
		return users;
	}

	@Override
	public void updateStatusOnline(String username) {
		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(
				"UPDATE User SET status = :status WHERE username = :username");
		query.setParameter("status", "online");
		query.setParameter("username", username);
		query.executeUpdate();
		logger.info("updated user online in userdaoimpl");
	}

	@Override
	public void updateStatusOffline(String username) {
		Session session = sessionFactory.getCurrentSession();
		Query<?> query = session.createQuery(
				"UPDATE User SET status = :status WHERE username = :username");
		query.setParameter("status", "offline");
		query.setParameter("username", username);
		query.executeUpdate();
		logger.info("updated user offline in userdaoimpl");
	}

	@Override
	public User getUserByName(String username) {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.select(root).where(cb.equal(root.get("username"), username));
		logger.info("getting user by username from userdaoimpl");
		return session.createQuery(query).uniqueResult();
	}

	@Override
	public User validateUser(String username, String password) {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.select(root).where(cb.equal(root.get("username"), username));
		logger.info("getting user by username from userdaoimpl");
		return session.createQuery(query).uniqueResult();
	}

	public String getMsg() {
		return "Hello from UserDaoImpl";
	}

	@Override
	public void addFriend(int uid1, int uid2) {
		Session session = sessionFactory.getCurrentSession();
		User user1 = session.get(User.class, uid1);
		User user2 = session.get(User.class, uid2);
		user1.getFriends().add(user2);
		user2.getFriends().add(user1);
		session.save(user1);
		session.save(user2);
		logger.info("added friend in userdaoimpl");
	}

	@Override
	public Set<User> getFriends(int uid) {
		Session session = sessionFactory.getCurrentSession();
		User user = session.get(User.class, uid);
		logger.info("loaded friends in userdaoimpl");
		return user.getFriends();
	}

	@Override
	public User getUserById(int id) {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.select(root).where(cb.equal(root.get("uid"), id));
		logger.info("getting user by id from userdaoimpl");
		return session.createQuery(query).uniqueResult();
	}

	@Override
	public List<User> getUsersForSearch(String name) {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<User> query = cb.createQuery(User.class);
		Root<User> root = query.from(User.class);
		query.select(root).where(cb.like(root.get("username"), "%" + name + "%"));
		logger.info("getting search results in userdaoimpl");
		return session.createQuery(query).getResultList();
	}

	@Override
	public User saveUser(User user) {
		// TODO Auto-generated method stub
		Session session = sessionFactory.getCurrentSession();
		User dbUser = getUserByName(user.getUsername());
		if(dbUser != null) {
			logger.info("Username already exists");
			return null;
		}
		else {
			session.save(user);
			logger.info("user saved successfully");
			return getUserByName(user.getUsername());
		}
	}
}
