package server.app;

import server.session.Session;

import java.util.HashSet;
import java.util.Set;

public class Room {
    private Set<Session> playerSessions;

    public Room() {
        playerSessions = new HashSet<>();
    }

    public synchronized boolean connectSession(Session session){
        playerSessions.add(session);
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;


}
