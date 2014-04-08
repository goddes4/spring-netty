package net.octacomm.sample.netty.common.msg;

public interface IMessageType {
	
	int getId();

	int getRequireBodySize();
	
	Class<? extends IncomingMessage<?>> getIncomingClass();

}
