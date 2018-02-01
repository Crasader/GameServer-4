package server.app;

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
    private String userId;

    @Before
    public void setUp() throws Exception {
        r = new Room("test");
        Channel channel = mock(Channel.class);
        userId = "nghia";
    }

    @Test
    public void newRoom() {
        assertEquals(r.getId(), "test");
        r.setId("abc");
        assertEquals(r.getId(), "abc");
    }


    @Test
    public void testNewPlayerArrive() {
        Session s = r.playerArrive(userId, channel, mock(EventHandler.class));
        assertEquals(s.getAttribute(UserSession.USER_ID), "nghia");
    }

    @Test
    public void testEventHandler() {
        EventHandler fakeHandler = mock(EventHandler.class);
        Session s = r.playerArrive(userId, channel, fakeHandler);
        s.setHandler(fakeHandler);
        Event e = new Event(EventType.NEW_PLAYER_ARRIVE);

        String userId2 = "userId2";
        Channel channel2 = mock(Channel.class);
        Session s2 = r.playerArrive(userId2, channel2, fakeHandler);
        EventHandler fakeHandler2 = mock(EventHandler.class);
        s2.setHandler(fakeHandler2);

        verify(fakeHandler).onEvent(EventType.NEW_PLAYER_ARRIVE, s2);
    }
}
