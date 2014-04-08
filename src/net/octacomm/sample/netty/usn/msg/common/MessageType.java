package net.octacomm.sample.netty.usn.msg.common;

import lombok.Getter;
import net.octacomm.sample.netty.common.msg.IMessageType;
import net.octacomm.sample.netty.exception.NotSupprtedMessageIdException;
import net.octacomm.sample.netty.usn.msg.DummyIncoming;
import net.octacomm.sample.netty.usn.msg.DummyOutgoingAck;

@Getter
public enum MessageType implements IMessageType {
	DUMMY_OUTGOING(0x01, 1),
	DUMMY_OUTGOING_ACK(0x81, DummyOutgoingAck.class),
	DUMMY_INCOMING(0x02, DummyIncoming.class),
	DUMMY_INCOMING_ACK(0x82, 0)
	;
	
	@Getter private Class<? extends UsnIncomingMessage> incomingClass;
	@Getter	private int id;
	@Getter	private int requireBodySize;
	private String desc;

	private MessageType(int id, int size) {
		this(id, size, null);
	}
	
	private MessageType(int id, Class<? extends UsnIncomingMessage> incomingClass) {
		this(id, 0, incomingClass);
	}
	
	private MessageType(int id, int size, Class<? extends UsnIncomingMessage> incomingClass) {
		this.id = id;
		this.requireBodySize = size;
		this.incomingClass = incomingClass;
		
		desc = name() + String.format("{ id:%02X, size:%d, class:%s }", id, size, (incomingClass == null ? null : incomingClass.getSimpleName()));
	}
	
	@Override
	public String toString() {
		return desc;
	}

	public static MessageType valueOf(int id) throws NotSupprtedMessageIdException {
		for (MessageType msg : MessageType.values()) {
			if (msg.id == id && msg.incomingClass != null) {
				return msg;
			}
		}
		throw new NotSupprtedMessageIdException(id);
	}
	
}
