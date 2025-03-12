package com.learn.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
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
import com.learn.service.UserService;
import com.learn.service.UserServiceAsync;

public class ChatPageBrowser {
	public User currUser;
	private HorizontalPanel mainPanel;
	private VerticalPanel leftPanel;
	private VerticalPanel rightPanel;
	private VerticalPanel leftHeader;
	private FlowPanel leftMiddle;
	private VerticalPanel leftFooter;
	private FlowPanel leftHeaderPanel1;
	private Label leftHeading;
	private Image createGroup;
	private TextBox leftHeaderSearch;

	private FlowPanel rightHeader;
	private FlowPanel rightMiddle;
	private FlowPanel rightFooter;
	private FlowPanel rightHeaderPanel1;
	private Label selectedChatName;
	private Button viewMembers;
	private TextBox messageBox;
	private Button sendMessage;

	public int selectedChatId;
	public String selectedChatType;
	public Group selectedGroup;
	public FlowPanel selectedFlowPanel;
	public int selectedMessageId;

	private DialogBox viewMemberDialogueBox;
	private DialogBox createGroupDialogueBox;
	private DialogBox addMembersDialogBox;

	private UserServiceAsync userService;
	private GroupServiceAsync groupService;
	private MessageServiceAsync messageService;

	public Map<Integer, FlowPanel> groupChats;
	public Map<Integer, FlowPanel> userChats;
	private CustomWidgets cw = new CustomWidgets(this);

