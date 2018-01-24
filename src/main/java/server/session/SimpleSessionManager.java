package server.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleSessionManager implements SessionManager
{
    protected final Map<String, Session> sessions;

    public SimpleSessionManager()
    {
        sessions = new ConcurrentHashMap<String, Session>(1000);
    }

    @Override
    public Session get(String key)
    {
        return sessions.get(key);
    }

    @Override
    public boolean put(String key, Session session)
    {
        assert key!=null && session!=null;
        return sessions.put(key, session) == null;
    }

    @Override
    public void remove(String key) {
        sessions.remove(key);
    }
}

