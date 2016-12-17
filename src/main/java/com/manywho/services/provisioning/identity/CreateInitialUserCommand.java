package com.manywho.services.provisioning.identity;

import com.manywho.sdk.api.InvokeType;
import com.manywho.sdk.api.run.elements.config.ServiceRequest;
import com.manywho.sdk.services.actions.ActionCommand;
import com.manywho.sdk.services.actions.ActionResponse;
import com.manywho.services.provisioning.ServiceConfiguration;
import com.manywho.services.provisioning.factories.Sql2oFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.inject.Inject;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CreateInitialUserCommand implements ActionCommand<ServiceConfiguration, CreateInitialUser, CreateInitialUser.Input, Void> {
    private final Sql2oFactory sql2oFactory;

    @Inject
    public CreateInitialUserCommand(Sql2oFactory sql2oFactory) {
        this.sql2oFactory = sql2oFactory;
    }

    @Override
    public ActionResponse<Void> execute(ServiceConfiguration configuration, ServiceRequest request, CreateInitialUser.Input input) {
        Sql2o sql2o = sql2oFactory.create(input.getDatabaseName(), input.getDatabaseUsername(), input.getDatabasePassword());

        // Connect to database using the provided details
        try (Connection connection = sql2o.open()) {

            // Insert a new user, hashing the given password
            connection.createQuery("INSERT INTO Users (id, first_name, last_name, email, password, tenant_id, created_at, updated_at) VALUES (:id, :firstName, :lastName, :email, :password, :tenantId, :createdAt, :updatedAt)")
                    .addParameter("id", UUID.randomUUID())
                    .addParameter("firstName", input.getUserFirstName())
                    .addParameter("lastName", input.getUserLastName())
                    .addParameter("email", input.getUserEmail())
                    .addParameter("password", BCrypt.hashpw(input.getUserPassword(), BCrypt.gensalt()))
                    .addParameter("tenantId", input.getTenant())
                    .addParameter("createdAt", OffsetDateTime.now())
                    .addParameter("updatedAt", OffsetDateTime.now())
                    .executeUpdate();
        }

        return new ActionResponse<>(InvokeType.Forward);
    }
}
