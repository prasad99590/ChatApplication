package com.learn.client;

import com.google.gwt.core.client.EntryPoint;

public class ChatAppGWT implements EntryPoint {
	
	public void onModuleLoad() {
		new LoginPage().loadPage();
	}
}
