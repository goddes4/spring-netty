package net.octacomm.sample.launcher;

import java.util.concurrent.ThreadPoolExecutor;

import net.octacomm.logger.LoggerBeanPostProcessor;
import net.octacomm.network.NioTcpServer;
import net.octacomm.sample.netty.usn.handler.UsnServerChannelInitializer;
import net.octacomm.sample.netty.usn.handler.UsnServerHandler;
import net.octacomm.sample.service.UsnMessageProcessor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@PropertySource("file:resources/netty.properties")
@EnableScheduling
@ComponentScan(basePackageClasses = UsnMessageProcessor.class)
public class ServerConfig {

	@Bean
	public BeanPostProcessor loggerPostProcessor() {
		return new LoggerBeanPostProcessor();
	}

	@Bean(destroyMethod = "shutdown")
	public TaskExecutor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(20);
		executor.setMaxPoolSize(30);
		executor.setQueueCapacity(100);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
		
		return executor;
	}

	@Configuration
	static class UsnServerConfig {

		@Autowired
		private Environment env;
		
		@Bean
		public UsnServerHandler usnServerHandler() {
			return new UsnServerHandler();
		}

		@Bean
		public UsnServerChannelInitializer usnServerChannelInitializer() {
			return new UsnServerChannelInitializer();
		}
		
		@Bean(destroyMethod = "close")
		public NioTcpServer usnTcpServer() {
			NioTcpServer tcpServer = new NioTcpServer();
			tcpServer.setLocalIP(env.getProperty("tcp.local.ip"));
			tcpServer.setLocalPort(env.getProperty("tcp.local.usn.port", int.class));
			tcpServer.setChannelInitializer(usnServerChannelInitializer());
			tcpServer.init();
			return tcpServer;
		}
	}	
}
