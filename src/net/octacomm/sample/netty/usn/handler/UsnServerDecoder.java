package net.octacomm.sample.netty.usn.handler;

import io.netty.buffer.ByteBuf;
import net.octacomm.sample.netty.common.handler.AbstractServerDecoder;
import net.octacomm.sample.netty.common.msg.IncomingMessage;
import net.octacomm.sample.netty.common.msg.MessageHeader;
import net.octacomm.sample.netty.usn.msg.common.MessageType;
import net.octacomm.sample.netty.usn.msg.common.UsnMessageHeader;

public class UsnServerDecoder extends AbstractServerDecoder {

	@Override
	public int requireHeaderSize() {
		return UsnMessageHeader.MESSAGE_HEADER_LENGTH;
	}
	
	@Override
	public MessageHeader<?> makeMessageHeader(ByteBuf buffer) {
		int msgId = buffer.readUnsignedByte();
		int seq = buffer.readUnsignedByte();
		int size = buffer.readUnsignedByte();
		UsnMessageHeader header = new UsnMessageHeader(MessageType.valueOf(msgId));
		header.setSeq(seq);
		header.setSize(size);
		return header;
	}

	@Override
	public void discardBufferByFailHeader(ByteBuf buffer) {
		buffer.readBytes(buffer.readableBytes());
	}

	@Override
	public void discardBufferByFailBody(ByteBuf buffer, MessageHeader<?> header) {
		buffer.readBytes(header.getRequiredBodySize());
	}

	@Override
	public boolean processChecksum(ByteBuf buffer, IncomingMessage<?> incomingMessage) {
		return true;
	}

}
