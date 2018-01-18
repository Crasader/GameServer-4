package auth;

import com.google.inject.AbstractModule;

public class LocalAuthModule extends AbstractModule{
    @Override
    protected void configure() {
        bind(LoginAuth.class).to(FakeLoginAuth.class);
    }
}
