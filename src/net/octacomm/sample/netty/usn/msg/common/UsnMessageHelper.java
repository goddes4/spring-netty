package net.octacomm.sample.netty.usn.msg.common;

import net.octacomm.sample.netty.usn.msg.DummyIncommingAck;
import net.octacomm.sample.netty.usn.msg.DummyOutgoing;

/**
 * 송신 메시지를 만들어 주는 헬퍼 클래스
 * 
 * @author taeyo
 *
 */
public class UsnMessageHelper {
	
	public static UsnOutgoingMessage makeDummyOutgoing(int dummyData) {
		return new DummyOutgoing(dummyData);
	}

	public static UsnOutgoingMessage makeDummyIncommingAck() {
		return new DummyIncommingAck();
	}
}
