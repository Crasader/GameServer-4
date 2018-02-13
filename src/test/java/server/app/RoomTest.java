package server.app;

import info.UserInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.Test;
import server.event.Event;
import server.event.EventHandler;
import server.event.EventType;
import server.event.impl.EventHandlerFactory;
import server.session.Session;
import server.session.UserSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RoomTest {
    private Room r;
    private ChannelHandlerContext channel;
    private UserInfo userInfo;

    @Before
    public void setUp() throws Exception {
        r = new Room("test");
        channel = mock(ChannelHandlerContext.class);
        userInfo = new UserInfo();
        userInfo.setUserId("nghia");
        userInfo.setDisplayName("nghia nguyen");
    }

    @Test
    public void newRoom() {
        assertEquals(r.getId(), "test");
        r.setId("abc");
        assertEquals(r.getId(), "abc");
    }


    @Test
    public void testNewPlayerArrive() {
        EventHandlerFactory mockFactory = mock(EventHandlerFactory.class);
        Session s = r.playerArrive(userInfo, channel, mockFactory);
        assertEquals(s.getAttribute(UserSession.USER_ID), "nghia");
    }

    @Test
    public void testCheckExistingSession() {
        Session s = r.playerArrive(userInfo, channel, mock(EventHandlerFactory.class));
        ChannelHandlerContext channel1 = mock(ChannelHandlerContext.class);
        Session s1 = r.playerArrive(userInfo, channel1, mock(EventHandlerFactory.class));

        assertEquals(s1, s);
        assertEquals(s1.getChannel(), channel1);
    }

    @Test
    public void testEventHandler() {
        EventHandlerFactory mockFactory = mock(EventHandlerFactory.class);
        EventHandler mockPlayerArriveHandler = mock(EventHandler.class);
        when(mockFactory.buildEventHandler(eq(EventType.NEW_PLAYER_ARRIVE), anyObject()))
                .thenReturn(mockPlayerArriveHandler);
        when(mockFactory.buildEventHandler(eq(EventType.PLAYER_LEFT), anyObject()))
                .thenReturn(mock(EventHandler.class));
        EventHandler fakeHandler = mock(EventHandler.class);
        Session s = r.playerArrive(userInfo, channel, mockFactory);
        Event e = new Event(EventType.NEW_PLAYER_ARRIVE);

        UserInfo userInfo2 = new UserInfo();
        userInfo2.setUserId("my");
        userInfo2.setDisplayName("my pham");
        ChannelHandlerContext channel2 = mock(ChannelHandlerContext.class);
        Session s2 = r.playerArrive(userInfo2, channel2, mock(EventHandlerFactory.class));
        EventHandler fakeHandler2 = mock(EventHandler.class);

        verify(mockPlayerArriveHandler).onEvent(EventType.NEW_PLAYER_ARRIVE, s2);
    }
}
