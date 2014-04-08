package net.octacomm.sample.service;

import net.octacomm.logger.Log;
import net.octacomm.sample.netty.listener.MessageListener;
import net.octacomm.sample.netty.listener.MessageSender;
import net.octacomm.sample.netty.usn.msg.common.UsnIncomingMessage;
import net.octacomm.sample.netty.usn.msg.common.UsnMessageHelper;
import net.octacomm.sample.netty.usn.msg.common.UsnOutgoingMessage;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
public class UsnMessageProcessor implements MessageListener<UsnIncomingMessage> {

	@Log private Logger logger;
	
	@Autowired
	private MessageSender<UsnOutgoingMessage> usnMessageSender;

	@Scheduled(fixedDelay = 10000, initialDelay = 5000)
	public void messageTest() {
		logger.error("method start");
		boolean result = usnMessageSender.sendSyncMessage(UsnMessageHelper.makeDummyOutgoing(33), true);
		logger.error("sync result : {}", result);
		usnMessageSender.sendAsyncMessage(UsnMessageHelper.makeDummyOutgoing(33), true).addCallback(new ListenableFutureCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {
				logger.error("Async {}", result);
			}

			@Override
			public void onFailure(Throwable t) {
				logger.error("Async {}", t);
			}
		});
		logger.error("method end");
	}
	
	@Override
	public void messageReceived(UsnIncomingMessage packet) {
		switch (packet.getMessageType()) {

			case DUMMY_INCOMING:
				usnMessageSender.sendAsyncMessage(UsnMessageHelper.makeDummyIncomingAck(), false);
					break;
			
			default:
					break;
		}
	}

	@Override
	public void connectionStateChanged(boolean isConnected) {
	}
	
}
