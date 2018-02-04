package builder;

import schema.*;
import com.google.flatbuffers.FlatBufferBuilder;
import server.app.Room;
import server.session.Session;
import server.session.UserSession;

public class SchemaBuilder {
    public static FlatBufferBuilder buildCredentialToken(String token) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1);
        int tokenId = builder.createString(token);
        CredentialToken.startCredentialToken(builder);
        CredentialToken.addToken(builder, tokenId);
        int cred = CredentialToken.endCredentialToken(builder);
        return buildMessage(builder, cred, Data.CredentialToken);
    }

    public static FlatBufferBuilder buildReconnectKey(String authKey) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1);
        int  key = builder.createString(authKey);
        ReconnectKey.startReconnectKey(builder);
        ReconnectKey.addKey(builder, key);
        int reconnectKey = ReconnectKey.endReconnectKey(builder);
        return buildMessage(builder, reconnectKey, Data.ReconnectKey);
    }

    public static FlatBufferBuilder buildMessage(FlatBufferBuilder builder, int data, byte dataType) {
        Message.startMessage(builder);
        Message.addDataType(builder, dataType);
        Message.addData(builder, data);
        int finalData = Message.endMessage(builder);
        builder.finish(finalData);
        return builder;
    }

    public static int createPlayerInfo(FlatBufferBuilder builder, Session s) {
        int userId = builder.createString((String)s.getAttribute(UserSession.USER_ID));
        PlayerInfo.startPlayerInfo(builder);
        PlayerInfo.addUserId(builder, userId);
        return PlayerInfo.endPlayerInfo(builder);
    }

    public static FlatBufferBuilder buildPlayer(Session s) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1);
        return buildMessage(builder, createPlayerInfo(builder, s), Data.PlayerInfo);
    }

    public static FlatBufferBuilder buildRoomInfo(Room r) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1);
        int len = r.getPlayerSessions().size();
        int[] players = new int[len];
        for(int i = 0; i < len; ++i) {
            Session s = r.getPlayerSessions().get(i);
            int userId = builder.createString((String)s.getAttribute(UserSession.USER_ID));
            int displayName = builder.createString((String)s.getAttribute(UserSession.DISPLAY_NAME));
            players[i] = PlayerInfo.createPlayerInfo(builder, userId, displayName);
        }
        int allPlayers = RoomInfo.createPlayersVector(builder, players);
        RoomInfo.startRoomInfo(builder);
        RoomInfo.addPlayers(builder, allPlayers);
        int roomInfo = RoomInfo.endRoomInfo(builder);
        return buildMessage(builder, roomInfo, Data.RoomInfo);
    }
}
