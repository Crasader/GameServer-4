package server.app;

public class RoomFactory {
    public Room createRoom(String roomId) {
        Room r = new Room(roomId);
        return r;
    }
}
