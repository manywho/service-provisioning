package com.manywho.services.provisioning.identity;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

import java.util.UUID;

@Action.Metadata(name = "Create Initial User", summary = "Create the initial user in the Identity Service", uri = "create-initial-user")
public class CreateInitialUser {

    public static class Input {
        @Action.Input(name = "Tenant ID", contentType = ContentType.String)
        private UUID tenant;

        @Action.Input(name = "Database Name", contentType = ContentType.String)
        private String databaseName;

        @Action.Input(name = "Database Username", contentType = ContentType.String)
        private String databaseUsername;

        @Action.Input(name = "Database Password", contentType = ContentType.String)
        private String databasePassword;

        @Action.Input(name = "User First Name", contentType = ContentType.String)
        private String userFirstName;

        @Action.Input(name = "User Last Name", contentType = ContentType.String)
        private String userLastName;

        @Action.Input(name = "User Email", contentType = ContentType.String)
        private String userEmail;

        @Action.Input(name = "User Password", contentType = ContentType.Password)
        private String userPassword;

        public UUID getTenant() {
            return tenant;
        }

        public String getDatabaseName() {
            return databaseName;
        }

        public String getDatabaseUsername() {
            return databaseUsername;
        }

        public String getDatabasePassword() {
            return databasePassword;
        }

        public String getUserFirstName() {
            return userFirstName;
        }

        public String getUserLastName() {
            return userLastName;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public String getUserPassword() {
            return userPassword;
        }
    }
}
