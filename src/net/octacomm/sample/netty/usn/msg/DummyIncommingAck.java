package net.octacomm.sample.netty.usn.msg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.ToString;
import net.octacomm.sample.netty.usn.msg.common.AbstractUsnMessage;
import net.octacomm.sample.netty.usn.msg.common.MessageType;
import net.octacomm.sample.netty.usn.msg.common.UsnMessageHeader;
import net.octacomm.sample.netty.usn.msg.common.UsnOutgoingMessage;

/**
 * 
 * @author taeyo
 *
 */
@Getter
@ToString(callSuper = true)
public class DummyIncommingAck extends AbstractUsnMessage implements UsnOutgoingMessage {

	public DummyIncommingAck() {
		super(new UsnMessageHeader(MessageType.DUMMY_INCOMMING_ACK));		
	}

	@Override
	public void encode(ByteBuf buffer) {
	}

	public int bodyDataSum() {
		return 0;
	}
}
