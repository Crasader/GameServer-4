package server.session;

import io.netty.channel.Channel;
import server.event.EventHandler;
import server.event.impl.SessionEventHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class UserSession implements Session{

    public static String USER_ID = "userId";
    protected final String id;
    protected final Map<String, Object> sessionAttributes;
    protected final long createdTime;
    protected final Channel channel;

    protected EventHandler handler;

    protected UserSession(UserSessionBuilder builder) {
        builder.setUp(this);
        this.id = builder.id;
        this.channel = builder.channel;
        sessionAttributes = builder.sessionAttributes;
        createdTime = System.currentTimeMillis();
    }

    public static class UserSessionBuilder {
        protected String id = null;
        protected Map<String, Object> sessionAttributes = null;
        public static final AtomicLong ID = new AtomicLong(0l);
        protected Channel channel = null;

        public UserSession build() {
            return new UserSession(this);
        }

        public void setUp(Session self) {
            // TODO : probably need to attach hosted server in the ID to support multiple server cases.
            if (this.id == null) {
                this.id = String.valueOf(ID.incrementAndGet());
            }
            if (this.sessionAttributes == null) {
                this.sessionAttributes = new HashMap<String, Object>();
            }
        }

        public UserSessionBuilder sessionAttributes(final Map<String, Object> sessionAttributes)
        {
            this.sessionAttributes = sessionAttributes;
            return this;
        }

        public UserSessionBuilder channel(Channel channel) {
            this.channel = channel;
            return this;
        }
    }

    @Override
    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public EventHandler getHandler() {
        return this.handler;
    }

    public void setHandler(EventHandler handler) {
        this.handler = handler;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setAttribute(String key, Object value) {
        sessionAttributes.put(key, value);

    }

    @Override
    public Object getAttribute(String key) {
        return sessionAttributes.get(key);
    }
}
