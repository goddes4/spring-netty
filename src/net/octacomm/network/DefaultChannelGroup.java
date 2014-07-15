package net.octacomm.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty를 통해서 접속된 채널의 리스트를 관리하고,
 * 해당 채널들에게 메시지를 전송할 때 사용한다.
 *
 * @author tykim
 */
public class DefaultChannelGroup implements ChannelGroup {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    // MapKey : "/192.168.0.101:8080"
    protected ConcurrentMap<String, Channel> allChannels = new ConcurrentHashMap<String, Channel>();
    
    @Override
    public void addChannel(Channel channel) {
        String key = makeMapKey(channel.remoteAddress());
        addChannel(key, channel);
    }

    @Override
    public void addChannel(String key, Channel channel) {
        allChannels.put(key, channel);
        logger.info("addChannel {}", allChannels);
    }

    @Override
    public void removeChannel(Channel channel) {
        String key = makeMapKey(channel.remoteAddress());
        removeChannel(key);
    }
    
    @Override
    public void removeChannel(String key) {
        allChannels.remove(key);
        logger.info("removeChannel {}", allChannels);
    }

    @Override
    public void removeAllChannels() {
    	for (Channel channel : allChannels.values()) {
    		channel.close();
    	}
    	allChannels.clear();
    }

    /**
     * 
     * @param address
     * @return "/192.168.0.101:8080"
     */
    private String makeMapKey(SocketAddress address) {
        return address.toString();
    }
    
    @Override
    public int getChannelSize() {
        return allChannels.size();
    }

    /**
     * 파라메터 ip가 "/192.168.0.101:8080"이면 해당 채널에 전송한다.
     * 
     * @param ip
     * @param msg 
     */
    @Override
    public void write(String ip, Object msg) {
        Channel channel = allChannels.get(ip);
        if (channel != null) {
            logger.debug("{} Send Message : {}", channel.remoteAddress(), msg);
            channel.writeAndFlush(msg).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    @Override
    public void writeToAll(Object msg) {
        for (Channel channel : allChannels.values()) {
            logger.debug("{} Send Message : {}", channel.remoteAddress(), msg);
            channel.writeAndFlush(msg).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        }
    }

    @Override
    public String toString() {
        return "DefaultChannelGroup [" + "clientChannels=" + allChannels + "]";
    }
}
