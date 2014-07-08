package net.octacomm.sample.netty.exception;

import net.octacomm.sample.netty.common.msg.IMessageType;

public class InvalidDataSizeException extends RuntimeException {

	public InvalidDataSizeException(IMessageType msg, int size) {
		super("Size of " + msg + " is " + msg.getRequireBodySize());
	}
}
