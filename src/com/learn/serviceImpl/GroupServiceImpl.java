package com.learn.serviceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.learn.daoImpl.GroupDaoImpl;
import com.learn.daoImpl.UserDaoImpl;
import com.learn.model.Group;
import com.learn.model.User;
import com.learn.service.GroupService;

public class GroupServiceImpl extends RemoteServiceServlet implements GroupService {
	
	private static GroupDaoImpl groupDao;
	private static final Logger logger = Logger.getLogger(GroupServiceImpl.class);

	public GroupDaoImpl getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(GroupDaoImpl groupDao) {
		GroupServiceImpl.groupDao = groupDao;
	}

	@Override
	public Group createGroup(String groupName, int creatorId) {
		// TODO Auto-generated method stub
		Group group = groupDao.createGroup(groupName, creatorId);
		group.detach(new HashSet<Object>());
		logger.info("detached group in create group method");
		return group;
	}

	@Override
	public void addMembers(int gid, List<Integer> members) {
		// TODO Auto-generated method stub
		groupDao.addMembers(gid, members);
		logger.info("added members in groupserviceimpl");
	}

	@Override
	public void removeMember(int gid, int uid) {
		// TODO Auto-generated method stub
		groupDao.removeMember(gid, uid);
		logger.info("removed member in groupserviceimpl");
	}

	@Override
	public Group getGroupById(int gid) {
		// TODO Auto-generated method stub
		Group group = groupDao.getGroupById(gid);
		if(group != null) group.detach(new HashSet<Object>());
		logger.info("detached group in get group by id method");
		return group;
	}

	@Override
	public void removeGroup(int gid) {
		// TODO Auto-generated method stub
		groupDao.removeGroup(gid);
		logger.info("removed group in groupserviceimpl");
	}

}
