package server.app;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RoomManagerTest {
    @Test
    public void addRoomTest() {
        RoomManager mrg = new RoomManager();
        Room r = new RoomFactory().createRoom("singapore");
        mrg.addRoom(r);
        assertEquals(mrg.getRoom("singapore"), r);
    }
}
