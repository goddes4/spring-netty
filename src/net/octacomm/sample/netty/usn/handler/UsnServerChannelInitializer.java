package net.octacomm.sample.netty.usn.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import javax.inject.Provider;

import org.springframework.beans.factory.annotation.Autowired;

public class UsnServerChannelInitializer extends ChannelInitializer<SocketChannel> {

	@Autowired
	Provider<UsnServerHandler> usnServerHandlerProvider;
	
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		
		pipeline.addLast("encoder", new UsnServerEncoder());
		pipeline.addLast("decoder", new UsnServerDecoder());
		
		// business logic
		pipeline.addLast("hanlder", usnServerHandlerProvider.get());
	}

}
