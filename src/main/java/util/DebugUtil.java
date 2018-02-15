package util;

import schema.PlayerInfo;
import schema.RoomInfo;

public class DebugUtil {
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
}
