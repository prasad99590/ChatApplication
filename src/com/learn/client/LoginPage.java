package com.learn.client;

import java.util.HashMap;
import java.util.Map;

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

public class LoginPage {
	private VerticalPanel loginBox;
	private Label heading;
	private TextBox username;
	private PasswordTextBox password;
	private Button login;
	private Button signUp;
	private UserServiceAsync userService;

	public LoginPage() {
		userService = GWT.create(UserService.class);
		loginBox = new VerticalPanel();
		loginBox.getElement().setClassName("loginBox");
		heading = new Label("Login Details");
		heading.getElement().setClassName("heading");
		username = new TextBox();
		username.getElement().setAttribute("placeholder", "Username");
		username.getElement().setClassName("textbox");
		password = new PasswordTextBox();
		password.getElement().setAttribute("placeholder", "Password");
		password.getElement().setClassName("textbox");
		login = new Button("Login");
		login.getElement().setClassName("button");
		signUp = new Button("Register");
		signUp.getElement().setClassName("button-register");
	}

	public void loadPage() {
		loginBox.add(heading);
		loginBox.add(username);
		loginBox.add(password);
		loginBox.add(login);
		loginBox.add(signUp);
		signUp.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				new Register().loadPage();
			}
		});
		RootPanel.get().clear();
		RootPanel.get().add(loginBox);
		login.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				if (username.getText().length() > 0) {
					userService.validateUser(username.getText(), password.getText(), new AsyncCallback<User>() {

						@Override
						public void onSuccess(User result) {
							// TODO Auto-generated method stub
							if(result == null) {
								Window.alert("Authentication Failed!!! Provide Valid Details...");
								new LoginPage().loadPage();
							}
							else {
								String jsonData = "{\"operation\":\"Authentication\","
						                + "\"username\":\"" + username.getText() + "\","
						                + "\"password\":\"" + password.getText() + "\"}";
								setData(jsonData);
								RootPanel.get().clear();
								new ChatPageBrowser(result);
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub
							Window.alert("Error in vaidating user...");
							new LoginPage().loadPage();
						}
					});
				}
				else {
					Window.alert("Enter valid details...");
				}
			}
		});
	}
	private native void setData(String data) /*-{
		$wnd.setAuthorizationData(data);
	}-*/;

}
