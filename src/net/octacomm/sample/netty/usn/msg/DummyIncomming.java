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
public class DummyIncomming extends AbstractUsnMessage implements UsnIncomingMessage {

	private int dummyData;
	
	public DummyIncomming(UsnMessageHeader header) {
		super(header);
	}

	@Override
	public int bodyDataSum() {
		return dummyData;
	}

	@Override
	public void decode(ByteBuf buffer) {
		dummyData = (buffer.readUnsignedByte());
	}
}
