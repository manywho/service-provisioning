package com.manywho.services.provisioning.factories;

import com.manywho.services.provisioning.ApplicationConfiguration;
import org.sql2o.Sql2o;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Sql2oFactory {
    private final ApplicationConfiguration configuration;

    @Inject
    public Sql2oFactory(ApplicationConfiguration configuration) {
        this.configuration = configuration;
    }

    public Sql2o create(String database) {
        return create(database, configuration.getDatabaseSuperuserUsername(), configuration.getDatabaseSuperuserPassword());
    }

    public Sql2o create(String database, String username, String password) {
        return new Sql2o(String.format("jdbc:postgresql://%s/%s", configuration.getDatabaseHostname(), database), username, password);
    }
}
