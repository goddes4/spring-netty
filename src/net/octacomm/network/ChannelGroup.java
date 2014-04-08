package net.octacomm.network;

import io.netty.channel.Channel;

/**
 * Netty를 통해서 접속된 채널의 리스트를 관리하고,
 * 해당 채널들에게 메시지를 전송할 때 사용한다.
 * 
 * @author tykim
 */
public interface ChannelGroup {

    void addChannel(Channel channel);

    void addChannel(String key, Channel channel);

    void removeChannel(Channel channel);

    void removeChannel(String key);
    
    void removeAllChannels();

    int getChannelSize();

    void write(String ip, Object msg);

    void writeToAll(Object msg);
}
