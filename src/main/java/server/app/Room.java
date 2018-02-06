package server.app;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import info.UserInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.event.Event;
import server.event.EventDispatcher;
import server.event.EventHandler;
import server.event.EventType;
import server.event.impl.SessionEventHandler;
import server.session.Session;
import server.session.UserSession;

import java.util.*;

public class Room {
    private static final Logger LOG = LoggerFactory.getLogger(Room.class);

    private List<Session> playerSessions;
    public List<Session> getPlayerSessions() {
        return playerSessions;
    }
    public void setPlayerSessions(List<Session> playerSessions) {
        this.playerSessions = playerSessions;
    }


    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    private EventDispatcher eventDispatcher;

    public Room(String id) {
        this.id = id;
        playerSessions = new LinkedList<>();
        eventDispatcher = new EventDispatcher();
    }

    public synchronized Session playerArrive(UserInfo userInfo, ChannelHandlerContext channel, EventHandler sessionHandler) {
        //Check for existing session
        for(Session s: playerSessions) {
            if (s.getAttribute(UserSession.USER_ID).equals(userInfo.getUserId())) {
                s.setChannel(channel);
                return s;
            }
        }
        //New session
        UserSession.UserSessionBuilder sessionBuilder = new UserSession.UserSessionBuilder();
        Map<String, Object> attr = new HashMap<>();
        attr.put(UserSession.USER_ID, userInfo.getUserId());
        attr.put(UserSession.DISPLAY_NAME, userInfo.getDisplayName());
        Session newSession = sessionBuilder.sessionAttributes(attr).channel(channel).build();
        newSession.setHandler(sessionHandler);
        sessionHandler.setSession(newSession);
        playerSessions.add(newSession);

        LOG.info("New player : " + userInfo.toString());
        eventDispatcher.dispatchEvent(new Event(EventType.NEW_PLAYER_ARRIVE), newSession);
        eventDispatcher.addListener(EventType.NEW_PLAYER_ARRIVE, sessionHandler);

        //Debug
        for(Session s:playerSessions) {
            LOG.info("Player in the room : " + s.getAttribute(UserSession.USER_ID));
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
