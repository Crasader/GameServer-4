package server.app;

import java.util.HashMap;
import java.util.Map;

public class RoomManager {

    private Map<String, Room> roomMap;
    public RoomManager() {
        roomMap = new HashMap<>();
    }

    public synchronized void addRoom(Room room) {
        roomMap.put(room.getId(), room);
    }

    public synchronized Room getRoom(String roomId) {
        return roomMap.get(roomId);
    }

}
