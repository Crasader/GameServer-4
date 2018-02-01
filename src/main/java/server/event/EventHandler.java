package server.event;

import server.session.Session;

public interface EventHandler {
    void onEvent(EventType event, Object e);
    void setSession(Session s);
}
