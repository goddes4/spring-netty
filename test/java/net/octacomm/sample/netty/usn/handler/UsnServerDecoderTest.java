package net.octacomm.sample.netty.usn.handler;


import static net.octacomm.sample.netty.usn.msg.common.MessageType.*;
import static org.junit.Assert.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import net.octacomm.sample.netty.usn.handler.UsnServerDecoder;
import net.octacomm.sample.netty.usn.handler.UsnServerEncoder;
import net.octacomm.sample.netty.usn.msg.common.UsnIncomingMessage;
import net.octacomm.sample.netty.usn.msg.common.UsnMessageHelper;

import org.junit.Before;
import org.junit.Test;


public class UsnServerDecoderTest {

	private EmbeddedChannel ch;

	@Before
	public void setup() {
		ch = new EmbeddedChannel(new UsnServerDecoder(), new UsnServerEncoder());
	}
	
	@Test
	public void testDummyIncoming() {
		ch.writeInbound(Unpooled.wrappedBuffer(new byte[] {0x02, (byte) 0xFF, 0x01, 23}));
		
		UsnIncomingMessage msg = (UsnIncomingMessage)ch.readInbound();
		assertEquals(msg.getMessageType(), DUMMY_INCOMING);
	}
	
	@Test
	public void testDummyOutgoingAck() {
		ch.writeInbound(Unpooled.wrappedBuffer(new byte[] {(byte) 0x81, (byte) 0xFF, 0x00}));
		
		UsnIncomingMessage msg = (UsnIncomingMessage)ch.readInbound();
		assertEquals(msg.getMessageType(), DUMMY_OUTGOING_ACK);
	}
	
	@Test
	public void testDummyOutgoing() {
		ByteBuf expectResult = Unpooled.wrappedBuffer(new byte[] {0x01, 0x00, 0x01, 34});
		
		ch.writeOutbound(UsnMessageHelper.makeDummyOutgoing(34));
		
		ByteBuf outBuff = (ByteBuf) ch.readOutbound();
		
		while (expectResult.isReadable()) {
			assertEquals(expectResult.readUnsignedByte(), outBuff.readUnsignedByte());
		}
	}
	
	@Test
	public void testDummyIncomingAck() {
		ByteBuf expectResult = Unpooled.wrappedBuffer(new byte[] {(byte) 0x82, 0x00, 0x00});
		
		ch.writeOutbound(UsnMessageHelper.makeDummyIncomingAck());
		
		ByteBuf outBuff = (ByteBuf) ch.readOutbound();
		
		while (expectResult.isReadable()) {
			assertEquals(expectResult.readUnsignedByte(), outBuff.readUnsignedByte());
		}
	}
	
}
