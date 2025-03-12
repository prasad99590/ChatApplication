package com.learn.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.learn.model.Group;
import com.learn.model.Message;
import com.learn.model.MessageDelivered;
import com.learn.model.User;
import com.learn.service.GroupService;
import com.learn.service.GroupServiceAsync;
import com.learn.service.MessageService;
import com.learn.service.MessageServiceAsync;

public class CustomWidgets {
	private GroupServiceAsync groupService = GWT.create(GroupService.class);
	private MessageServiceAsync messageService = GWT.create(MessageService.class);
	private DialogBox seenByDialogueBox;
	private DialogBox createGroupDialogueBox;
	private DialogBox addMembersDialogBox;
	private ChatPageBrowser cpb;

	public CustomWidgets() {
	}

	public CustomWidgets(ChatPageBrowser cpb) {
		this.cpb = cpb;
	}

	public void loadMessages(int senderId, int recieverId, String chatType, FlowPanel rightMiddle, User currUser) {
		// TODO Auto-generated method stub
		if (chatType.equals("User")) {
			messageService.getMessagesInUser(senderId, recieverId, new AsyncCallback<List<Message>>() {

				@Override
				public void onSuccess(List<Message> result) {
					// TODO Auto-generated method stub
					for (Message m : result) {
						rightMiddle.add(createMessageTemplate(m,
								m.getSender().getUid() == currUser.getUid() ? "right" : "left"));
					}
					cpb.scrollToBottom(rightMiddle.getElement());
				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					Window.alert("Error in loading messages for user...");
				}
			});
		} else {
			messageService.getMessagesInGroup(recieverId, new AsyncCallback<List<Message>>() {

				@Override
				public void onSuccess(List<Message> result) {
					// TODO Auto-generated method stub
					for (Message m : result) {
						rightMiddle.add(createMessageTemplate(m,
								m.getSender().getUid() == currUser.getUid() ? "right" : "left"));
					}
					cpb.scrollToBottom(rightMiddle.getElement());
				}

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					Window.alert("Error in loading group messages...");
				}
			});
		}
	}

	public void updateSeenAt(int seenById, int chatId, String chatType) {
		// TODO Auto-generated method stub
//		Window.alert("Called update seen at...");
		messageService.updateSeenAt(seenById, chatId, chatType, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub
				// Window.alert("Updated seen at successfully...");
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert("Error in updating seen at...");
			}
		});
	}

	public VerticalPanel createMessageTemplate(Message message, String direction) {
		VerticalPanel messageTemplate = new VerticalPanel();
		FlowPanel firstRow = new FlowPanel();
		firstRow.getElement().setClassName("message-top");
		firstRow.add(new Label(direction.equals("right") ? "You" : message.getSender().getUsername()));
		Image info = new Image("icons/information.png");
		info.getElement().setClassName("info");
		info.getElement().setAttribute("id", "" + message.getMid());
		info.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				int id = Integer.parseInt(info.getElement().getAttribute("id"));
				seenByDialogueBox(id);
			}
		});
		if (direction.equals("right")) {
			firstRow.add(info);
		}
		messageTemplate.add(firstRow);
		messageTemplate.add(new Label(message.getContent()));
		FlowPanel fPanel = new FlowPanel();
		String arr[] = message.getCreatedAt().toString().split(" ");
		String month = "";
		String day = "";
		String year = "";
		String time = "";
		if (arr[0].charAt(0) >= 'A' && arr[0].charAt(0) <= 'Z') { // Mon Feb 3 11:00:42 GMT 2025
			day = arr[2];
			month = "" + getMonth(arr[1]);
			year = arr[arr.length - 1];
			time = arr[3];
		} else { // 2025-02-10 10:30:19.154
			String arr2[] = arr[0].split("-");
			day = arr2[2];
			month = arr2[1];
			year = arr2[0];
			time = arr[1].substring(0, arr[1].lastIndexOf('.'));
		}
		fPanel.getElement().setClassName("timeBox");
		fPanel.add(new Label(day + "-" + month + "-" + year));
		fPanel.add(new Label(time));
		messageTemplate.add(fPanel);
		if (direction.equals("left")) {
			messageTemplate.getElement().setClassName("senderMessage");
		} else {
			messageTemplate.getElement().setClassName("myMessage");
		}
		return messageTemplate;
	}

	private int getMonth(String s) {
		List<String> al = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May",
				"Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
				"Dec");
		return al.indexOf(s) + 1;
	}

	private void seenByDialogueBox(int id) {
		// TODO Auto-generated method stub
		seenByDialogueBox = new DialogBox();
		VerticalPanel vp = new VerticalPanel();
		vp.getElement().setClassName("dialogue-box");
		vp.add(new Label("Seen By"));
		Button close = new Button("Close");
		close.getElement().setClassName("closeBtn");
		close.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				seenByDialogueBox.hide();
			}
		});
		messageService.getSeenMembers(id, new AsyncCallback<List<MessageDelivered>>() {

			@Override
			public void onSuccess(List<MessageDelivered> result) {
				// TODO Auto-generated method stub
				for (MessageDelivered md : result) {
					if (md.getSeenAt() != null) {
						FlowPanel fp = new FlowPanel();
						fp.getElement().setClassName("flex-2");
						fp.add(new Label(md.getSeenBy().getUsername()));
						String date = md.getSeenAt().toString();
						fp.add(new Label(date.substring(0, date.lastIndexOf('.'))));
						vp.add(fp);
					}
				}
				vp.add(close);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert("Error in loading seen by members...");
			}
		});
		seenByDialogueBox.setWidget(vp);
		seenByDialogueBox.center();
		seenByDialogueBox.show();
	}

	public void createGroupDialogueBox() {
		createGroupDialogueBox = new DialogBox();
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.add(new Label("Create Group"));
		vPanel.getElement().setClassName("dialogue-box");
		TextBox groupName = new TextBox();
		groupName.getElement().setAttribute("placeholder", "Enter Group Name");
		groupName.getElement().setClassName("search-bar");
		vPanel.add(groupName);
		FlowPanel fp = new FlowPanel();
		fp.getElement().setClassName("flex-2");
		Button close = new Button("Close");
		close.getElement().setClassName("closeBtn");
		close.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				createGroupDialogueBox.hide();
			}
		});
		Button create = new Button("Add Members");
		create.getElement().setClassName("addMembers");
		fp.add(close);
		fp.add(create);
		create.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if (groupName.getText().length() > 0) {
					createGroupDialogueBox.hide();
					groupService.createGroup(groupName.getText(), cpb.currUser.getUid(), new AsyncCallback<Group>() {

						@Override
						public void onSuccess(Group result) {
							// TODO Auto-generated method stub
							addMembersDialogueBox(result);
						}

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
							Window.alert("Group may already exists...");
						}
					});
				} else {
					Window.alert("Group name should not be empty...");
				}
			}
		});
		vPanel.add(fp);
		createGroupDialogueBox.setWidget(vPanel);
		createGroupDialogueBox.center();
		createGroupDialogueBox.show();
	}

	public void addMembersDialogueBox(Group group) {
		// TODO Auto-generated method stub
		addMembersDialogBox = new DialogBox();
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.getElement().setClassName("dialogue-box");
		TextBox search = new TextBox();
		search.getElement().setAttribute("placeholder", "Search here...");
		search.getElement().setClassName("search-bar");
		VerticalPanel list = new VerticalPanel();
		List<Integer> members = new ArrayList<>();
		vPanel.add(search);
		vPanel.add(list);
		search.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				String s = search.getValue();
				list.clear();
				for (User friend : cpb.currUser.getFriends()) {
					if (friend.getUsername().contains(s) && (!(isAlreadyInList(friend, members)))
							&& (!(isInGroup(friend, group)))) {
						FlowPanel fp = new FlowPanel();
						fp.getElement().setClassName("flex-between");
						fp.add(new Label(friend.getUsername()));
						Image addIcon = new Image("icons/plus.png");
						addIcon.getElement().setClassName("add-icon");
						addIcon.addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {
								// TODO Auto-generated method stub
								members.add(friend.getUid());
								list.remove(fp);
							}
						});
						fp.add(addIcon);
						list.add(fp);
					}
				}
			}
		});

		Button ok = new Button("Ok");
		ok.getElement().setClassName("closeBtn");
		ok.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				groupService.addMembers(group.getGid(), members, new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						// TODO Auto-generated method stub
						cpb.getMyDetails(false);
						cpb.getGroupDetails();
						addMembersDialogBox.hide();
						Set<Integer> to = new HashSet<>(members);
						sendRefreshMessage(to, false);
					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert("Error in creating group...");
					}
				});
			}
		});
		vPanel.add(ok);
		addMembersDialogBox.setWidget(vPanel);
		addMembersDialogBox.center();
		addMembersDialogBox.show();
	}

	public void sendRefreshMessage(Set<Integer> to, boolean defaultClick) {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		jsonObject.put("operation", new JSONString("RefreshChats"));
		int index = 0;
		for (int uid : to) {
			jsonArray.set(index, new JSONNumber(uid));
			index++;
		}
		jsonObject.put("to", jsonArray);
		jsonObject.put("defaultClick", new JSONString(defaultClick ? "true" : "false"));

		String jsonData = jsonObject.toString();
		sendMessageGWT(jsonData);
	}

	public boolean isInGroup(User friend, Group group) {
		// TODO Auto-generated method stub
		for (User member : group.getMembers()) {
			if (member.getUid() == friend.getUid()) {
				return true;
			}
		}
		return false;
	}

	public boolean isAlreadyInList(User friend, List<Integer> members) {
		// TODO Auto-generated method stub
		for (int id : members) {
			if (friend.getUid() == id) {
				return true;
			}
		}
		return false;
	}

	public boolean isFriend(User a, User b) {
		for (User friend : b.getFriends()) {
			if (a.getUid() == friend.getUid()) {
				return true;
			}
		}
		return false;
	}

	public void doMessageHandlingForOther(String type, Message result, FlowPanel rightMiddle) {
		// TODO Auto-generated method stub
		if (type.equals("User")) {
			if (cpb.selectedChatType.equals("User") && result.getSender().getUid() == cpb.selectedChatId) {
				rightMiddle.add(createMessageTemplate(result, "left"));
				updateSeenAt(cpb.currUser.getUid(), cpb.selectedChatId, "User");
				cpb.scrollToBottom(rightMiddle.getElement());
			} else {
				int fid = result.getSender().getUid();
				FlowPanel chat = null;
				if (result.getRecieverUser() == null) {
					chat = cpb.groupChats.get(result.getRecieverGroup().getGid());
				} else {
					chat = cpb.userChats.get(fid);
				}
				if (chat.getWidgetCount() == 3) {
					chat.remove(2);
				}
				int count = Integer.parseInt(chat.getElement().getAttribute("unreadCount"));
				// Window.alert(count+"");
				chat.getElement().setAttribute("unreadCount", "" + (count + 1));
				Label lb = new Label("" + (count + 1));
				lb.getElement().setClassName("display-unread");
				chat.add(lb);
			}
		} else {
			if (cpb.selectedChatType.equals("Group")
					&& result.getRecieverGroup().getGid() == cpb.selectedChatId) {
				rightMiddle.add(createMessageTemplate(result, "left"));
				updateSeenAt(cpb.currUser.getUid(), cpb.selectedChatId, "Group");
				cpb.scrollToBottom(rightMiddle.getElement());
			} else {
				int fid = result.getSender().getUid();
				FlowPanel chat = null;
				if (result.getRecieverUser() == null) {
					chat = cpb.groupChats.get(result.getRecieverGroup().getGid());
				} else {
					chat = cpb.userChats.get(fid);
				}
				if (chat.getWidgetCount() == 3) {
					chat.remove(2);
				}
				int count = Integer.parseInt(chat.getElement().getAttribute("unreadCount"));
				// Window.alert(count+"");
				chat.getElement().setAttribute("unreadCount", "" + (count + 1));
				Label lb = new Label("" + (count + 1));
				lb.getElement().setClassName("display-unread");
				chat.add(lb);
			}
		}
	}

	public void doMessageHandlingForMyself(String type, Message result, FlowPanel rightMiddle) {
		// TODO Auto-generated method stub
		if (type.equals("User")) {
			if (result.getRecieverUser().getUid() == cpb.selectedChatId) {
				rightMiddle.add(createMessageTemplate(result, "right"));
			}
		} else {
			if (result.getRecieverGroup().getGid() == cpb.selectedChatId) {
				rightMiddle.add(createMessageTemplate(result, "right"));
			}
		}
		cpb.scrollToBottom(rightMiddle.getElement());
	}

	public native void sendMessageGWT(String jsonData) /*-{
		// TODO Auto-generated method stub
		$wnd.sendMessage(jsonData);
	}-*/;
}
