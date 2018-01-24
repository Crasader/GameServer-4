package server.session;

public interface SessionManager {
    public Session get(String key);
    public boolean put(String key, Session session);
    public void remove(String key);
}
