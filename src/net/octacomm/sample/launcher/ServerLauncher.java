package net.octacomm.sample.launcher;

import java.io.FileNotFoundException;

import net.octacomm.util.Slf4jConfigurer;

import org.slf4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

public class ServerLauncher {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(ServerLauncher.class);
	
	public static void main(String[] args) throws FileNotFoundException{
		Slf4jConfigurer.initLogging("resources/logback.xml");
		
		@SuppressWarnings("resource")
		AbstractApplicationContext context = new AnnotationConfigApplicationContext(ServerConfig.class);
		logger.info("Run Server ...");
		context.registerShutdownHook();
	}
}