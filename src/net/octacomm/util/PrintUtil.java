package net.octacomm.util;

import io.netty.buffer.ByteBuf;

public class PrintUtil {
	public static String printReceivedChannelBuffer(String msg, ByteBuf buffer) {
		ByteBuf copyBuffer = buffer.copy();
		StringBuilder str = new StringBuilder();

		str.append(msg + " : ");
		while (copyBuffer.isReadable()) {
			str.append(String.format("%02X ", copyBuffer.readUnsignedByte()));
		}

		return str.toString();
	}
}
