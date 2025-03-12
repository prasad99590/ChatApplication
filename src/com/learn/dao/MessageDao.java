package com.learn.dao;

import java.util.List;

import com.learn.model.Group;
import com.learn.model.Message;
import com.learn.model.MessageDelivered;

public interface MessageDao {
	Message saveMessage(Message message);
	Message getMessageById(int mid);
	List<Message> getMessagesInGroup(int gid);
	List<Message> getMessagesInUser(int senderId, int recieverId);
	
	List<MessageDelivered> getSeenMembers(int mid);
	void updateSeenAt(int seenById, int chatId, String chatType);
	int getUnreadCount(int senderId, int chatId, String chatType);
}
