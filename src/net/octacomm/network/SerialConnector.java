package net.octacomm.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.rxtx.RxtxChannel;
import io.netty.channel.rxtx.RxtxChannelConfig.Databits;
import io.netty.channel.rxtx.RxtxChannelConfig.Paritybit;
import io.netty.channel.rxtx.RxtxChannelConfig.Stopbits;
import io.netty.channel.rxtx.RxtxChannelOption;
import io.netty.channel.rxtx.RxtxDeviceAddress;
import net.octacomm.sample.netty.common.msg.OutgoingMessage;
import net.octacomm.sample.netty.listener.MessageSender;
import net.octacomm.util.PrintUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.concurrent.ListenableFuture;

@Qualifier("remocon")
public class SerialConnector implements MessageSender<OutgoingMessage<?>> {

	private final Logger logger = LoggerFactory.getLogger(getClass());
    
	private EventLoopGroup group;
    private Stopbits stopbits = Stopbits.STOPBITS_1;
    private Databits databits = Databits.DATABITS_8;
    private Paritybit paritybit = Paritybit.NONE;

	private String deviceAddress;
    private int baudrate;
    private ChannelInitializer<RxtxChannel> channelInitializer;
    private Channel channel;

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public void setBaudrate(int baudrate) {
        this.baudrate = baudrate;
    }

    public void setChannelInitializer(ChannelInitializer<RxtxChannel> channelInitializer) {
        this.channelInitializer = channelInitializer;
    }

    public boolean start() {
        final Bootstrap bootstrap = new Bootstrap();
        group = new OioEventLoopGroup();
        
        bootstrap.group(group)
        	.channel(RxtxChannel.class)
        	.option(RxtxChannelOption.BAUD_RATE, baudrate)
        	.option(RxtxChannelOption.STOP_BITS, stopbits)
        	.option(RxtxChannelOption.DATA_BITS, databits)
        	.option(RxtxChannelOption.PARITY_BIT, paritybit)
        	.handler(channelInitializer);

        // Make a new connection.
        ChannelFuture future = bootstrap.connect(new RxtxDeviceAddress(deviceAddress));
        future.awaitUninterruptibly();
        if (!future.isSuccess()) {
        	logger.error("Remocon channel is disconnected", future.cause());
        	return false;
        } else {
        	logger.info("Remocon channel is connected");
        }
        
        channel = future.channel();
        
        future.channel().closeFuture().addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture cf) throws Exception {
                // An Executor cannot be shut down from the thread acquired from itself.  
                // Please make sure you are not calling releaseExternalResources() from an I/O worker thread.
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                    	channel = null;
                    	logger.info("Channel is closed.");
                    	group.shutdownGracefully();
                    }
                }).start();
            }
        });
        return true;
    }
    
    public boolean stop() {
    	if (channel != null && channel.isActive()) {
    		channel.close();
    		return true;
    	} else {
    		return false;
    	}
    }

	@Override
	public boolean isConnected() {
		if (channel != null) {
			return channel.isActive();
		} else {
			return false;
		}
	}

	@Override
	public boolean sendSyncMessage(OutgoingMessage<?> packet, boolean ackReq) {
		channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
		return true;
	}

	@Override
	public ListenableFuture<Boolean> sendAsyncMessage(OutgoingMessage<?> packet, boolean ackReq) {
		throw new UnsupportedOperationException();
	}

	public void forceClose() {
		if (channel != null) {
			channel.close();
		}
	}

    private static final Logger log = LoggerFactory.getLogger(SerialConnector.class);

    public static void main(String[] args) {
    	SerialConnector connector = new SerialConnector();
        connector.setDeviceAddress("COM3");
        connector.setBaudrate(19200);
        connector.setChannelInitializer(new ChannelInitializer<RxtxChannel>() {
			
			@Override
			protected void initChannel(RxtxChannel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("loggingHandler", new SimpleChannelInboundHandler<ByteBuf>() {

					@Override
					protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
						log.debug("{}", PrintUtil.printReceivedChannelBuffer("recv", msg));
					}
				});
			}
		});
        connector.start();
    }

}
