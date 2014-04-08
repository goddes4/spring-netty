package net.octacomm.sample.netty.usn.msg.common;

import net.octacomm.sample.netty.common.msg.AbstractMessage;

public abstract class AbstractUsnMessage extends AbstractMessage<UsnMessageHeader, MessageType> {

	public AbstractUsnMessage(UsnMessageHeader header) {
		super(header);
	}
}
