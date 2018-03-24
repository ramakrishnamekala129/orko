package com.grahamcrockford.oco.web.auth;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.grahamcrockford.oco.OcoConfiguration;
import com.grahamcrockford.oco.api.auth.AuthConfiguration;
import com.grahamcrockford.oco.api.util.EnvironmentInitialiser;
import com.grahamcrockford.oco.web.WebResource;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;

public class AuthModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(GoogleAuthenticator.class).toInstance(
        new GoogleAuthenticator(
          new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().build()
        )
      );
    Multibinder.newSetBinder(binder(), WebResource.class).addBinding().to(AuthResource.class);
    Multibinder.newSetBinder(binder(), EnvironmentInitialiser.class).addBinding().to(AuthEnvironment.class);
  }

  @Provides
  AuthConfiguration authConfiguration(OcoConfiguration ocoConfiguration) {
    return ocoConfiguration.getAuth();
  }
}