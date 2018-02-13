package builder;

import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.buffer.ByteBuf;
import javafx.util.Pair;
import org.junit.Test;
import schema.*;
import server.app.PlayerManager;
import server.app.Room;
import server.event.EventHandler;
import server.event.EventType;
import server.session.Session;
import server.session.UserSession;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class SchemaBuilderTest {

    private Map<String, Object> buildAttr(String userId, String displayName) {
        Map<String, Object> attr = new HashMap<>();
        attr.put(UserSession.USER_ID, userId);
        attr.put(UserSession.DISPLAY_NAME, displayName);
        return attr;
    }
    @Test
    public void testBuildPlayerArrive() {
        Map<String, Object> attr = buildAttr("userId", "nghia");
        Session s = new UserSession.UserSessionBuilder().sessionAttributes(attr).build();
        FlatBufferBuilder builder = SchemaBuilder.buildPlayerArrive(s);

        ByteBuffer byteBuffer = ByteBuffer.wrap(builder.sizedByteArray());
        Message msg = Message.getRootAsMessage(byteBuffer);
        assertEquals(msg.dataType(), Data.PlayerUpdate);
        PlayerUpdate player = (PlayerUpdate)msg.data(new PlayerUpdate());
        assertEquals(player.player().userId(), "userId");
        assertEquals(player.player().name(), "nghia");
        assertEquals(player.status(), PlayerStatus.Arrive);
    }

    @Test
    public void testBuildPlayerLeft() {
        Map<String, Object> attr = buildAttr("userId", "nghia");
        Session s = new UserSession.UserSessionBuilder().sessionAttributes(attr).build();
        FlatBufferBuilder builder = SchemaBuilder.buildPlayerLeft(s);

        ByteBuffer byteBuffer = ByteBuffer.wrap(builder.sizedByteArray());
        Message msg = Message.getRootAsMessage(byteBuffer);
        assertEquals(msg.dataType(), Data.PlayerUpdate);
        PlayerUpdate player = (PlayerUpdate)msg.data(new PlayerUpdate());
        assertEquals(player.player().userId(), "userId");
        assertEquals(player.player().name(), "nghia");
        assertEquals(player.status(), PlayerStatus.Left);
    }

    @Test
    public void testBuildRoomInfo() {
        Map<String, Object> attr = buildAttr("userId1", "nghia1");
        Session s = new UserSession.UserSessionBuilder().sessionAttributes(attr).build();

        Map<String, Object> attr2 = buildAttr("userId2", "nghia2");
        Session s2 = new UserSession.UserSessionBuilder().sessionAttributes(attr2).build();

        Room r = new Room("Singapore");
        List<PlayerManager>  players = new ArrayList<>();
        players.add(new PlayerManager(s, new ArrayList<>()));
        players.add(new PlayerManager(s2, new ArrayList<>()));
        r.setPlayers(players);
        FlatBufferBuilder builder = SchemaBuilder.buildRoomInfo(r);
        ByteBuffer byteBuffer = ByteBuffer.wrap(builder.sizedByteArray());
        Message msg = Message.getRootAsMessage(byteBuffer);
        assertEquals(msg.dataType(), Data.RoomInfo);
        RoomInfo room = (RoomInfo)msg.data(new RoomInfo());
        assertEquals(room.playersLength(), 2);
        PlayerInfo player = room.players(0);
        assertEquals(player.userId(), "userId1");
        assertEquals(player.name(), "nghia1");
        PlayerInfo player2 = room.players(1);
        assertEquals(player2.userId(), "userId2");
        assertEquals(player2.name(), "nghia2");
    }
}
