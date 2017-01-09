package com.manywho.services.provisioning.database;

import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.provisioning.ServiceConfiguration;
import com.manywho.services.provisioning.factories.Sql2oFactory;
import org.sql2o.Connection;

import javax.inject.Inject;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;

public class ProvisionDatabaseCommand implements ActionCommand<ServiceConfiguration, ProvisionDatabase, ProvisionDatabase.Input, ProvisionDatabase.Output> {
    private final Sql2oFactory sql2oFactory;
    private final SecureRandom secureRandom;

    @Inject
    public ProvisionDatabaseCommand(Sql2oFactory sql2oFactory, SecureRandom secureRandom) {
        this.sql2oFactory = sql2oFactory;
        this.secureRandom = secureRandom;
    }

    @Override
    public ActionResponse<ProvisionDatabase.Output> execute(ServiceConfiguration configuration, ServiceRequest request, ProvisionDatabase.Input input) {
        String databaseName = String.format("tenant_%s", input.getTenant());

        // Make sure a database for that tenant ID doesn't already exist
        try (Connection connection = sql2oFactory.create("postgres").open()) {
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

        try (Connection connection = sql2oFactory.create("postgres").open()) {
            // Create the user in the database
            connection.createQuery("CREATE USER " + username + " PASSWORD '" + password + "'")
                    .executeUpdate();

            // Add the current "superuser" as a member of the newly created role (this may only be needed for RDS)
            connection.createQuery("GRANT " + username + " TO current_user")
                    .executeUpdate();

            // Create a database for the tenant ID
            connection.createQuery("CREATE DATABASE \"" + databaseName + "\" OWNER " + username + " TEMPLATE template_tenant")
                    .executeUpdate();
        }

        try (Connection connection = sql2oFactory.create(databaseName).beginTransaction()) {
            // Reassign all the tables in the new database to the new user
            List<String> alterStatements = connection.createQuery("SELECT 'ALTER TABLE '|| schemaname || '.\"' || tablename ||'\" OWNER TO " + username + ";' FROM pg_tables WHERE NOT schemaname IN ('pg_catalog', 'information_schema') ORDER BY schemaname, tablename")
                    .executeAndFetch(String.class);

            for (String alterStatement : alterStatements) {
                connection.createQuery(alterStatement)
                        .executeUpdate();
            }

            // Remove public privileges from the database
            connection.createQuery("REVOKE ALL ON DATABASE \"" + databaseName + "\" FROM public")
                    .executeUpdate();

            // Allow the new user to actually use the stuff in the database
            connection.createQuery("GRANT ALL ON DATABASE \"" + databaseName + "\" TO " + username)
                    .executeUpdate();

            connection.createQuery("GRANT ALL ON SCHEMA public TO " + username)
                    .executeUpdate();

            connection.commit();
        }

        return new ActionResponse<>(new ProvisionDatabase.Output(databaseName, username, password));
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
