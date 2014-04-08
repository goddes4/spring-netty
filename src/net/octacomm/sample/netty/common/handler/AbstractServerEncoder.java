package net.octacomm.sample.netty.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.octacomm.sample.netty.common.msg.OutgoingMessage;
import net.octacomm.util.PrintUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServerEncoder extends MessageToByteEncoder<OutgoingMessage<?>> {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	protected void encode(ChannelHandlerContext ctx, OutgoingMessage<?> msg, ByteBuf out) throws Exception {
		logger.debug("[Outgoing] {} => {}", ctx.channel(), msg);
		encode(msg, out);
		
		logger.debug(PrintUtil.printReceivedChannelBuffer("out", out));
	}

	/**
	 * 1. 데이터 전송에 필요한 ChannelBuffer를 생성한다.
	 * 2. 생성한 buffer에 데이터를 담고, 반환한다.
	 * 
	 * @param message
	 * @param out 
	 * @return
	 */
	public abstract void encode(OutgoingMessage<?> message, ByteBuf out);

}
