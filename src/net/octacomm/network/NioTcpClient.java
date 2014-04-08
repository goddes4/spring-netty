package net.octacomm.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.net.InetSocketAddress;

import net.octacomm.util.DelayUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

/**
 *
 * @author Taeyoung, Kim
 */
public class NioTcpClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private String localIP;
    private int localPort;
    private String serverIP;
    private int serverPort;
    private ChannelInitializer<SocketChannel> channelInitializer;

    private final Bootstrap bootstrap = new Bootstrap();
    
	private final Runnable retryConnect = new Runnable() {
		@Override
		public void run() {
			DelayUtil.sleep(10);
			connect();
		}
	};

    
    @Autowired
    private TaskExecutor executor; 

    public void setLocalIP(String localIP) {
        this.localIP = localIP;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setChannelInitializer(ChannelInitializer<SocketChannel> channelInitializer) {
        this.channelInitializer = channelInitializer;
    }
    
    public void init() {
    	bootstrap.group(new NioEventLoopGroup())
    		.channel(NioSocketChannel.class)
    		.option(ChannelOption.TCP_NODELAY, true)
    		.option(ChannelOption.SO_KEEPALIVE, true)
    		.handler(channelInitializer);
    	
        connect();
    }

	private void connect() {
		logger.info("Attempts a new connection to TCP Server.");
        ChannelFuture future = null;

        if (localPort == 0) {
            future = bootstrap.connect(new InetSocketAddress(serverIP, serverPort));
        } else {
            if (localIP == null || localIP.isEmpty()) {
                future = bootstrap.connect(new InetSocketAddress(serverIP, serverPort), new InetSocketAddress(localPort));
            } else {
                future = bootstrap.connect(new InetSocketAddress(serverIP, serverPort), new InetSocketAddress(localIP, localPort));
            }
        }
        future.addListener(new ChannelFutureListener() {
			
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if (future.isSuccess()) {
					logger.info("Success!!!");

					future.channel().closeFuture().addListener(new ChannelFutureListener() {
			            @Override
			            public void operationComplete(ChannelFuture cf) throws Exception {
			            	logger.info("Channel Close!!!");
							executor.execute(retryConnect);
			            }
			        });
					
				} else {
					logger.info("Failre!!! Retry to connect");
					executor.execute(retryConnect);
				}
			}
		});
	}

    public static void main(String[] args) {
        NioTcpClient tcpClient = new NioTcpClient();
        tcpClient.setServerIP("127.0.0.1");
        tcpClient.setServerPort(9001);
        tcpClient.setChannelInitializer(new ChannelInitializer<SocketChannel>() {
			
			@Override
			protected void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast(
						new ObjectEncoder(),
						new ObjectDecoder(ClassResolvers.softCachingResolver(null)),
						new SimpleChannelInboundHandler<String>() {

							@Override
							public void channelActive(ChannelHandlerContext ctx) throws Exception {
								ctx.channel().writeAndFlush("xxx").addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
							}

							@Override
							public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
								cause.printStackTrace();
								ctx.close();
							}

							@Override
							protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
								System.out.println(msg);
							}
						});
			}
		});
        tcpClient.init();
    }
}
