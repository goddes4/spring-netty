package net.octacomm.sample.netty.usn.msg;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.octacomm.sample.netty.usn.msg.common.AbstractUsnMessage;
import net.octacomm.sample.netty.usn.msg.common.UsnIncomingMessage;
import net.octacomm.sample.netty.usn.msg.common.UsnMessageHeader;

@Setter
@Getter
@ToString(callSuper = true)
public class DummyOutgoingAck extends AbstractUsnMessage implements UsnIncomingMessage {

	public DummyOutgoingAck(UsnMessageHeader header) {
		super(header);
	}

	@Override
	public int bodyDataSum() {
		return 0;
	}

	@Override
	public void decode(ByteBuf buffer) {
	}
}
