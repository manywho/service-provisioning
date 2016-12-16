package com.manywho.services.provisioning.database;

import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.provisioning.ServiceConfiguration;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.inject.Inject;
import java.math.BigInteger;
import java.security.SecureRandom;

public class ProvisionDatabaseCommand implements ActionCommand<ServiceConfiguration, ProvisionDatabase, ProvisionDatabase.Input, ProvisionDatabase.Output> {
    private final Sql2o sql2o;
    private final SecureRandom secureRandom;

    @Inject
    public ProvisionDatabaseCommand(Sql2o sql2o, SecureRandom secureRandom) {
        this.sql2o = sql2o;
        this.secureRandom = secureRandom;
    }

    @Override
    public ActionResponse<ProvisionDatabase.Output> execute(ServiceConfiguration configuration, ServiceRequest request, ProvisionDatabase.Input input) {
        String databaseName = String.format("tenant_%s", input.getTenant());

        // Make sure a database for that tenant ID doesn't already exist
        try (Connection connection = sql2o.open()) {
            boolean databaseExists = connection.createQuery("SELECT EXISTS(SELECT 1 FROM pg_database WHERE datname = :name)")
                    .addParameter("name", databaseName)
                    .executeScalar(boolean.class);

            if (databaseExists) {
                throw new RuntimeException("A database already exists for the tenant with ID " + input.getTenant());
            }
        }

        // Generate a user (and password) for the tenant ID
        String username = createRandomString();
        String password = createRandomString();

        try (Connection connection = sql2o.open()) {
            // Create the user in the database
            connection.createQuery("CREATE USER " + username + " PASSWORD '" + password + "'")
                    .executeUpdate();

            // Create a database for the tenant ID
            connection.createQuery("CREATE DATABASE \"" + databaseName + "\" WITH OWNER " + username)
                    .executeUpdate();
        }

        return new ActionResponse<>(new ProvisionDatabase.Output(username, password));
    }

    private String createRandomString() {
        String string = new BigInteger(130, secureRandom).toString(32);

        // If the first character is a number then we can't use it for a username, so generate another one
        if (Character.isDigit(string.charAt(0))) {
            return createRandomString();
        }

        return string;
    }
}
