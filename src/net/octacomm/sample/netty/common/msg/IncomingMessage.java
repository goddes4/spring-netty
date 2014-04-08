package net.octacomm.sample.netty.common.msg;

import io.netty.buffer.ByteBuf;

/**
 * @author taeyo
 *
 * @param <T> MessageType
 */
public interface IncomingMessage<T extends IMessageType> {
	
	T getMessageType();

	void decode(ByteBuf buffer);

	int checksum();
	
}
