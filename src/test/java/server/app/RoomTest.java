package server.app;

import io.netty.channel.Channel;
import org.junit.Before;
import org.junit.Test;
import server.session.Session;
import server.session.UserSession;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

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
        Session s = r.playerArrive(userId, channel);
        assertEquals(s.getAttribute(UserSession.USER_ID), "nghia");
    }

    @Test
    public void testEventHandler() {
        r.playerArrive(userId, channel);


    }
}
