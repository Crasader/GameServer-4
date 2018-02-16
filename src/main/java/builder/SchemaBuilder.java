package builder;

import schema.*;
import com.google.flatbuffers.FlatBufferBuilder;
import server.app.PlayerManager;
import server.app.Room;
import server.session.Session;
import server.session.UserSession;

import java.util.HashMap;
import java.util.Map;

public class SchemaBuilder {

    private static final Map<Byte, String> errorMessages;
    static
    {
        errorMessages = new HashMap<>();
        errorMessages.put(ErrorCode.INVALID_AUTH, "INVALID LOGIN");
        errorMessages.put(ErrorCode.ROOM_NOT_FOUND, "Not able to find a room");
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
        int displayName = builder.createString((String)s.getAttribute(UserSession.DISPLAY_NAME));
        PlayerInfo.startPlayerInfo(builder);
        PlayerInfo.addUserId(builder, userId);
        PlayerInfo.addName(builder, displayName);
        return PlayerInfo.endPlayerInfo(builder);
    }

    public static FlatBufferBuilder buildPlayerArrive(Session s) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1);
        int playerInfo = createPlayerInfo(builder, s);
        PlayerUpdate.startPlayerUpdate(builder);
        PlayerUpdate.addStatus(builder, PlayerStatus.Arrive);
        PlayerUpdate.addPlayer(builder, playerInfo);
        int playerArrive = PlayerUpdate.endPlayerUpdate(builder);
        return buildMessage(builder, playerArrive, Data.PlayerUpdate);
    }

    public static FlatBufferBuilder buildPlayerLeft(Session s) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1);
        int playerInfo = createPlayerInfo(builder, s);
        PlayerUpdate.startPlayerUpdate(builder);
        PlayerUpdate.addStatus(builder, PlayerStatus.Left);
        PlayerUpdate.addPlayer(builder, playerInfo);
        int playerArrive = PlayerUpdate.endPlayerUpdate(builder);
        return buildMessage(builder, playerArrive, Data.PlayerUpdate);
    }

    public static FlatBufferBuilder buildRoomInfo(Room r) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1);
        int len = r.getPlayers().size();
        int[] players = new int[len];
        int i = 0;
        for(PlayerManager s: r.getPlayers()) {
            int userId = builder.createString((String)s.getSession().getAttribute(UserSession.USER_ID));
            int displayName = builder.createString((String)s.getSession().getAttribute(UserSession.DISPLAY_NAME));
            players[i++] = PlayerInfo.createPlayerInfo(builder, userId, displayName);
        }
        int allPlayers = RoomInfo.createPlayersVector(builder, players);
        RoomInfo.startRoomInfo(builder);
        RoomInfo.addPlayers(builder, allPlayers);
        int roomInfo = RoomInfo.endRoomInfo(builder);
        return buildMessage(builder, roomInfo, Data.RoomInfo);
    }

    public static FlatBufferBuilder buildJoinCommand(String roomId, String authToken) {
        FlatBufferBuilder builder = new FlatBufferBuilder(1);
        int authTokenInt = builder.createString(authToken);
        int roomIdInt = builder.createString(roomId);
        JoinRoomCommand.startJoinRoomCommand(builder);
        JoinRoomCommand.addRoomId(builder, roomIdInt);
        JoinRoomCommand.addToken(builder, authTokenInt);
        int joinRoomCmd = JoinRoomCommand.endJoinRoomCommand(builder);
        return buildMessage(builder, joinRoomCmd, Data.JoinRoomCommand);
    }

    public static FlatBufferBuilder buildErrorMessage(byte errorCode) {
        String errorMessage = errorMessages.getOrDefault(errorCode, "Internal error!");
        FlatBufferBuilder builder = new FlatBufferBuilder(1);
        int errorMsg = builder.createString(errorMessage);
        ErrorMessage.startErrorMessage(builder);
        ErrorMessage.addCode(builder, errorCode);
        ErrorMessage.addError(builder, errorMsg);
        int errorInt = ErrorMessage.endErrorMessage(builder);
        return buildMessage(builder, errorInt, Data.ErrorMessage);
    }
}
