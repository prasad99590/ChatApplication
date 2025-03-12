package com.learn.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.learn.model.User;
import com.learn.service.UserService;
import com.learn.service.UserServiceAsync;

public class Register {
	private VerticalPanel loginBox;
	private Label heading;
	private TextBox username;
	private PasswordTextBox password;
	private Button login;
	private UserServiceAsync userService;

	public Register() {
		userService = GWT.create(UserService.class);
		loginBox = new VerticalPanel();
		loginBox.getElement().setClassName("loginBox");
		heading = new Label("Register Form");
		heading.getElement().setClassName("heading");
		username = new TextBox();
		username.getElement().setAttribute("placeholder", "Username");
		username.getElement().setClassName("textbox");
		password = new PasswordTextBox();
		password.getElement().setAttribute("placeholder", "Password");
		password.getElement().setClassName("textbox");
		login = new Button("Register");
		login.getElement().setClassName("button");
	}

	public void loadPage() {
		loginBox.add(heading);
		loginBox.add(username);
		loginBox.add(password);
		loginBox.add(login);
		RootPanel.get().clear();
		RootPanel.get().add(loginBox);
		login.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if (username.getText().length() > 0) {
					userService.saveUser(username.getText(), password.getText(), new AsyncCallback<User>() {

						@Override
						public void onSuccess(User result) {
							// TODO Auto-generated method stub
							if (result == null) {
								Window.alert("Username already exists...");
							} else {
								Window.alert("Registered Successfully...");
								RootPanel.get().clear();
								new LoginPage().loadPage();
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
							Window.alert("Error in saving user...");
						}
					});
				} else {
					Window.alert("Enter valid details...");
				}
			}
		});
	}

	private native void setData(String data) /*-{
		$wnd.setAuthorizationData(data);
	}-*/;
}
