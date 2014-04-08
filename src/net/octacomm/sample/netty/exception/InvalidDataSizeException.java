package net.octacomm.sample.netty.exception;

import net.octacomm.sample.netty.usn.msg.common.MessageType;

public class InvalidDataSizeException extends RuntimeException {

	public InvalidDataSizeException(MessageType msg, int size) {
		super("Size of " + msg + " is " + msg.getRequireBodySize());
	}
}
