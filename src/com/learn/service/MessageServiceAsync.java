package com.learn.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.learn.model.Message;
import com.learn.model.MessageDelivered;

public interface MessageServiceAsync {
	void saveMyMessage(int senderId, int recieverId, String chatType, String message, AsyncCallback<Message> callback);
	void getMessageById(int mid, AsyncCallback<Message> callback);
	void getMessagesInGroup(int gid, AsyncCallback<List<Message>> callback);
	void getMessagesInUser(int senderId, int recieverId, AsyncCallback<List<Message>> callback);
	
	void getSeenMembers(int mid, AsyncCallback<List<MessageDelivered>> callback);
	void updateSeenAt(int seenById, int chatId, String chatType, AsyncCallback<Void> callback);
	void getUnreadCount(int senderId, int chatId, String chatType, AsyncCallback<Integer> callback);
}
