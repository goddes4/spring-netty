package net.octacomm.sample.netty.common.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import net.octacomm.sample.netty.common.msg.IMessageType;
import net.octacomm.sample.netty.common.msg.IncomingMessage;
import net.octacomm.sample.netty.common.msg.MessageHeader;
import net.octacomm.sample.netty.exception.InvalidChecksumException;
import net.octacomm.util.PrintUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractServerDecoder extends ByteToMessageDecoder {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private MessageHeader<? extends IMessageType> header;
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
		decode(buffer, out);
		logger.debug(PrintUtil.printReceivedChannelBuffer("After decoding", buffer));		
	}

	private void decode(ByteBuf buffer, List<Object> out) {
		logger.debug(PrintUtil.printReceivedChannelBuffer("in", buffer));

		while (buffer.isReadable()) {
			// 이전 수신 처리에서 Header 까지 완료 되었는지 확인
			if (header == null) {
				// Header를 처리하기 위한 데이터가 있는지 확인
				if (buffer.readableBytes() < requireHeaderSize()) {
					return;
				}
				
				try {
					header = makeMessageHeader(buffer);
				} catch (RuntimeException e) {
					discardBufferByFailHeader(buffer);
					logger.error("", e);
					continue;
				}
			}
			
			// Body 를 처리하기 위한 데이터가 있는지 확인
			if (buffer.readableBytes() < header.getRequiredBodySize()) {
				return;
			}
	
			try {
				out.add(makeMessageBody(buffer));
			} catch (InvalidChecksumException | ReflectiveOperationException e) {
				logger.error("", e);
			} catch (RuntimeException e) {
				header = null;
				throw e;
			}
			// 패킷이 완성이 되면 리스트에 저장하고, 반드시 Header는 null로 초기화 해야 한다.
			header = null;
		}
	}

	public abstract int requireHeaderSize();

	/**
	 * 메시지 해더를 생성하고, 데이터를 입력한다.
	 * ex1) 현재 수신한 모든 버퍼를 삭제한다.
	 * ex2) 예외를 발생시켜 채널을 종료한다.
	 * 
	 * @param buffer
	 */
	public abstract MessageHeader<? extends IMessageType> makeMessageHeader(ByteBuf buffer);
 
	/**
	 * 버퍼를 이용해 IncomingMessage 생성한다.
	 * 예외가 발생할 경우 size만큼 데이터를 읽어서 버린다.
	 * Checksum 비교
	 *  
	 * @param buffer
	 */
	private IncomingMessage<?> makeMessageBody(ByteBuf buffer) throws InvalidChecksumException, ReflectiveOperationException {
		IncomingMessage<?> incomingMessage;
		
		try {
			incomingMessage = header.getMessageType().getIncomingClass().getConstructor(header.getClass()).newInstance(header);
		} catch (ReflectiveOperationException e) {
			discardBufferByFailBody(buffer, header);
			throw e;
		}
		incomingMessage.decode(buffer);
		
		if (!processChecksum(buffer, incomingMessage)) {
			throw new InvalidChecksumException(incomingMessage.checksum());
		}
		return incomingMessage;
	}
	
	/**
	 * Header 처리시 예외가 발생 했을 경우 buffer 처리
	 * 
	 * @param buffer
	 */
	public abstract void discardBufferByFailHeader(ByteBuf buffer);
	
	/**
	 * Body 처리시 예외가 발생 했을 경우 buffer 처리
	 * ex1) 현재 수신한 모든 버퍼를 삭제한다.
	 * ex2) 예외를 발생시켜 채널을 종료한다.
	 * ex3) BodySize 를 버퍼에서 삭제한다. (뒤에 메시지를 보존하기 위해)
	 * 
	 * @param buffer
	 * @param header
	 */
	public abstract void discardBufferByFailBody(ByteBuf buffer, MessageHeader<?> header);

	/**
	 * USN 메시지가 checksum 이 존재 할경우 해당 패킷을 처리한다.
	 * 
	 * @param buffer
	 * @param incomingMessage
	 * @return
	 */
	public abstract boolean processChecksum(ByteBuf buffer, IncomingMessage<?> incomingMessage);
	
}
