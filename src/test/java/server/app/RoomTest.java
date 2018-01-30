package server.app;

import org.junit.Test;
import server.session.Session;
import server.session.UserSession;

import static org.junit.Assert.assertEquals;

public class RoomTest {
    @Test
    public void newRoom() {
        Room r = new Room("test");
        assertEquals(r.getId(), "test");
    }

    @Test
    public void connectSession() {
        Room r = new Room("test");
        Session s = new UserSession.UserSessionBuilder().build();
        r.connectSession(s);
    }

    @Test
    public void testNewPlayerArrive() {
        Room r = new Room("test");
        String userId = "nghia";

        r.playerArrive(userId, channel);
    }
}