	public ChatPageBrowser(User user) {
		userService = GWT.create(UserService.class);
		groupService = GWT.create(GroupService.class);
		messageService = GWT.create(MessageService.class);
		currUser = user;
		selectedMessageId = -1;
		mainPanel = new HorizontalPanel();
		leftPanel = new VerticalPanel();
		rightPanel = new VerticalPanel();
		mainPanel.getElement().setClassName("mainPanel");
		leftPanel.getElement().setClassName("leftPanel");
		rightPanel.getElement().setClassName("rightPanel");
		mainPanel.add(leftPanel);
		mainPanel.add(rightPanel);
		RootPanel.get().add(mainPanel);

		leftHeader = new VerticalPanel();
		rightHeader = new FlowPanel();
		leftHeader.getElement().setClassName("leftHeader");
		rightHeader.getElement().setClassName("rightHeader");
		leftPanel.add(leftHeader);
		rightPanel.add(rightHeader);

		leftHeaderPanel1 = new FlowPanel();
		leftHeaderPanel1.getElement().setClassName("flex-between");
		leftHeading = new Label("Hello, " + currUser.getUsername());
		createGroup = new Image("icons/group.png");
		leftHeaderSearch = new TextBox();
		leftHeaderSearch.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				// TODO Auto-generated method stub
				if (leftHeaderSearch.getValue().length() != 0) {
//					Window.alert("Searching for : " + event.getValue());
					userService.getUsersForSearch(leftHeaderSearch.getValue(), new AsyncCallback<List<User>>() {

						@Override
						public void onSuccess(List<User> result) {
							// TODO Auto-generated method stub
							leftMiddle.clear();
							List<User> others = new ArrayList<>();
							for (User user : result) {
								if (cw.isFriend(user, currUser))
									leftMiddle.add(createUserChat(user));
								else if (user.getUid() != currUser.getUid())
									others.add(user);
							}
							Label lb = new Label("Other Results");
							lb.getElement().setClassName("other-results");
							leftMiddle.add(lb);
							for (User user : others) {
								leftMiddle.add(createUserChatForOther(user));
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
							Window.alert("Error in fetching results for the name...");
						}
					});
				} else
					loadChats(true);
			}
		});
		leftHeaderSearch.getElement().setAttribute("placeholder", "Search here...");
		leftHeaderSearch.getElement().setClassName("search-bar");
		leftHeading.getElement().setClassName("heading");
		createGroup.getElement().setClassName("icon");
		createGroup.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				createGroupDialogueBox();
			}
		});

		leftHeaderPanel1.add(leftHeading);
		leftHeaderPanel1.add(createGroup);
		leftHeader.add(leftHeaderPanel1);
		leftHeader.add(leftHeaderSearch);

		leftMiddle = new FlowPanel();
		leftFooter = new VerticalPanel();
		leftMiddle.getElement().setClassName("leftMiddle");
		leftFooter.getElement().setClassName("leftFooter");
		leftPanel.add(leftMiddle);
		leftPanel.add(leftFooter);

		Button logOut = new Button("Log Out");
		logOut.getElement().setClassName("logOut");
		logOut.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				updateStatusOffline();
			}
		});
		leftFooter.add(logOut);

		rightMiddle = new FlowPanel();
		rightFooter = new FlowPanel();
		rightMiddle.getElement().setClassName("rightMiddle");
		rightFooter.getElement().setClassName("rightFooter");
		rightPanel.add(rightMiddle);
		rightPanel.add(rightFooter);
		rightHeaderPanel1 = new FlowPanel();
		rightHeaderPanel1.getElement().setClassName("flex-between");
		rightHeader.add(rightHeaderPanel1);
		selectedChatName = new Label("Welcome to Chat App");
		selectedChatName.getElement().setClassName("heading");
		viewMembers = new Button("View Members");
		viewMembers.getElement().setClassName("viewMembers");
		viewMembers.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				viewMemberDialogueBox();
			}
		});
		rightHeaderPanel1.add(selectedChatName);
		rightHeaderPanel1.add(viewMembers);

		messageBox = new TextBox();
		messageBox.getElement().setAttribute("placeholder", "Type your message here...");
		messageBox.getElement().setClassName("messageBox");
		sendMessage = new Button("Send");
		sendMessage.getElement().setClassName("sendMessage");
		rightFooter.add(messageBox);
		rightFooter.add(sendMessage);

		messageBox.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					if (messageBox.getText().length() > 0) {
						doMessageHandling();
						messageBox.setText("");
					} else {
						Window.alert("Message should not be empty...");
					}
				}
			}
		});
		sendMessage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if (messageBox.getText().length() > 0) {
					doMessageHandling();
					messageBox.setText("");
				} else {
					Window.alert("Message should not be empty...");
				}
			}
		});

		createCbForLoadChatsGWT();
		createCbForAcceptMessageGWT();

		updateStatusOnline();
		loadChats(true);
	}

	private void loadChats(boolean defaultClick) {
		leftMiddle.clear();
		userChats = new HashMap<>();
		groupChats = new HashMap<>();
		List<User> friends = new ArrayList<>(currUser.getFriends());
		Collections.sort(friends, new Comparator<User>() {
			public int compare(User a, User b) {
				int r = a.getStatus().compareTo(b.getStatus());
				if (r != 0)
					return -1 * r;
				return a.getUsername().compareTo(b.getUsername());
			}
		});
		for (User friend : friends) {
			FlowPanel fp = createUserChat(friend);
			userChats.put(friend.getUid(), fp);
			leftMiddle.add(fp);
		}
		// Window.alert("No of groups : " + currUser.getGroups().size());
		for (Group group : currUser.getGroups()) {
			FlowPanel fp = createUserChat(group);
			groupChats.put(group.getGid(), fp);
			leftMiddle.add(fp);
		}
		if (defaultClick) {
			clickElement(leftMiddle.getWidget(leftMiddle.getWidgetCount() - 1).getElement());
		}
	}

	private void updateStatusOnline() {
		userService.updateStatusOnline(currUser.getUsername(), new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub
				connectToWS("ws://localhost:8080/ws");
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert("Error in updating online status of user...");
			}
		});
	}

	private void updateStatusOffline() {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		jsonObject.put("operation", new JSONString("RefreshChats"));
		Set<Integer> to = new HashSet<>();
		for (User friend : currUser.getFriends()) {
			to.add(friend.getUid());
		}
		int index = 0;
		for (int uid : to) {
			jsonArray.set(index, new JSONNumber(uid));
			index++;
		}
		jsonObject.put("to", jsonArray);
		jsonObject.put("type", new JSONString("LogOut"));
		String jsonData = jsonObject.toString();
		cw.sendMessageGWT(jsonData);
		new LoginPage().loadPage();
	}

	private void doMessageHandling() {
		messageService.saveMyMessage(currUser.getUid(), selectedChatId, selectedChatType, messageBox.getText(),
				new AsyncCallback<Message>() {

					@Override
					public void onSuccess(Message result) {
						// TODO Auto-generated method stub
						rightMiddle.add(cw.createMessageTemplate(result, "right"));
						JSONObject jsonObject = new JSONObject();

						jsonObject.put("operation", new JSONString("sendMessage"));
						jsonObject.put("mid", new JSONNumber(result.getMid()));
						jsonObject.put("message", new JSONString(messageBox.getText()));
						jsonObject.put("senderId", new JSONNumber(currUser.getUid()));
						jsonObject.put("recieverId", new JSONNumber(selectedChatId));
						jsonObject.put("chatType", new JSONString(selectedChatType));
						jsonObject.put("createdAt", new JSONString(new Date().toString()));

						String jsonData = jsonObject.toString();
						cw.sendMessageGWT(jsonData);
						scrollToBottom(rightMiddle.getElement());
					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert("Error in saving message...");
					}
				});
	}

	public void getMyDetails(boolean defaultClick) {
		userService.getUserById(currUser.getUid(), new AsyncCallback<User>() {

			@Override
			public void onSuccess(User result) {
				// TODO Auto-generated method stub
				currUser = result;

				loadChats(defaultClick);
				// Window.alert("Loaded successfully...");
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert("Error in loading user...");
			}
		});
	}

	public FlowPanel createUserChat(Object chat) {
		FlowPanel chatTemplate = new FlowPanel();
		chatTemplate.getElement().addClassName("flex-between");
		chatTemplate.getElement().addClassName("chatTemplate");
		String colorClass = "";
		Label chatname = new Label();
		Label stat = new Label();
		int tempId = -1;
		if (chat instanceof User) {
			tempId = ((User) chat).getUid();
			colorClass = ((User) chat).getStatus().equals("online") ? "green" : "red";
			chatname.setText(((User) chat).getUsername());
			stat.setText("O\t " + ((User) chat).getStatus());
			chatTemplate.getElement().setAttribute("id", "" + ((User) chat).getUid());
			chatTemplate.getElement().setAttribute("type", "User");
		} else {
			tempId = ((Group) chat).getGid();
			chatname.setText(((Group) chat).getGname());
			chatTemplate.getElement().setAttribute("id", "" + ((Group) chat).getGid());
			chatTemplate.getElement().setAttribute("type", "Group");
		}
		messageService.getUnreadCount(currUser.getUid(), tempId, chat instanceof User ? "User" : "Group",
				new AsyncCallback<Integer>() {

					@Override
					public void onSuccess(Integer result) {
						// TODO Auto-generated method stub
						int unreadCount = result;
						Label unread = new Label();
						chatTemplate.getElement().setAttribute("unreadCount", "" + unreadCount);
						if (unreadCount != 0) {
							unread.setText("" + unreadCount);
							unread.getElement().setClassName("display-unread");
							chatTemplate.add(unread);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert("Error in getting unread count...");
					}
				});
		stat.getElement().setClassName(colorClass);
		chatTemplate.add(chatname);
		chatTemplate.add(stat);
		chatTemplate.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				chatTemplate.getElement().setAttribute("unreadCount", "0");
				if (chatTemplate.getWidgetCount() >= 3) {
					chatTemplate.remove(2);
				}
				if (selectedFlowPanel != null) {
					selectedFlowPanel.getElement().removeClassName("selectedChat");
				}
				selectedFlowPanel = chatTemplate;
				chatTemplate.getElement().addClassName("selectedChat");
				selectedChatId = Integer.parseInt(chatTemplate.getElement().getAttribute("id"));
				selectedChatType = chatTemplate.getElement().getAttribute("type");
				selectedChatName.setText(chatname.getText());
				if (selectedChatType.equals("User")) {
					viewMembers.getElement().addClassName("display-none");
				} else {
					viewMembers.getElement().removeClassName("display-none");
					for (Group group : currUser.getGroups()) {
						if (group.getGid() == selectedChatId) {
							selectedGroup = group;
							break;
						}
					}
				}
				rightMiddle.clear();
				cw.loadMessages(currUser.getUid(), selectedChatId, selectedChatType, rightMiddle, currUser);
				cw.updateSeenAt(currUser.getUid(), selectedChatId, selectedChatType);
			}
		}, ClickEvent.getType());
		return chatTemplate;
	}

	private FlowPanel createUserChatForOther(User user) {
		FlowPanel chatTemplate = new FlowPanel();
		chatTemplate.getElement().setClassName("flex-between");
		chatTemplate.getElement().addClassName("chatTemplate");
		chatTemplate.add(new Label(user.getUsername()));
		Image addIcon = new Image("icons/plus.png");
		addIcon.getElement().setClassName("add-icon");
		addIcon.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				userService.addFriend(currUser.getUid(), user.getUid(), new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						// TODO Auto-generated method stub
						// Window.alert("Added friend successfully");
						getMyDetails(true);
						Set<Integer> to = new HashSet<>();
						to.add(user.getUid());
						cw.sendRefreshMessage(to, false);
					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert("Error in adding friend...");
					}
				});
			}
		});
		chatTemplate.add(addIcon);
		return chatTemplate;
	}

	private void viewMemberDialogueBox() {
		viewMemberDialogueBox = new DialogBox();
		viewMemberDialogueBox.setAnimationEnabled(true);
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.getElement().setClassName("dialogue-box");
		FlowPanel fp = new FlowPanel();
		fp.getElement().setClassName("flex-between");
		fp.add(new Label(selectedGroup.getGname()));
		boolean isAdmin = (currUser.getUid() == selectedGroup.getCreatedBy().getUid());
		Button exitGroup = new Button(isAdmin ? "Delete Group" : "Exit Group");
		exitGroup.getElement().setClassName("exitGroup");
		exitGroup.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if (isAdmin) { // remove members then group
					Set<Integer> to = new HashSet<>();
					for (User member : selectedGroup.getMembers()) {
						to.add(member.getUid());
						groupService.removeMember(selectedChatId, member.getUid(), new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								// TODO Auto-generated method stub
								// Window.alert("Removed : " + member.getUsername());
							}

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								Window.alert("Error in exiting group...");
							}
						});
					}
					groupService.removeGroup(selectedChatId, new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							// TODO Auto-generated method stub
							viewMemberDialogueBox.hide();
							getMyDetails(true);
							cw.sendRefreshMessage(to, true);
						}

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
							Window.alert("Error in deleting group...");
						}
					});
				} else {
					groupService.removeMember(selectedChatId, currUser.getUid(), new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							// TODO Auto-generated method stub
							viewMemberDialogueBox.hide();
							Set<Integer> to = new HashSet<>();
							for (User friend : currUser.getFriends()) {
								to.add(friend.getUid());
							}
							cw.sendRefreshMessage(to, false);
							getMyDetails(true);
						}

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
							Window.alert("Error in exiting group...");
						}
					});
				}
			}
		});
		fp.add(exitGroup);
		vPanel.add(fp);
		for (User member : selectedGroup.getMembers()) {
			FlowPanel fPanel = new FlowPanel();
			fPanel.getElement().setClassName("flex-between");
			fPanel.getElement().addClassName("userTemplate");
			fPanel.add(new Label(member.getUsername()));
			Button remove = new Button("Remove");
			remove.getElement().setClassName("logOut");
			remove.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					// TODO Auto-generated method stub
					groupService.removeMember(selectedChatId, member.getUid(), new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							// TODO Auto-generated method stub
							// Window.alert("Member removed successfully...");
							getGroupDetails();
							vPanel.remove(fPanel);
							Set<Integer> to = new HashSet<>();
							to.add(member.getUid());
							cw.sendRefreshMessage(to, true);
						}

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
							Window.alert("Error in removing member...");
						}
					});
				}
			});
			if (selectedGroup.getCreatedBy().getUid() == currUser.getUid())
				fPanel.add(remove);
			if (member.getUid() != currUser.getUid())
				vPanel.add(fPanel);
		}
		FlowPanel fp1 = new FlowPanel();
		fp1.getElement().setClassName("flex-between");
		fp1.getElement().addClassName("userTemplate");
		fp1.add(new Label("Admin : " + selectedGroup.getCreatedBy().getUsername()));
		vPanel.add(fp1);
		FlowPanel tempPanel = new FlowPanel();
		tempPanel.getElement().setClassName("flex-2");
		Button close = new Button("Close");
		close.getElement().setClassName("closeBtn");
		close.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				viewMemberDialogueBox.hide();
			}
		});
		tempPanel.add(close);
		Button addMembers = new Button("Add");
		addMembers.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				viewMemberDialogueBox.hide();
				addMembersDialogueBox(selectedGroup);
			}
		});
		addMembers.getElement().setClassName("addMember");
		if (currUser.getUid() == selectedGroup.getCreatedBy().getUid()) {
			tempPanel.add(addMembers);
		}
		vPanel.add(tempPanel);
		viewMemberDialogueBox.setWidget(vPanel);
		viewMemberDialogueBox.center();
		viewMemberDialogueBox.show();
	}

	private void createGroupDialogueBox() {
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
					groupService.createGroup(groupName.getText(), currUser.getUid(), new AsyncCallback<Group>() {

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

	private void addMembersDialogueBox(Group group) {
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
				for (User friend : currUser.getFriends()) {
					if (friend.getUsername().contains(s) && (!(cw.isAlreadyInList(friend, members)))
							&& (!(cw.isInGroup(friend, group)))) {
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
						getMyDetails(false);
						getGroupDetails();
						addMembersDialogBox.hide();
						Set<Integer> to = new HashSet<>(members);
						cw.sendRefreshMessage(to, false);
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

	public void getGroupDetails() {
		groupService.getGroupById(selectedChatId, new AsyncCallback<Group>() {

			@Override
			public void onSuccess(Group result) {
				// TODO Auto-generated method stub
				selectedGroup = result;
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert("Error in fetching group details...");
			}
		});
	}

	private void acceptMessage(String message) {
//		Window.alert(userChats.size()+" "+groupChats.size());
		JSONValue value = JSONParser.parseStrict(message);
		if (value != null && value.isObject() != null) {
			JSONObject jsonObject = value.isObject();

			String operation = jsonObject.get("operation").isString().stringValue();
			if (operation.equals("RefreshChats")) {
				boolean defaultClick = false;
				if (jsonObject.containsKey("defaultClick"))
					jsonObject.get("defaultClick").isString().stringValue().equals("true");
				getMyDetails(defaultClick);
			} else if (operation.equals("sendMessage")) {
				String type = jsonObject.get("chatType").isString().stringValue();
				int mid = (int) jsonObject.get("mid").isNumber().doubleValue();
				messageService.getMessageById(mid, new AsyncCallback<Message>() {

					@Override
					public void onSuccess(Message result) {
						// TODO Auto-generated method stub
						if (result.getSender().getUid() == currUser.getUid()) {
							cw.doMessageHandlingForMyself(type, result, rightMiddle);
						} else {
							cw.doMessageHandlingForOther(type, result, rightMiddle);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						Window.alert("Error in accepting message...");
					}
				});
			}
		}
	}

	private native void connectToWS(String url) /*-{
		$wnd.connectWS(url)
	}-*/;

	private native void clickElement(JavaScriptObject element) /*-{
		element.click();
	}-*/;

	public native void scrollToBottom(Element element) /*-{
		element.scrollTop = element.scrollHeight;
	}-*/;

	public native void createCbForAcceptMessageGWT() /*-{
		var self = this;
		var callback = $entry(function(message) {
			console.log("Called accept message");
			self.@com.learn.client.ChatPageBrowser::acceptMessage(Ljava/lang/String;)(message);
		});
		$wnd.createCbForAcceptMessage(callback);
	}-*/;

	public native void createCbForLoadChatsGWT() /*-{
		var self = this;
		var callback = $entry(function(defaultClick) {
			console.log("Called load users");
			self.@com.learn.client.ChatPageBrowser::getMyDetails(Z)(defaultClick);
		});
		$wnd.createCbForLoadChats(callback);
	}-*/;
}
