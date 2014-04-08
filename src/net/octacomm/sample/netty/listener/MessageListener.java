package net.octacomm.sample.netty.listener;

public interface MessageListener<T> {

	void connectionStateChanged(boolean isConnected);

	void messageReceived(T message);
	
}
