package auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import info.UserInfo;

public class FireBaseLoginAuth implements LoginAuth{
    public String getLoginUserId(String idToken) throws Exception {
        System.out.println("Debug idToken:" + idToken);
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdTokenAsync(idToken).get();
        return decodedToken.getUid();
    }

    public UserInfo getUserRecord(String userid) throws Exception {
        UserRecord record = FirebaseAuth.getInstance().getUserAsync(userid).get();
        UserInfo userInfo = new UserInfo();
        userInfo.setDisplayName(record.getDisplayName());
        System.out.println("Debug displayName=" + record.getDisplayName());
        userInfo.setUserId(record.getUid());
        return userInfo;
    }

}
