package com.learn.daoImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import com.learn.dao.GroupDao;
import com.learn.model.Group;
import com.learn.model.User;
import com.learn.serviceImpl.UserServiceImpl;

public class GroupDaoImpl implements GroupDao {
	private SessionFactory sessionFactory;
	private static final Logger logger = Logger.getLogger(GroupDaoImpl.class);

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Group createGroup(String groupName, int creatorId) {
		Session session = sessionFactory.getCurrentSession();
		Group group = new Group();
		group.setGname(groupName);

		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<User> userQuery = cb.createQuery(User.class);
		Root<User> userRoot = userQuery.from(User.class);
		userQuery.select(userRoot).where(cb.equal(userRoot.get("uid"), creatorId));
		User createdBy = session.createQuery(userQuery).uniqueResult();

		group.setCreatedBy(createdBy);
		Set<User> members = new HashSet<>();
		members.add(createdBy);
		group.setMembers(members);
		session.save(group);

		CriteriaQuery<Group> groupQuery = cb.createQuery(Group.class);
		Root<Group> groupRoot = groupQuery.from(Group.class);
		groupQuery.select(groupRoot).where(cb.equal(groupRoot.get("gname"), groupName));
		logger.info("created group in groupdaoimpl");
		return session.createQuery(groupQuery).uniqueResult();
	}

	@Override
	public void addMembers(int gid, List<Integer> members) {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();

		CriteriaQuery<Group> groupQuery = cb.createQuery(Group.class);
		Root<Group> groupRoot = groupQuery.from(Group.class);
		groupQuery.select(groupRoot).where(cb.equal(groupRoot.get("gid"), gid));
		Group group = session.createQuery(groupQuery).uniqueResult();

		for (int memberId : members) {
			CriteriaQuery<User> userQuery = cb.createQuery(User.class);
			Root<User> userRoot = userQuery.from(User.class);
			userQuery.select(userRoot).where(cb.equal(userRoot.get("uid"), memberId));
			User member = session.createQuery(userQuery).uniqueResult();
			group.getMembers().add(member);
		}
		session.saveOrUpdate(group);
		logger.info("added members in group in groupdaoimpl");
	}

	@Override
	public void removeMember(int gid, int uid) {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();

		CriteriaQuery<Group> groupQuery = cb.createQuery(Group.class);
		Root<Group> groupRoot = groupQuery.from(Group.class);
		groupQuery.select(groupRoot).where(cb.equal(groupRoot.get("gid"), gid));
		Group group = session.createQuery(groupQuery).uniqueResult();

		CriteriaQuery<User> userQuery = cb.createQuery(User.class);
		Root<User> userRoot = userQuery.from(User.class);
		userQuery.select(userRoot).where(cb.equal(userRoot.get("uid"), uid));
		User member = session.createQuery(userQuery).uniqueResult();

		group.getMembers().remove(member);
		session.saveOrUpdate(group);
		logger.info("removed member from group in groupdaoimpl");
	}

	@Override
	public Group getGroupById(int gid) {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Group> query = cb.createQuery(Group.class);
		Root<Group> root = query.from(Group.class);
		query.select(root).where(cb.equal(root.get("gid"), gid));
		logger.info("getting group by id in groupdaoimpl");
		return session.createQuery(query).uniqueResult();
	}

	@Override
	public void removeGroup(int gid) {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Group> query = cb.createQuery(Group.class);
		Root<Group> root = query.from(Group.class);
		query.select(root).where(cb.equal(root.get("gid"), gid));
		Group group = session.createQuery(query).uniqueResult();
		session.remove(group);
		logger.info("removing group using id in groupdaoimpl");
	}
}
