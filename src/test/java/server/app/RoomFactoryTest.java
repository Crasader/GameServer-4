package server.app;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RoomFactoryTest {
    @Test
    public void createRoom() {
        RoomFactory f = new RoomFactory();
        Room r = f.createRoom("test");
        assertEquals(r.getId(), "test");
    }
}
