package server.app;

import info.UserInfo;
import io.netty.channel.Channel;
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
    private Channel channel;
    private UserInfo userInfo;

    @Before
    public void setUp() throws Exception {
        r = new Room("test");
        Channel channel = mock(Channel.class);
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
    public void testEventHandler() {
        EventHandler fakeHandler = mock(EventHandler.class);
        Session s = r.playerArrive(userInfo, channel, fakeHandler);
        s.setHandler(fakeHandler);
        Event e = new Event(EventType.NEW_PLAYER_ARRIVE);

        UserInfo userInfo2 = new UserInfo();
        userInfo2.setUserId("my");
        userInfo2.setDisplayName("my pham");
        Channel channel2 = mock(Channel.class);
        Session s2 = r.playerArrive(userInfo2, channel2, fakeHandler);
        EventHandler fakeHandler2 = mock(EventHandler.class);
        s2.setHandler(fakeHandler2);

        verify(fakeHandler).onEvent(EventType.NEW_PLAYER_ARRIVE, s2);
    }
}
