package net.octacomm.sample.netty.listener;

public interface MessageSenderAware<T> {
	
	void setMessageSender(MessageSender<T> messageSender);
	
}
