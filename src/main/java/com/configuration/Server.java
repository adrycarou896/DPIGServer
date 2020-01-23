package com.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import com.services.IPCamerasRecord;
import com.smarthings.IPCamerasManager;
import com.socket.ISocketServer;
import com.trainning.Trainning;
import com.utils.Util;

@Configuration
public class Server {
	
	@Autowired
	private IPCamerasRecord ipCamerasRecord;
	
	@Bean
	public TaskExecutor createTaskExecutor() {
		return new SimpleAsyncTaskExecutor();
	}
	
	@Bean
	public CommandLineRunner createEventServerRunner(TaskExecutor executor, ISocketServer server) {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				executor.execute(server);
			}
		};
	}
	
	
	@Bean
	public CommandLineRunner schedulingRunner(TaskExecutor executor) {
	    return new CommandLineRunner() {
	        public void run(String... args) throws Exception {
	            executor.execute(new Runnable() {
					@Override
					public void run() {
						System.load(Util.LOAD_OPENCV_PATH);
						
						Trainning train = new Trainning();
						train.run();
						
						IPCamerasManager ipCamerasManager = new IPCamerasManager();
						ipCamerasRecord.setConf(ipCamerasManager, train);
						while (true) {
							ipCamerasRecord.run();
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
					}
				});
	        }
	    };
	}
	
	
}
