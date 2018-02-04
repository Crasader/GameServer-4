package auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import info.UserInfo;

public class FakeLoginAuth implements LoginAuth{
    public String getLoginUserId(String idToken) throws Exception {
        return idToken;
    }

    @Override
    public UserInfo getUserRecord(String userid) throws Exception {
        UserInfo info = new UserInfo();
        info.setUserId(userid);
        info.setDisplayName("nghiaround");
        return info;
    }
}
