package builder;

import com.google.flatbuffers.FlatBufferBuilder;
import org.junit.Test;
import schema.Data;
import schema.Message;
import schema.PlayerInfo;
import server.session.Session;
import server.session.UserSession;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;

public class SchemaBuilderTest {

    @Test
    public void testBuildPlayer() {
        Map<String, Object> attr = new HashMap<>();
        attr.put(UserSession.USER_ID, "userId");
        Session s = new UserSession.UserSessionBuilder().sessionAttributes(attr).build();
        FlatBufferBuilder builder = SchemaBuilder.buildPlayer(s);

        ByteBuffer byteBuffer = ByteBuffer.wrap(builder.sizedByteArray());
        Message msg = Message.getRootAsMessage(byteBuffer);
        assertEquals(msg.dataType(), Data.PlayerInfo);
        PlayerInfo player = (PlayerInfo)msg.data(new PlayerInfo());
        assertEquals(player.userId(), "userId");
    }
}
