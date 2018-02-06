package server.app;

import info.UserInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Before;
import org.junit.Test;
import server.event.Event;
import server.event.EventHandler;
import server.event.EventType;
import server.session.Session;
import server.session.UserSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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
        Session s = r.playerArrive(userInfo, channel, mock(EventHandler.class));
        assertEquals(s.getAttribute(UserSession.USER_ID), "nghia");
    }

    @Test
    public void testCheckExistingSession() {
        Session s = r.playerArrive(userInfo, channel, mock(EventHandler.class));
        ChannelHandlerContext channel1 = mock(ChannelHandlerContext.class);
        Session s1 = r.playerArrive(userInfo, channel1, mock(EventHandler.class));

        assertEquals(s1, s);
        assertEquals(s1.getChannel(), channel1);
    }

    @Test
    public void testEventHandler() {
        EventHandler fakeHandler = mock(EventHandler.class);
        Session s = r.playerArrive(userInfo, channel, fakeHandler);
        s.setHandler(fakeHandler);
        Event e = new Event(EventType.NEW_PLAYER_ARRIVE);

        UserInfo userInfo2 = new UserInfo();
        userInfo2.setUserId("my");
        userInfo2.setDisplayName("my pham");
        ChannelHandlerContext channel2 = mock(ChannelHandlerContext.class);
        Session s2 = r.playerArrive(userInfo2, channel2, fakeHandler);
        EventHandler fakeHandler2 = mock(EventHandler.class);
        s2.setHandler(fakeHandler2);

        verify(fakeHandler).onEvent(EventType.NEW_PLAYER_ARRIVE, s2);
    }
}
