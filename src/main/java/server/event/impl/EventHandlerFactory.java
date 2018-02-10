package server.event.impl;

import server.event.EventHandler;
import server.event.EventType;

public class EventHandlerFactory {
    public EventHandler buildEventHandler(EventType eventType) {
        switch (eventType) {
            case NEW_PLAYER_ARRIVE:
                return new PlayerArriveHandler();
            case PLAYER_LEFT:
                return new PlayerLeftHandler();
        }
        return null;
    }
}
