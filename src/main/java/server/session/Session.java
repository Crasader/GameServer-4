package server.session;

import io.netty.channel.Channel;

public interface Session {
    String getId();
    void setAttribute(String key, Object value);
    Object getAttribute(String key);
    long getCreatedTime();
    Channel getChannel();
}
