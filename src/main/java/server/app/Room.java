package server.app;

import info.UserInfo;
import io.netty.channel.ChannelHandlerContext;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.event.Event;
import server.event.EventDispatcher;
import server.event.EventHandler;
import server.event.EventType;
import server.event.impl.EventHandlerFactory;
import server.session.Session;
import server.session.UserSession;

import java.util.*;

public class Room {
    private static final Logger LOG = LoggerFactory.getLogger(Room.class);

    List<EventType> eventTypes = new ArrayList<EventType>() {
        {
            add(EventType.NEW_PLAYER_ARRIVE);
            add(EventType.PLAYER_LEFT);
        }
    };

    private Map<Session, List<Pair<EventType,EventHandler>> > playerSessions;
    public Map<Session, List<Pair<EventType, EventHandler>> > getPlayerSessions() {
        return playerSessions;
    }
    public void setPlayerSessions(Map<Session, List<Pair<EventType, EventHandler>> > playerSessions) {
        this.playerSessions = playerSessions;
    }


    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    private EventDispatcher eventDispatcher;

    public Room(String id) {
        this.id = id;
        playerSessions = new HashMap<>();
        eventDispatcher = new EventDispatcher();
    }

    private Session getPlayerSession(String userId) {
        for(Session s: playerSessions.keySet()) {
            if (s.getAttribute(UserSession.USER_ID).equals(userId)) {
                return s;
            }
        }
        return null;
    }

    public synchronized void playerLeave(String userId) {
        Session s = getPlayerSession(userId);
        if (s == null) {
            LOG.error("Player leave the room but not able to find player session, userId:" + userId);
            return;
        }
        List<Pair<EventType, EventHandler> > eventHandlers = playerSessions.getOrDefault(s, null);
        if (eventHandlers != null) {
            for(Pair<EventType, EventHandler> pair: eventHandlers) {
                eventDispatcher.removeListener(pair.getKey(), pair.getValue());
            }
            playerSessions.remove(s);
            LOG.info("Player " + userId + " leaving the room: " + this.id);
        } else {
            LOG.error("Not able to find session in playerSession, userId=" + s.getAttribute(UserSession.USER_ID));
        }
        eventDispatcher.dispatchEvent(new Event(EventType.PLAYER_LEFT), s);
    }

    public synchronized Session playerArrive(
            UserInfo userInfo, ChannelHandlerContext channel, EventHandlerFactory eventHandlerFactory) {
        Session s = getPlayerSession(userInfo.getUserId());
        if (s != null) {
            s.setChannel(channel);
            return s;
        }

        //New session
        UserSession.UserSessionBuilder sessionBuilder = new UserSession.UserSessionBuilder();
        Map<String, Object> attr = new HashMap<>();
        attr.put(UserSession.USER_ID, userInfo.getUserId());
        attr.put(UserSession.DISPLAY_NAME, userInfo.getDisplayName());
        Session newSession = sessionBuilder.sessionAttributes(attr).channel(channel).build();
        List<Pair<EventType, EventHandler>> eventHandlers = new ArrayList<>();
        for(EventType eventType: eventTypes) {
            EventHandler eventHandler = eventHandlerFactory.buildEventHandler(eventType, newSession);
            eventHandlers.add(new Pair<EventType, EventHandler>(eventType, eventHandler));
        }
        playerSessions.put(newSession, eventHandlers);
        LOG.info("New player : " + userInfo.toString());
        eventDispatcher.dispatchEvent(new Event(EventType.NEW_PLAYER_ARRIVE), newSession);
        for(Pair<EventType, EventHandler> pair: eventHandlers) {
            eventDispatcher.addListener(pair.getKey(), pair.getValue());
        }
        //Debug
        for(Session s1:playerSessions.keySet()) {
            LOG.info("Player info   :" + s1.getAttribute(UserSession.USER_ID));
        }
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
