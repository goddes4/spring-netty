package net.octacomm.sample.netty.usn.msg.common;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.octacomm.sample.netty.common.msg.MessageHeader;

/**
 * 1. 해더에 포함되는 필드를 선언한다.
 * 2. getRequiredHeaderSize() 와 getRequiredBodySize() 를 구현한다.
 * 3. 해더의 필드에서 계산되는 checksum() 을 구현한다.
 * 
 * @author taeyo
 *
 */
@Getter
@ToString
public class UsnMessageHeader implements MessageHeader<MessageType> {
	public static final int MESSAGE_HEADER_LENGTH = 3;
	
	@Setter	private int seq;
	@Setter	private int size;
	
	private MessageType messageType;
	
	public UsnMessageHeader(MessageType messageType) {
		this.messageType = messageType;
	}
	
	/**
	 * Body를 생성하기 위해 필요한 사이즈
	 * for decoding
	 * 
	 * @return
	 */
	@Override
	public int getRequiredBodySize() {
		return size;
	}

	/**
	 * Header에서 checksum에 포함되는 값을 계산
	 * 
	 * @return
	 */
	@Override
	public int checksum() {
		return messageType.getId() + size;
	}

	@Override
	public void encode(ByteBuf buffer) {
		
	}

}
