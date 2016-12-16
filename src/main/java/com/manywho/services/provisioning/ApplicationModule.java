package com.manywho.services.provisioning;

import com.google.inject.AbstractModule;
import com.manywho.services.provisioning.providers.SecureRandomProvider;
import com.manywho.services.provisioning.providers.Sql2oProvider;
import org.sql2o.Sql2o;

import java.security.SecureRandom;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(SecureRandom.class).toProvider(SecureRandomProvider.class).asEagerSingleton();
        bind(Sql2o.class).toProvider(Sql2oProvider.class);
    }
}
