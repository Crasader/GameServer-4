package auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import info.UserInfo;

public class FireBaseLoginAuth implements LoginAuth{
    public String getLoginUserId(String idToken) throws Exception {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdTokenAsync(idToken).get();
        return decodedToken.getUid();
    }

    public UserInfo getUserRecord(String userid) throws Exception {
        UserRecord record = FirebaseAuth.getInstance().getUserAsync(userid).get();
        UserInfo userInfo = new UserInfo();
        userInfo.setDisplayName(record.getDisplayName());
        userInfo.setUserId(record.getUid());
        return userInfo;
    }

}
