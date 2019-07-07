package com.server.eventsserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class EventsServerConfiguration {

	@Bean
	public TaskExecutor createTaskExecutor() {
		return new SimpleAsyncTaskExecutor();
	}
	
	@Bean
	public CommandLineRunner createEventServerRunner(TaskExecutor executor, IEventsServer server) {
		
		return new CommandLineRunner() {
			
			@Override
			public void run(String... args) throws Exception {
				executor.execute(server);
			}
		};
	}
	
	
}
