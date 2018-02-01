package server.session;

import io.netty.channel.Channel;
import server.event.EventHandler;

public interface Session {
    String getId();
    void setAttribute(String key, Object value);
    Object getAttribute(String key);
    long getCreatedTime();
    Channel getChannel();

    EventHandler getHandler();
    void setHandler(EventHandler e);
}
