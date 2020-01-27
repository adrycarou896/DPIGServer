package dpigServer.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import dpigServer.services.IPCamerasRecord;
import dpigServer.services.InsertDataService;
import dpigServer.socket.ISocketServer;
import dpigServer.training.Training;
import dpigServer.utils.Util;

@Configuration
public class Server{
	
	@Autowired
	private IPCamerasRecord ipCamerasRecord;
	
	@Autowired
	private InsertDataService insertDataService;
	
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
						
						Training train = new Training(insertDataService.getUtil());
						train.run();
						
						ipCamerasRecord.setConf(train, insertDataService.getUtil());
						while (true) {
							long inicio = System.currentTimeMillis();
							ipCamerasRecord.run();
							long fin = System.currentTimeMillis();
							System.out.println("Tiempo->"+((fin-inicio)/1000));
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
