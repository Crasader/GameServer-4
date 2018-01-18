package auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

public class FakeLoginAuth implements LoginAuth{
    public String getLoginUserId(String idToken) throws Exception {
        return idToken;
    }
}
