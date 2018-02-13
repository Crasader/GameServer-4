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

    private List<PlayerManager> players;

    public List<PlayerManager> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerManager> players) {
        this.players = players;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    private EventDispatcher eventDispatcher;

    public Room(String id) {
        this.id = id;
        players = new ArrayList<>();
        eventDispatcher = new EventDispatcher();
    }

    private PlayerManager getPlayer(String userId) {
        for(PlayerManager player: players) {
            if (player.getSession().getAttribute(UserSession.USER_ID).equals(userId)) {
                return player;
            }
        }
        return null;
    }

    public synchronized void playerLeave(String userId) {
        PlayerManager player = getPlayer(userId);
        if (player == null) {
            LOG.error("Player leave the room but not able to find player session, userId:" + userId);
            return;
        }
        List<Pair<EventType, EventHandler> > eventHandlers = player.getEventHandlers();
        if (eventHandlers != null) {
            for(Pair<EventType, EventHandler> pair: eventHandlers) {
                eventDispatcher.removeListener(pair.getKey(), pair.getValue());
            }
            players.remove(player);
            LOG.info("Player " + userId + " leaving the room: " + this.id);
        } else {
            LOG.error("Not able to find session in playerSession, userId=" + player.getSession().getAttribute(UserSession.USER_ID));
        }
        eventDispatcher.dispatchEvent(new Event(EventType.PLAYER_LEFT), player.getSession());
    }

    public synchronized Session playerArrive(
            UserInfo userInfo, ChannelHandlerContext channel, EventHandlerFactory eventHandlerFactory) {
        PlayerManager s = getPlayer(userInfo.getUserId());
        if (s != null) {
            s.getSession().setChannel(channel);
            return s.getSession();
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
        PlayerManager newPlayer = new PlayerManager(newSession, eventHandlers);
        players.add(newPlayer);
        LOG.info("New player : " + userInfo.toString());
        eventDispatcher.dispatchEvent(new Event(EventType.NEW_PLAYER_ARRIVE), newSession);
        for(Pair<EventType, EventHandler> pair: eventHandlers) {
            eventDispatcher.addListener(pair.getKey(), pair.getValue());
        }
        //Debug
        for(PlayerManager player:players) {
            LOG.info("Player info   :" + player.getSession().getAttribute(UserSession.USER_ID));
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
