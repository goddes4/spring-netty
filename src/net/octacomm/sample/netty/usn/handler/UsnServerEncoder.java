package net.octacomm.sample.netty.usn.handler;

import io.netty.buffer.ByteBuf;

import java.nio.ByteOrder;

import net.octacomm.sample.netty.common.handler.AbstractServerEncoder;
import net.octacomm.sample.netty.common.msg.OutgoingMessage;
import net.octacomm.sample.netty.usn.msg.common.UsnMessageHeader;

public class UsnServerEncoder extends AbstractServerEncoder {

	@Override
	public void encode(OutgoingMessage<?> message, ByteBuf out) {
		out.order(ByteOrder.BIG_ENDIAN);

		UsnMessageHeader header = (UsnMessageHeader) message.getHeader();

		out.writeByte(message.getMessageType().getId());
		out.writeByte(header.getSeq());
		out.writeByte(message.getMessageType().getRequireBodySize());
		message.encode(out);
	}

}
