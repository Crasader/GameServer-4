package server.event.impl;

import server.event.EventHandler;
import server.event.EventType;
import server.session.Session;

public class EventHandlerFactory {
    public EventHandler buildEventHandler(EventType eventType, Session s) {
        switch (eventType) {
            case NEW_PLAYER_ARRIVE:
                return new PlayerArriveHandler(s);
            case PLAYER_LEFT:
                return new PlayerLeftHandler(s);
        }
        return null;
    }
}
