package server.session;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class UserSession implements Session{

    protected final String id;
    protected final Map<String, Object> sessionAttributes;
    protected final long createdTime;

    protected UserSession(UserSessionBuilder builder) {
        builder.setUp();
        this.id = builder.id;
        sessionAttributes = builder.sessionAttributes;
        createdTime = System.currentTimeMillis();
    }

    public static class UserSessionBuilder {
        protected String id = null;
        protected Map<String, Object> sessionAttributes = null;
        public static final AtomicLong ID = new AtomicLong(0l);

        public UserSession build() {
            return new UserSession(this);
        }

        public void setUp() {
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
    }

    @Override
    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setAttribute(String key, Object value) {

    }

    @Override
    public Object getAttribute(String key) {
        return null;
    }

}
