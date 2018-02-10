package server.session;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import server.event.EventHandler;

public interface Session {
    String getId();
    void setAttribute(String key, Object value);
    Object getAttribute(String key);
    long getCreatedTime();
    ChannelHandlerContext getChannel();
    void setChannel(ChannelHandlerContext channel);
}
