package com.learn.websocket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.daoImpl.UserDaoImpl;
import com.learn.model.User;
import com.learn.server.ApplicationContextListener;
import com.learn.serviceImpl.UserServiceImpl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
	
	public static Map<Channel, Integer> authorizedConnections = new HashMap<>(); // (channel -> uid)
	public static Map<Integer, Set<Channel>> clientToConnections = new HashMap<>(); // (uid -> channel)
	
	private static final Logger logger = Logger.getLogger(WebSocketServerHandler.class);
	private final WebSocketServer wsServer = (WebSocketServer) ApplicationContextListener.applicationContext.getBean("wsServer");

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		logger.info("New client connected...");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("Client disconnected : " + authorizedConnections.get(ctx.channel()) + "\n\n\n");
		int uid = -1;
		if (authorizedConnections.containsKey(ctx.channel())) {
			uid = authorizedConnections.get(ctx.channel());
			authorizedConnections.remove(ctx.channel());
			doOfflineCheck(ctx, uid);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
		if (msg instanceof TextWebSocketFrame) {
			String message = ((TextWebSocketFrame) msg).text();
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = null;
			try {
				rootNode = objectMapper.readTree(message);
			}
			catch(Exception e) {
				logger.error("Error in parsing json message : "+e);
			}
			String operation = rootNode.get("operation").asText();
			if (operation.equals("Authentication")) {
				String username = rootNode.get("username").asText();
				String password = rootNode.get("password").asText();
				User user = wsServer.getUserDao().getUserByName(username);
				authenticateConnection(ctx, user, username, password);
			}
			if (!authorizedConnections.containsKey(ctx.channel())) {
				logger.info("Unknown connection...");
				ctx.close();
			} else {
				if (operation.equals("RefreshChats")) {
					if (rootNode.has("to")) {
						JsonNode toArray = rootNode.get("to");
						sendToMyself(toArray, message);
					}
					if (rootNode.has("type") && rootNode.get("type").asText().equals("LogOut")) {
						int uid = authorizedConnections.get(ctx.channel());
						authorizedConnections.remove(ctx.channel());
						doOfflineCheck(ctx, uid);
						logger.info("Logged Out : " + uid + "\n\n\n");
					}
				} else if (operation.equals("sendMessage")) {
					String chatType = rootNode.get("chatType").asText();
					int recieverId = rootNode.get("recieverId").asInt();
					int senderId = rootNode.get("senderId").asInt();
					sendMessages(ctx, chatType, recieverId, senderId, message);
				}
			}
		} else {
			logger.error("Format not supported...");
		}
	}
	
	private void sendMessages(ChannelHandlerContext ctx, String chatType, int recieverId, int senderId, String message) {
		// TODO Auto-generated method stub
		if(clientToConnections.get(senderId).size() > 1) {
			for(Channel channel : clientToConnections.get(senderId)) {
				if(ctx.channel() != channel) {
					channel.writeAndFlush(new TextWebSocketFrame(message));
				}
			}
		}
		if (chatType.equals("User")) {
			if (clientToConnections.containsKey(recieverId)) {
				logger.info("Sending message to : " + recieverId);
				for(Channel channel : clientToConnections.get(recieverId)) channel.writeAndFlush(new TextWebSocketFrame(message));
			}
		} else {
			Set<User> members = wsServer.getGroupDao().getGroupById(recieverId).getMembers();
			for (User member : members) {
				if (member.getUid() != senderId && clientToConnections.containsKey(member.getUid())) {
					logger.info("Sending message to : " + recieverId);
					for(Channel channel : clientToConnections.get(member.getUid())) channel.writeAndFlush(new TextWebSocketFrame(message));
				}
			}
		}
	}

	private void sendToMyself(JsonNode toArray, String message) {
		// TODO Auto-generated method stub
		if (toArray != null && toArray.isArray()) {
			for (JsonNode uid : toArray) {
				if (clientToConnections.containsKey(uid.asInt())) {
					for(Channel channel : clientToConnections.get(uid.asInt())) channel.writeAndFlush(new TextWebSocketFrame(message));
				}
			}
		}
	}

	private void authenticateConnection(ChannelHandlerContext ctx, User user, String username, String password) throws Exception {
		// TODO Auto-generated method stub
		if (user == null || (!user.getPassword().equals(password))) {
			ctx.close();
			throw new Exception("Authentication Failed...");
		} else {
			authorizedConnections.put(ctx.channel(), user.getUid());
			Set<Channel> hs = clientToConnections.getOrDefault(user.getUid(), new HashSet<>());
			hs.add(ctx.channel());
			clientToConnections.put(user.getUid(), hs);
			String json = "{\"operation\":\"RefreshChats\"}";
			for (User friend : user.getFriends()) {
				if (clientToConnections.containsKey(friend.getUid())) {
					logger.info("Sending refresh alert to : " + friend.getUid());
					for(Channel channel : clientToConnections.get(friend.getUid())) {
						channel.writeAndFlush(new TextWebSocketFrame(json));
					}
				}
			}
			logger.info(username + " validated successfully...");
		}
	}

	public void doOfflineCheck(ChannelHandlerContext ctx, int uid) {
		clientToConnections.get(uid).remove(ctx.channel());
		if(clientToConnections.get(uid).size() == 0) {
			clientToConnections.remove(uid);
			User user = wsServer.getUserDao().getUserById(uid);
			if (user != null) {
				wsServer.getUserDao().updateStatusOffline(user.getUsername());
				String json = "{\"operation\":\"RefreshChats\"}";
				for (User friend : user.getFriends()) {
					if (clientToConnections.containsKey(friend.getUid())) {
						logger.info("Sending refresh alert to : " + friend.getUid());
						for(Channel channel : clientToConnections.get(friend.getUid())) channel.writeAndFlush(new TextWebSocketFrame(json));
					}
				}
			}
		}
	}
}
