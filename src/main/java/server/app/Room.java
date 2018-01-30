package server.app;

import server.session.Session;
import server.session.UserSession;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Room {
    private Set<Session> playerSessions;

    public Room(String id) {
        this.id = id;
        playerSessions = new HashSet<>();
    }

    public synchronized Session playerArrive(String userId) {
        //New session
        UserSession.UserSessionBuilder sessionBuilder = new UserSession.UserSessionBuilder();
        Map<String, Object> attr = new HashMap<>();
        attr.put(UserSession.USER_ID, userId);
        Session newSession = sessionBuilder.sessionAttributes(attr).build();
        this.connectSession(newSession);
        return newSession;
    }

    public synchronized boolean connectSession(Session session) {
        playerSessions.add(session);
        return true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
}
