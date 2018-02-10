package server.session;

import io.netty.channel.ChannelHandlerContext;
import server.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class UserSession implements Session{
    public static String USER_ID = "userId";
    public static String DISPLAY_NAME = "displayName";
    protected final String id;
    protected final Map<String, Object> sessionAttributes;
    protected final long createdTime;
    protected ChannelHandlerContext channel;

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
        protected ChannelHandlerContext channel = null;

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

        public UserSessionBuilder channel(ChannelHandlerContext channel) {
            this.channel = channel;
            return this;
        }
    }

    @Override
    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public ChannelHandlerContext getChannel() {
        return channel;
    }

    @Override
    public void setChannel(ChannelHandlerContext channel) {
        this.channel = channel;
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
