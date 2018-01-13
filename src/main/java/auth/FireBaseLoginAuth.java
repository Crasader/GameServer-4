package auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

public class FireBaseLoginAuth implements LoginAuth{
    public String getLoginUserId(String idToken) throws Exception {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdTokenAsync(idToken).get();
        return decodedToken.getUid();
    }
}
