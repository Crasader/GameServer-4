package util;

import schema.*;

public class DebugUtil {

    public static String toString(Message m) {
        String res = "DataType=" + Data.name(m.dataType()) + "->";
        if (m.dataType() == Data.RoomInfo) {
            res += toString((RoomInfo)m.data(new RoomInfo()));
        } else if (m.dataType() == Data.PlayerUpdate) {
            res += toString((PlayerUpdate) m.data(new PlayerUpdate()));
        } else if (m.dataType() == Data.ErrorMessage) {
            res += toString((ErrorMessage)m.data(new ErrorMessage()));
        }
        return res;
    }

    public static String toString(RoomInfo roomInfo) {
        String res = "";
        for(int i = 0; i < roomInfo.playersLength(); ++i) {
            res += "Player " + i + " : ";
            res += toString(roomInfo.players(i));
            res += "\n";
        }
        return res;
    }

    public static String toString(PlayerInfo playerInfo) {
        return "UserId=" + playerInfo.userId() + ", Name=" + playerInfo.name();
    }

    public static String toString(PlayerUpdate playerUpdate) {
        String res = "Player status=" + PlayerStatus.name(playerUpdate.status());
        res += "\n";
        res += toString(playerUpdate.player());
        return res;
    }

    public static String toString(ErrorMessage error) {
        String res = "Error code = " + ErrorCode.name(error.code()) + "\n";
        res += "Error message = " + error.error();
        return res;
    }
}
