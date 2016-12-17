package com.manywho.services.provisioning;

import com.google.inject.AbstractModule;
import com.manywho.services.provisioning.providers.SecureRandomProvider;

import java.security.SecureRandom;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SecureRandom.class).toProvider(SecureRandomProvider.class).asEagerSingleton();
    }
}
