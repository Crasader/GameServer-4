package server.session;

public interface Session {
    String getId();
    void setAttribute(String key, Object value);
    Object getAttribute(String key);
    long getCreatedTime();
}
