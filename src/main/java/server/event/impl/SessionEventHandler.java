package server.event.impl;

import org.w3c.dom.events.Event;
import server.event.EventHandler;
import server.event.EventType;
import server.session.Session;

public class SessionEventHandler implements EventHandler {

    private Session session;
    public SessionEventHandler() {
    }

    @Override
    public void onEvent(EventType event, Object e) {

    }

    @Override
    public void setSession(Session s) {
        this.session = s;
    }
}
