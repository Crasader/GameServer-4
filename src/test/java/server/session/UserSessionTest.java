package server.session;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserSessionTest {

    @Test
    public void idChangeOverTime() {
        Session u1 = new UserSession.UserSessionBuilder().build();
        Session u2 = new UserSession.UserSessionBuilder().build();
        assertNotEquals(u1.getId(), u2.getId());
    }

    @Test
    public void createdTimeIsUpdated() {
        Session u1 = new UserSession.UserSessionBuilder().build();
        assertTrue(u1.getCreatedTime() >= System.currentTimeMillis());
    }
}