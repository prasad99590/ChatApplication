package com.learn.websocket;

import org.apache.log4j.Logger;

import com.learn.daoImpl.GroupDaoImpl;
import com.learn.daoImpl.UserDaoImpl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.ChannelOption;

public class WebSocketServer {
	private int port;
	private GroupDaoImpl groupDao;
	private UserDaoImpl userDao;
	
	private static final Logger logger = Logger.getLogger(WebSocketServer.class);

	public WebSocketServer() {

	}

	public GroupDaoImpl getGroupDao() {
		return this.groupDao;
	}

	public void setGroupDao(GroupDaoImpl groupDao) {
		this.groupDao = groupDao;
	}

	public UserDaoImpl getUserDao() {
		return this.userDao;
	}

	public void setUserDao(UserDaoImpl userDao) {
		this.userDao = userDao;
	}

	public void init() throws InterruptedException {
		this.port = 8080;
		Runnable runnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				EventLoopGroup bossGroup = new NioEventLoopGroup();
				EventLoopGroup workerGroup = new NioEventLoopGroup();

				try {
					ServerBootstrap b = new ServerBootstrap();
					b.group(bossGroup, workerGroup)
							.channel(NioServerSocketChannel.class)
							.option(ChannelOption.SO_BACKLOG, 128)
							.childOption(ChannelOption.SO_KEEPALIVE, true)
							.childHandler(new WebSocketServerInitializer());

					Channel ch = b.bind(port).sync().channel();

					logger.info("WebSocket server started at ws://localhost:" + port);

					ch.closeFuture().sync();
				} 
				catch(Exception e) {
					logger.info("Error in initializing the server : "+e);
				}
				finally {
					workerGroup.shutdownGracefully();
					bossGroup.shutdownGracefully();
				}
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

}
