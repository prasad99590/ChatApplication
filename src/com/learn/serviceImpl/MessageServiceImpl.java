package com.learn.serviceImpl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.learn.daoImpl.GroupDaoImpl;
import com.learn.daoImpl.MessageDaoImpl;
import com.learn.daoImpl.UserDaoImpl;
import com.learn.model.Message;
import com.learn.model.MessageDelivered;
import com.learn.service.MessageService;

public class MessageServiceImpl extends RemoteServiceServlet implements MessageService{
	private static MessageDaoImpl messageDao;
	private static GroupDaoImpl groupDao;
	private static UserDaoImpl userDao;
	private static final Logger logger = Logger.getLogger(MessageServiceImpl.class);

	public MessageDaoImpl getMessageDao() {
		return messageDao;
	}

	public void setMessageDao(MessageDaoImpl messageDao) {
		MessageServiceImpl.messageDao = messageDao;
	}
	
	public GroupDaoImpl getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(GroupDaoImpl groupDao) {
		MessageServiceImpl.groupDao = groupDao;
	}

	public UserDaoImpl getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDaoImpl userDao) {
		MessageServiceImpl.userDao = userDao;
	}

	@Override
	public Message saveMyMessage(int senderId, int recieverId, String chatType, String message) {
		// TODO Auto-generated method stub
		System.out.println("In message service...\n\n\n");
		Message m = new Message();
		m.setContent(message);
		m.setCreatedAt(new Date());
		m.setSender(userDao.getUserById(senderId));
		if(chatType.equals("User")) {
			m.setRecieverUser(userDao.getUserById(recieverId));
		}
		else {
			m.setRecieverGroup(groupDao.getGroupById(recieverId));
		}
		Message result = messageDao.saveMessage(m);
		result.detach(new HashSet<Object>());
		logger.info("saved message in messageserviceimpl");
		return result;
	}

	@Override
	public List<Message> getMessagesInGroup(int gid) {
		// TODO Auto-generated method stub
		List<Message> messages = messageDao.getMessagesInGroup(gid);
		for(Message m : messages) {
			m.detach(new HashSet<Object>());
		}
		logger.info("detached messages in get messages in group method");
		return messages;
	}

	@Override
	public List<Message> getMessagesInUser(int senderId, int recieverId) {
		// TODO Auto-generated method stub
		List<Message> messages = messageDao.getMessagesInUser(senderId, recieverId);
		for(Message m : messages) {
			m.detach(new HashSet<Object>());
		}
		logger.info("detached messages in get messages in user method");
		return messages;
	}

	@Override
	public Message getMessageById(int mid) {
		// TODO Auto-generated method stub
		Message m = messageDao.getMessageById(mid);
		m.detach(new HashSet<Object>());
		logger.info("detached message in get message by id method");
		return m;
	}

	@Override
	public List<MessageDelivered> getSeenMembers(int mid) {
		// TODO Auto-generated method stub
		List<MessageDelivered> members = messageDao.getSeenMembers(mid);
		for(MessageDelivered md : members) {
			md.detach(new HashSet<Object>());
		}
		logger.info("detached message delivered in get seen members method");
		return members;
	}

	@Override
	public void updateSeenAt(int seenById, int chatId, String chatType) {
		// TODO Auto-generated method stub
		messageDao.updateSeenAt(seenById, chatId, chatType);
		logger.info("updated seen at in messageserviceimpl");
	}

	@Override
	public int getUnreadCount(int senderId, int chatId, String chatType) {
		// TODO Auto-generated method stub
		logger.info("getting unread count in messageserviceimpl");
		return messageDao.getUnreadCount(senderId, chatId, chatType);
	}

}
