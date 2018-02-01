package server.app;

import io.netty.channel.Channel;
import server.event.Event;
import server.event.EventDispatcher;
import server.event.EventHandler;
import server.event.EventType;
import server.session.Session;
import server.session.UserSession;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Room {
    private Set<Session> playerSessions;

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    private EventDispatcher eventDispatcher;

    public Room(String id) {
        this.id = id;
        playerSessions = new HashSet<>();
        eventDispatcher = new EventDispatcher();
    }

    public synchronized Session playerArrive(String userId, Channel channel) {
        //New session
        UserSession.UserSessionBuilder sessionBuilder = new UserSession.UserSessionBuilder();
        Map<String, Object> attr = new HashMap<>();
        attr.put(UserSession.USER_ID, userId);
        Session newSession = sessionBuilder.sessionAttributes(attr).channel(channel).build();
        playerSessions.add(newSession);

        eventDispatcher.dispatchEvent(new Event(EventType.NEW_PLAYER_ARRIVE), newSession);
        eventDispatcher.addListener(EventType.NEW_PLAYER_ARRIVE, newSession.getHandler());
        return newSession;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
}
