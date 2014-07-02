package net.octacomm.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Taeyoung,Kim
 */
public class NioTcpServer extends ChannelInboundHandlerAdapter {

    private static final int DEFAULT_SERVER_PORT = 10000;

	private final Logger logger = LoggerFactory.getLogger(getClass());
    
    // Spring DI
    private String localIP;
    private int localPort = DEFAULT_SERVER_PORT;
    private ChannelInitializer<SocketChannel> channelInitializer;
    
    private	ChannelFuture channelFuture;
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    @Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	logger.info(ctx.channel() + " NioTcpServer is bound and started to accept incoming connections .");
	}

    public void setLocalIP(String serverIP) {
        this.localIP = serverIP;
    }

    public void setLocalPort(int serverPort) {
        this.localPort = serverPort;
    }

    public void setChannelInitializer(ChannelInitializer<SocketChannel> channelInitializer) {
        this.channelInitializer = channelInitializer;
    }

    public void init() {
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
        		.channel(NioServerSocketChannel.class)
        		.childHandler(channelInitializer)
        		.childOption(ChannelOption.TCP_NODELAY, true)
        		.childOption(ChannelOption.SO_KEEPALIVE, true)
        		.handler(this);
        
        logger.debug("---------- Netty Option ----------");
        logger.debug(" tcpNoDelay is true");
        logger.debug(" keepAlive is true");

        // Bind and start to accept incoming connections.
        if (localIP == null || localIP.isEmpty()) {
        	channelFuture = bootstrap.bind(new InetSocketAddress(localPort));
        } else {
        	channelFuture = bootstrap.bind(new InetSocketAddress(localIP, localPort));
        }
    }
    
    public void close() {
    	if (channelFuture != null) {
    		try {
				channelFuture.channel().close().sync();
			} catch (InterruptedException e) {
				logger.info("", e.toString());
			}
    		logger.info("releaseExternalResources");
    		bossGroup.shutdownGracefully();
    		workerGroup.shutdownGracefully();
    	}
    }
}
