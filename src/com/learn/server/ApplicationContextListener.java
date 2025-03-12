package com.learn.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class ApplicationContextListener implements ApplicationListener<ContextRefreshedEvent> {
	public static ApplicationContext applicationContext;
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		// TODO Auto-generated method stub
		applicationContext = event.getApplicationContext();
		System.out.println("Application context object created\n\n\n");
	}

}
