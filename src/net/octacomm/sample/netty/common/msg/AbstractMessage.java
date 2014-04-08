package net.octacomm.sample.netty.common.msg;

import lombok.Getter;


/**
 * AbstractMessage 를 상속하는 클래스는 IncommingMessage와 OutgoingMessage로 분리 된다.
 * 
 * IncommingMessage은 Netty Decoder를 통해서 파싱 되기 때문에
 * MessagePacket보다 MessageHeader가 먼저 생성된다.
 * 그래서 IncommingMessage는 MessageHeader를 생성자 파라메터로 입력해야 한다.
 * 
 * OutgoingMessage는 서버에서 외부로 송신되는 메시지로
 * 생성자에서 MessageHeader를 만든다.
 * 
 * @author taeyo
 *
 * @param <H> MessageHeader
 * @param <T> MessageType
 */
public abstract class AbstractMessage<H extends MessageHeader<T>, T extends IMessageType> {

	@Getter
	private H header;

	public AbstractMessage(H header) {
		this.header = header;
	}
	
	public T getMessageType() {
		return header.getMessageType();
	}
	
	/**
	 * MessageHeader 와 MessageBody 의 데이터를 합친 checksum
	 * 
	 * @return
	 */
	public int checksum() {
		return bodyDataSum() + header.checksum();
	}
	
	protected abstract int bodyDataSum();

	/**
	 * 필드의 데이터 사이즈가 2 바이트인 경우에 
	 * Checksum 계산을 위해서 상위 바이트와 하위 바이트를 더한다. 
	 * 
	 * @param twoBytesArray
	 * @return
	 */
	protected static int calqTwoBytesChecksum(int ... twoBytesArray) {
		int result = 0;
		for (int twoBytes : twoBytesArray) {
			result += ((twoBytes >> 8) & 0xFF) + (twoBytes & 0xFF);
		}
		return result;
	}
	
	public String toString() {
		return header.toString();
	}
}
