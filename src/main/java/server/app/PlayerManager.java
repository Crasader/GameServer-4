package server.app;

import javafx.util.Pair;
import server.event.EventHandler;
import server.event.EventType;
import server.session.Session;

import java.util.List;

public class PlayerManager {
    private Session session;
    private List<Pair<EventType,EventHandler>> eventHandlers;

    public PlayerManager(Session s, List<Pair<EventType,EventHandler>>  eventHandlers){
        this.session = s;
        this.eventHandlers = eventHandlers;
    }

    public Session getSession() {
        return session;
    }

    public List<Pair<EventType, EventHandler>> getEventHandlers() {
        return eventHandlers;
    }
}
