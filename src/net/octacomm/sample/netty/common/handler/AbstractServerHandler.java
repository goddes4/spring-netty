package net.octacomm.sample.netty.common.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import net.octacomm.sample.netty.common.msg.IncomingMessage;
import net.octacomm.sample.netty.common.msg.OutgoingMessage;
import net.octacomm.sample.netty.exception.InvalidDataSizeException;
import net.octacomm.sample.netty.exception.NotSupprtedMessageIdException;
import net.octacomm.sample.netty.listener.MessageListener;
import net.octacomm.sample.netty.listener.MessageSender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

/**
 * USN 과 연결되는 Channel을 통해서 데이터의 read, write 를 처리한다.
 * MessageSensor<MessagePacket> 은 본 채널을 통해서 전송하기 위해서 사용하는 인터페이스.
 * 
 * MessageSensor 를 구현한 핸들러는 GuiServerHandler, UsnServerHandler 총 2개가 되며,
 * @Autowired를 사용하기 위해 Qualifier 사용한다.
 * 
 * @author taeyo
 *
 */
@Sharable
public abstract class AbstractServerHandler<I extends IncomingMessage<?>, O extends OutgoingMessage<?>> extends SimpleChannelInboundHandler<I> implements MessageSender<O> {

	private static final int SYNC_MESSAGE_TIMEOUT_SEC = 2000;
	private static final int RETRY_COUNT = 3;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private TaskExecutor executor;
	
	private BlockingQueue<I> recvLock = new SynchronousQueue<I>();
	private Channel channel;

	protected MessageListener<I> listener;

	public abstract void setListener(MessageListener<I> listener);

	private String getChannelName() {
		return channel.remoteAddress().toString();
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		channel = ctx.channel();
		logger.debug("{} is connected", getChannelName());
		listener.connectionStateChanged(true);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.debug("{} was disconnected", getChannelName());
		listener.connectionStateChanged(false);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, I packet)
			throws Exception {
		
		logger.info("[Incoming] {} {}", getChannelName(), packet);

		if (isAckMessage(packet)) {
			recvLock.offer(packet);
		} else {
			listener.messageReceived(packet);
		}
	}

	protected abstract boolean isAckMessage(I packet);

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		
		logger.error("{}", getChannelName(), cause);
		
		if (cause instanceof NotSupprtedMessageIdException
				|| cause instanceof InvalidDataSizeException) {
			ctx.channel().close();
		}
	}

    /**
     * 응답이 요구 되는 메시지의 경우 BlockingQueue를 사용하여, 
     * 응답메시지가 오기를 기다린후 응답시간 10000 msec 이 초과했을때 실패로 간주한다.
     * - 응답 메시지는 요청 메시지와 메시지 아이디가 같다.
     * - ACK가 오지 않을 경우 3회 재전송
     */
    @Override
    public boolean sendSyncMessage(O packet, boolean isWaitAck) {
    	if (channel == null || !channel.isActive()) return false;
    	
    	int retryCount = 0;

    	// 3회 재전송
    	while(retryCount < RETRY_COUNT) {
    		logger.debug("retry count : {}, msg : {}", retryCount, packet);
    		if (send(packet, isWaitAck)) {
    			return true;
    		}
    		
    		retryCount++;
    	}
    	return false;
    }

	private boolean send(O packet, boolean isWaitAck) {
		channel.writeAndFlush(packet).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);

		if (isWaitAck) {
			return waitAckMessage(packet);
		}
		return true;
	}

	protected boolean waitAckMessage(O packet) {
		I recvMessage;
		try {
			recvMessage = recvLock.poll(SYNC_MESSAGE_TIMEOUT_SEC, TimeUnit.MILLISECONDS);

			if (recvMessage != null) {
				if (checkAckMessage(packet, recvMessage)) {
					return true;
				}
			}
		} catch (InterruptedException e) {
			logger.warn("{}", e.getMessage());
		}
    	
    	return false;
	}

	protected abstract boolean checkAckMessage(O out, I in);

    /**
     * 비동기로 메시지 전송 (executor의 ThreadPool 이용) 
     * Ack 메시지 사용
     * 
     */
	@Override
	public ListenableFuture<Boolean> sendAsyncMessage(final O packet, final boolean isWaitAck) {
		ListenableFutureTask<Boolean> futureTask = new ListenableFutureTask<>(new Callable<Boolean>() {

			@Override
			public Boolean call() throws Exception {
		    	return sendSyncMessage(packet, isWaitAck);
			}
		});

		executor.execute(futureTask);
    	return futureTask;  	
	}

	@Override
	public boolean isConnected() {
		if (channel != null) {
			return channel.isActive();
		} else {
			return false;
		}
	}	
}
