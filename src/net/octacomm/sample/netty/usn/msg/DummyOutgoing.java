package net.octacomm.sample.netty.usn.msg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.ToString;
import net.octacomm.sample.netty.usn.msg.common.AbstractUsnMessage;
import net.octacomm.sample.netty.usn.msg.common.MessageType;
import net.octacomm.sample.netty.usn.msg.common.UsnMessageHeader;
import net.octacomm.sample.netty.usn.msg.common.UsnOutgoingMessage;

/**
 * request가 true이면 TimeSync 요청 메시지 이고
 *           false 이면 NotifyTimeSync 메시지의 Ack 이다.
 * 
 * @author taeyo
 *
 */
@Getter
@ToString(callSuper = true)
public class DummyOutgoing extends AbstractUsnMessage implements UsnOutgoingMessage {

	private int dummyData;

	public DummyOutgoing(int dummyData) {
		super(new UsnMessageHeader(MessageType.DUMMY_OUTGOING));		
		this.dummyData = dummyData;
	}

	@Override
	public void encode(ByteBuf buffer) {
		buffer.writeByte(dummyData);
	}

	@Override
	public int bodyDataSum() {
		return dummyData;
	}
}
