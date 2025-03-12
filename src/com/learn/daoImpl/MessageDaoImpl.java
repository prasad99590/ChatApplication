package com.learn.daoImpl;

import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.learn.dao.MessageDao;
import com.learn.model.Message;
import com.learn.model.MessageDelivered;
import com.learn.model.User;
import com.learn.serviceImpl.UserServiceImpl;

public class MessageDaoImpl implements MessageDao {
	private SessionFactory sessionFactory;
	private static final Logger logger = Logger.getLogger(MessageDaoImpl.class);

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Message saveMessage(Message message) {
		Session session = sessionFactory.getCurrentSession();
		session.save(message);

		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Message> query = cb.createQuery(Message.class);
		Root<Message> root = query.from(Message.class);
		query.select(root).where(cb.equal(root.get("mid"), message.getMid()));
		Message result = session.createQuery(query).uniqueResult();

		if (message.getRecieverGroup() == null) {
			MessageDelivered md = new MessageDelivered();
			md.setMessage(message);
			md.setSeenBy(message.getRecieverUser());
			session.save(md);
		} else {
			for (User receiver : message.getRecieverGroup().getMembers()) {
				if (message.getSender().getUid() != receiver.getUid()) {
					MessageDelivered md = new MessageDelivered();
					md.setMessage(message);
					md.setSeenBy(receiver);
					session.save(md);
				}
			}
		}
		logger.info("saved message in messagedaoimpl");
		return result;
	}

	@Override
	public List<Message> getMessagesInGroup(int gid) {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Message> query = cb.createQuery(Message.class);
		Root<Message> root = query.from(Message.class);
		query.select(root).where(cb.equal(root.get("recieverGroup").get("gid"), gid));
		logger.info("getting messages in messagedaoimpl");
		return session.createQuery(query).getResultList();
	}

	@Override
	public List<Message> getMessagesInUser(int senderId, int recieverId) {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Message> query = cb.createQuery(Message.class);
		Root<Message> root = query.from(Message.class);
		query.select(root).where(cb.or(
				cb.and(cb.equal(root.get("sender").get("uid"), senderId),
						cb.equal(root.get("recieverUser").get("uid"), recieverId)),
				cb.and(cb.equal(root.get("sender").get("uid"), recieverId),
						cb.equal(root.get("recieverUser").get("uid"), senderId))));
		logger.info("getting messages for a user one-to-one chat in messagedaoimpl");
		return session.createQuery(query).getResultList();
	}

	@Override
	public Message getMessageById(int mid) {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Message> query = cb.createQuery(Message.class);
		Root<Message> root = query.from(Message.class);
		query.select(root).where(cb.equal(root.get("mid"), mid));
		logger.info("getting message by id in messagedaoimpl");
		return session.createQuery(query).uniqueResult();
	}

	@Override
	public List<MessageDelivered> getSeenMembers(int mid) {
		Session session = sessionFactory.getCurrentSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<MessageDelivered> query = cb.createQuery(MessageDelivered.class);
		Root<MessageDelivered> root = query.from(MessageDelivered.class);
		query.select(root).where(cb.equal(root.get("message").get("mid"), mid));
		logger.info("getting seen members in messagedaoimpl");
		return session.createQuery(query).getResultList();
	}

	@Override
	public void updateSeenAt(int seenById, int chatId, String chatType) {
		Session session = sessionFactory.getCurrentSession();
		List<Message> messages;
		if (chatType.equals("User")) {
			messages = getMessagesInUser(seenById, chatId);
		} else {
			messages = getMessagesInGroup(chatId);
		}
		for (Message message : messages) {
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<MessageDelivered> query = cb.createQuery(MessageDelivered.class);
			Root<MessageDelivered> root = query.from(MessageDelivered.class);
			query.select(root).where(cb.and(cb.equal(root.get("seenBy").get("uid"), seenById),
					cb.equal(root.get("message").get("mid"), message.getMid())));
			MessageDelivered toBeUpdated = session.createQuery(query).uniqueResult();
			logger.info("For updating a message, I recieved : "+toBeUpdated+"\n\n\n\n\n\n\n\n\n\n\n\n");
			if (toBeUpdated != null && toBeUpdated.getSeenAt() == null) {
				toBeUpdated.setSeenAt(new Date());
				session.saveOrUpdate(toBeUpdated);
				logger.info("message updated : "+toBeUpdated+"\n\n\n\n\n\n\n\n\n\n\n\n");
			}
		}
		logger.info("updated seen at in messagedaoimpl");
	}

	@Override
	public int getUnreadCount(int senderId, int chatId, String chatType) {
		Session session = sessionFactory.getCurrentSession();
		List<Message> messages;
		int unreadCount = 0;
		if (chatType.equals("User")) {
			messages = getMessagesInUser(senderId, chatId);
		} else {
			messages = getMessagesInGroup(chatId);
		}
		for (Message message : messages) {
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<MessageDelivered> query = cb.createQuery(MessageDelivered.class);
			Root<MessageDelivered> root = query.from(MessageDelivered.class);
			query.select(root).where(cb.and(cb.equal(root.get("seenBy").get("uid"), senderId),
					cb.equal(root.get("message").get("mid"), message.getMid())));
			MessageDelivered toBeUpdated = session.createQuery(query).uniqueResult();
			if (toBeUpdated != null && toBeUpdated.getSeenAt() == null) {
				unreadCount++;
			}
		}
		logger.info("For sender id : "+senderId+" and chatId : "+chatId+" type : "+chatType+" unread count : "+unreadCount);
		return unreadCount;
	}
}
