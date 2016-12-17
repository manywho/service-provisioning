package com.manywho.services.provisioning.database;

import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.services.actions.Action;

import java.util.UUID;

@Action.Metadata(name = "Provision Database", summary = "Provisions a database for a given tenant", uri = "provision-database")
public class ProvisionDatabase {

    public static class Input {
        @Action.Input(name = "Tenant ID", contentType = ContentType.String)
        private UUID tenant;

        public UUID getTenant() {
            return tenant;
        }
    }

    public static class Output {
        @Action.Output(name = "Database", contentType = ContentType.String)
        private String database;

        @Action.Output(name = "Username", contentType = ContentType.String)
        private String username;

        @Action.Output(name = "Password", contentType = ContentType.String)
        private String password;

        public Output() {
        }

        public Output(String database, String username, String password) {
            this.database = database;
            this.username = username;
            this.password = password;
        }

        public String getDatabase() {
            return database;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }
    }
}
