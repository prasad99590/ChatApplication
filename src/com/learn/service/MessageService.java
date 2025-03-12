package com.learn.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.learn.model.Message;
import com.learn.model.MessageDelivered;

@RemoteServiceRelativePath("message")
public interface MessageService extends RemoteService{
	Message saveMyMessage(int senderId, int recieverId, String chatType, String message);
	Message getMessageById(int mid);
	List<Message> getMessagesInGroup(int gid);
	List<Message> getMessagesInUser(int senderId, int recieverId);
	
	List<MessageDelivered> getSeenMembers(int mid);
	void updateSeenAt(int seenById, int chatId, String chatType);
	int getUnreadCount(int senderId, int chatId, String chatType);
}
