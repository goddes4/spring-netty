package net.octacomm.sample.netty.usn.handler;

import io.netty.channel.ChannelHandler.Sharable;
import net.octacomm.sample.netty.common.handler.AbstractServerHandler;
import net.octacomm.sample.netty.listener.MessageListener;
import net.octacomm.sample.netty.usn.msg.common.UsnIncomingMessage;
import net.octacomm.sample.netty.usn.msg.common.UsnOutgoingMessage;

import org.springframework.beans.factory.annotation.Autowired;

@Sharable
public class UsnServerHandler extends AbstractServerHandler<UsnIncomingMessage, UsnOutgoingMessage> {

	@Autowired
	@Override
	public void setListener(MessageListener<UsnIncomingMessage> listener) {
		super.listener = listener;
	}

	/**
	 * IncomingPacket 이면서 suffix가 Ack 인 클래스의 경우
	 * sendSyncMessage() 에서 보낸 메시지의 Ack로 인식할 수 있도록 함
	 * 
	 * 예) EnemyHitResult -> EnemyHitResultAck
	 * 
	 * @param packet
	 * @return
	 */
	@Override
	protected boolean isAckMessage(UsnIncomingMessage packet)  {
		return packet.getClass().getSimpleName().endsWith("Ack");
	}

	@Override
	protected boolean checkAckMessage(UsnOutgoingMessage out, UsnIncomingMessage in)  {
		return (out.getMessageType().getId() + 0x80) == in.getMessageType().getId();
	}
}
